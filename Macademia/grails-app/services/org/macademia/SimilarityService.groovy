package org.macademia

import com.aliasi.tokenizer.IndoEuropeanTokenizerFactory
import com.aliasi.tokenizer.TokenizerFactory
import com.aliasi.spell.TfIdfDistance

class SimilarityService {

    // The maximum number of similar interests per interest
    int numSimsPerInterest = 8

    // The maximum number of similar interests per neighbors
    int numSimsPerNeighbor = 10

    // The minimum number of similar interests per interest
    int minSimsPerInterest = 2

    // The maximum number of times to use a particular interest as a partner for other interests
    int maxSimsPerInterest = 10

    // Only similarities in the top fraction are stored in the database.
    double roughThreshold = 0.15

    // Only similarities in the top fraction are used to fill out interests
    double refinedThreshold = 0.08

    // The lowest possible acceptable similarity score
    double absoluteThreshold = 0.15

    // This is a temporary value designed to allow the new methods to approximate the outputs of the old ones
    double threshold = refinedThreshold/roughThreshold

    //Has buildInterestRelations() been run?
    boolean relationsBuilt = false

    boolean transactional = true
    Map<Interest, List<InterestRelation>> similarInterests = [:]

    def personService
    def interestService
    def collaboratorRequestService
    def databaseService

    def buildInterestRelations() {
        log.info("deleting existing interest relations")
        for (InterestRelation ir : InterestRelation.findAll()) {
            ir.delete(flush : true)
        }

        log.info("building similarities for ${Interest.count()} interests")

        TokenizerFactory tokenizerFactory = IndoEuropeanTokenizerFactory.INSTANCE;
        TfIdfDistance tfIdf = new TfIdfDistance(tokenizerFactory);
        log.info("training on ${Document.count()} documents")
        for (Document d : Document.findAll()) {
            tfIdf.handle(d.text.toCharArray(), 0, d.text.length());
        }
        int counter = 0
        List<Interest> interests = Interest.findAll()

        for (Interest i : interests) {
            log.info("calculating all similarities for ${i}")
            List<InterestRelation> relations = []
            for (Interest j : interests) {
                if (!i.equals(j)) {
                    double sim = calculatePairwiseSimilarity(i, j, tfIdf)
                    InterestRelation ir = new InterestRelation(first : i, second: j, similarity : sim)
                    relations.add(ir)
//                    println("${sim}\t${i}\t${j}")
                }
            }
            relations = relations.sort()
            for (InterestRelation ir : relations[0..((roughThreshold * relations.size())-1 as int)]) {
                Utils.safeSave(ir)
            }
            if (((++counter) % 10) == 0) {
                cleanUpGorm()
            }
        }
        relationsBuilt = true
    }


    public void buildAllInterestRelations() {
        TfIdfDistance tfIdf = calculateDocumentDistances()
        List<Long> intIds = Interest.findAll().collect{it.id}
        int setSize = (int) intIds.size()*roughThreshold
        HashMap<Long,String> docMap = new HashMap<Long,String>()
        intIds.each({
            Document doc = Interest.get(it).findMostRelevantDocument()
            if (doc != null) {
            String text = doc.text
            docMap.put(it, text)
            }
        })
        intIds.each({
            String text = docMap.get(it)
            //log.info("calculating all similarities for ${i}")
            log.info("calculating all similarities for $it")
            SortedSet<InterestRelation> newIR = new TreeSet<InterestRelation>()
            int count = 0
            for(Long id : intIds){
                count++
                String comp = docMap.get(id)
                if(it != id){
                    double sim
                    if (comp == null || text == null) {
                        sim = -1.0
                    } else {
                        sim = tfIdf.proximity(comp, text)
                    }
                    //we could check for precomputed relations here
                    if (setSize > newIR.size() || sim > newIR.last().similarity) {
                        InterestRelation ir = new InterestRelation(first:Interest.get(it), second:Interest.get(id), similarity:sim)
                        newIR.add(ir)
                        if (setSize <= newIR.size()) {
                            newIR.pollLast()
                        }
                    }
                }
                if (count % 50 == 0) {
                    //log.info("Cleaning up Gorm at interest relation $count for interest ${i}")
                    log.info("Cleaning up Gorm at interest relation $count")
                    cleanUpGorm()
                }
            }
            newIR.each({
                Utils.safeSave(it)
                databaseService.addToInterests(it.first, it.second, it.similarity)
                //we could use this to do a lot less work later (some interest relations would be precomputed)
                //InterestRelation ir=new InterestRelation(first:it.second, second:it.first, similarity:it.similarity)
                //Utils.safeSave(ir)
            })
            cleanUpGorm()
        })
        relationsBuilt = true
    }

   /**
    *
    * @param i
    */
    public void buildInterestRelations(Interest interest){
        List<Interest> interests = Interest.findAll()
        int setSize = (int) interests.size()*roughThreshold
        SortedSet<InterestRelation> newIR = new TreeSet<InterestRelation>()
        TokenizerFactory tokenizerFactory = IndoEuropeanTokenizerFactory.INSTANCE;
        TfIdfDistance tfIdf = new TfIdfDistance(tokenizerFactory);
        Double lastSim
        //Are the next four lines necessary?
        log.info("training on ${Document.count()} documents")
        for (Document d : Document.findAll()) {
            tfIdf.handle(d.text.toCharArray(), 0, d.text.length());  //what does this do??
        }
        for(Interest i : interests){
            //log.info("building interest relations for ${i}")
            if(i != interest){
                double sim = calculatePairwiseSimilarity(interest, i, tfIdf)
                if (newIR.size() > 0) {
                    lastSim = newIR.last().similarity
                } else {
                    lastSim = 0
                }
                if (setSize > newIR.size() || sim > lastSim) {
                    InterestRelation ir = new InterestRelation(first:interest, second:i, similarity:sim)
                    newIR.add(ir)
                    if (setSize < newIR.size()) {
                        newIR.pollLast()
                    }
                }
            }
        }
        newIR.each({
            Utils.safeSave(it)
            InterestRelation ir=new InterestRelation(first:it.second, second:it.first, similarity:it.similarity)
            Utils.safeSave(ir)
            databaseService.addToInterests(it.first, it.second, it.similarity)
            databaseService.addToInterests(ir.first, ir.second, ir.similarity)
        })
    }

    public TfIdfDistance calculateDocumentDistances() {
        TokenizerFactory tokenizerFactory = IndoEuropeanTokenizerFactory.INSTANCE;
        TfIdfDistance tfIdf = new TfIdfDistance(tokenizerFactory);
        //Are the next four lines necessary?
        log.info("training on ${Document.count()} documents")
        for (Document d : Document.findAll()) {
            tfIdf.handle(d.text.toCharArray(), 0, d.text.length());
        }
        return tfIdf
    }

    def calculatePairwiseSimilarity(Interest i1, Interest i2, TfIdfDistance tfIdf) {
        //Random rand = new Random()
        Document d1 = i1.findMostRelevantDocument()
        Document d2 = i2.findMostRelevantDocument()
        if (d1 == null || d2 == null) {
            return -1.0
        } else {
            return tfIdf.proximity(d1.text, d2.text)
        }
//        double simSum = 0
//        double weightSum = 0
//
//        for (InterestDocument id1 : i1.documents) {
//            for (InterestDocument id2 : i2.documents) {
////                println("comparing ${id1.document.url} and ${id2.document.url}")
//                double w = id1.weight * id2.weight
//                weightSum += w
//                simSum += w * tfIdf.proximity(id1.document.text, id2.document.text)
//            }
//        }
//
//        return 1.0 * simSum / weightSum
    }

    //not used
    /*def analyze(BlacklistRelations bl) {
        calculateSimilarInterests(bl)
        calculateNeighbors()
    }

    //not used
    def calculateSimilarInterests(BlacklistRelations bl) {
        log.error("building similar interests...")

        // initialization
        Collection<InterestRelation> all = InterestRelation.findAll()
        all.sort({ it.similarity })
        all = all.reverse()

        def counts = [:]
        def used = new HashSet()
        similarInterests.clear()

        // first pass: give every interest one partner at a time
        // to reach the minSimsPerInterest
        // but don't exceed maxSimsPerInterest
        for (int i: 1..minSimsPerInterest) {
            for (InterestRelation ir: all) {
                if ((!used.contains(ir))
                && (bl == null || bl.isRetained(ir))
                && (similarInterests.get(ir.first, []).size() < minSimsPerInterest)
                && (counts.get(ir.second, 0) < maxSimsPerInterest)) {
                    counts[ir.second] = counts.get(ir.second, 0) + 1
                    if (similarInterests.containsKey(ir.first)) {
                        similarInterests[ir.first].add(ir)
                    } else {
                        similarInterests[ir.first] = [ir]
                    }
                    used.add(ir)
                }
            }
        }
        // second pass: try to fill in the remaining
        // but don't use any similarities that are too low
        def maxIndex = all.size() * refinedThreshold as int
        for (InterestRelation ir: all[0..maxIndex]) {
            if ((!used.contains(ir))
            && (bl == null || bl.isRetained(ir))
            && (similarInterests[ir.first].size() < numSimsPerInterest)
            && (counts.get(ir.second, 0) < maxSimsPerInterest)) {
                counts[ir.second] = counts.get(ir.second, 0) + 1
                similarInterests[ir.first].add(ir)
                used.add(ir)
            }
        }
//        for (Interest i : similarInterests.keySet()) {
//            println "interests similar to '${i.text}'"
//            for (InterestRelation ir : similarInterests[i]) {
//                println "\t${ir.similarity}\t${ir.second.text}"
//            }
//        }
    }  */

    /**
     * Finds the local graph given an interest to go at the center and a maximum number of people
     * to go in the graph.
     *
     * @param interest the interest to go at the center of the graph
     * @param maxPeople the maximum number of people to go in the graph
     * @param maxInterests the maximum number of interests to go in the graph
     * @return A graph that contains the nodes and edges to them of the neighbor of the passed interest
     */
    public Graph calculateInterestNeighbors(Interest interest, int maxPeople, int maxInterests){
        Graph graph = new Graph()
        //adds the edges to the set between people who have the central interest
        for(Person p : personService.findByInterest(interest)){
            if(graph.getPeople().size() + graph.getRequests().size() < maxPeople || graph.getPeople().contains(p)){
                graph.addEdge(new Edge(person:p, interest:interest))
            }
        }
        for(CollaboratorRequest cr : collaboratorRequestService.findByInterest(interest)){
            if(graph.getPeople().size() + graph.getRequests().size() < maxPeople || graph.getRequests().contains(cr)){
                graph.addEdge(new Edge(request: cr, interest:interest))
            }
        }
        //loops over the InterestRelations representing similar interests, then looks for people with similar interests
        for(SimilarInterest ir : getSimilarInterests(interest, maxSimsPerInterest, absoluteThreshold)){
            //log.info("Similar interest ID: "+ir.interestId+" similarity score "+ir.similarity)
            if (graph.getInterests().size() < maxInterests + 1) {
                Interest second=Interest.findById(ir.interestId)
                if(second !=null){
                    graph.addEdge(new Edge(interest:interest, relatedInterest:second))
                    for(Person p : personService.findByInterest(second)){
                        if(graph.getPeople().size() + graph.getRequests().size() < maxPeople  || graph.getPeople().contains(p)){
                            graph.addEdge(new Edge(person:p, interest:second))
                        }
                    }
                    for(CollaboratorRequest cr : collaboratorRequestService.findByInterest(second)){
                        if(graph.getPeople().size() + graph.getRequests().size() < maxPeople || graph.getRequests().contains(cr)){
                            graph.addEdge(new Edge(request: cr, interest:second))
                        }
                    }
                }
            }
        }
        return graph
    }

    /**
     * Finds the local graph given a person to go at the center and a maximum number of people
     * to go in the graph.
     * @param person the person at the center of the graph
     * @param maxPeople the maximum number of people in the graph
     * @return A graph that contains the nodes and edges to them of the neighbor of the passed person
     */
    public Graph calculatePersonNeighbors( Person person, int maxPeople){
        Graph graph= new Graph()
        //loops over the central person's interests, adds people who have those interests, then people who have interests
        //similar to the central person's interests
        for(Interest i : person.interests){
            graph = calculateNeighbors(i, graph, maxPeople, person.interests)
        }
        return graph
    }


   /**
    * Should we link requests to other requests?
    *
    * Finds the local graph given a collaboration request to go at the center and a maximum number of
    * people to go in the graph
    * @param request   the collaborator request that goes at the center
    * @param maxPeople the maximum number of people to go in the graph
    * @return the local graph centered at the input CollaboratorRequest
    */
    public Graph calculateRequestNeighbors(CollaboratorRequest request, int maxPeople) {
        Graph graph = new Graph()
        //loops over the collaborator request's interests, adds people who have those interests, then people who have interests
        //similar to the requests's interests
        for (Interest i : request.keywords) {
             graph = calculateNeighbors(i, graph, maxPeople, request.keywords)
        }
        return graph
    }


   /**
    * Finds the branches off of an interest node in a graph centered on a request or a person
    * @param i
    * @param graph
    * @param maxPeople
    * @param inner the interests that should be on the inner ring
    * @return
    */
    public Graph calculateNeighbors(Interest i, Graph graph, int maxPeople, Set<Interest> inner) {
        for(Person p : personService.findByInterest(i)){
            if(graph.getPeople().size() + graph.getRequests().size() < maxPeople || graph.getPeople().contains(p)){
                graph.addEdge(new Edge(person:p, interest:i))
            }
        }
        for(CollaboratorRequest cr : collaboratorRequestService.findByInterest(i)) {
            if(graph.getPeople().size() + graph.getRequests().size() < maxPeople || graph.getRequests().contains(cr)) {
                graph.addEdge(new Edge(request: cr, interest: i))
            }
        }
        for(SimilarInterest ir : getSimilarInterests(i, maxSimsPerInterest, absoluteThreshold)){
            //log.info("Similar interest ID: "+ir.interestId+" similarity score "+ir.similarity+"calculate neighbors")
            if(ir.interestId!=null){
                Interest second = Interest.findById(ir.interestId)
                if(!inner.contains(second) && (second!=null)) {
                    for(Person p : personService.findByInterest(second)){
                        if(graph.getPeople().size() + graph.getRequests().size() < maxPeople || graph.getPeople().contains(p)){
                            graph.addEdge(new Edge(person:p, interest:i, relatedInterest:second))
                        }
                    }
                    for (CollaboratorRequest cr : collaboratorRequestService.findByInterest(second)) {
                        if(graph.getPeople().size() + graph.getRequests().size()  < maxPeople || graph.getRequests().contains(cr)){
                            graph.addEdge(new Edge(request: cr, interest:i, relatedInterest:second))
                        }
                    }
                }
            }
            Interest second = Interest.findById(ir.interestId)
            if(!inner.contains(second) && (second!=null)) {
                for(Person p : personService.findByInterest(second)){
                    if(graph.getPeople().size() + graph.getRequests().size() < maxPeople || graph.getPeople().contains(p)){
                        graph.addEdge(new Edge(person:p, interest:i, relatedInterest:second))
                    }
                }
                for (CollaboratorRequest cr : collaboratorRequestService.findByInterest(second)) {
                    if(graph.getPeople().size() + graph.getRequests().size()  < maxPeople || graph.getRequests().contains(cr)){
                        graph.addEdge(new Edge(request: cr, interest:i, relatedInterest:second))
                    }
                }
            }
        }
        return graph
    }


    /*
     * Returns a list of similar interests in the form of interest relations.
     * The first interest in every interest relation will be the interest requested.
     * The second will be the similar interest.
     * The list is sorted by similarity in descending.  The size of the list depends
     * upon the relative threshold defined inside, as well as the roughThreshold variable.
     *
     * @param i the interest for which similar interests are being obtained
     *
     */
    public SimilarInterestList getSimilarInterests(Interest i) {
        return getSimilarInterests(i, maxSimsPerInterest, absoluteThreshold, null);
    }

    public SimilarInterestList getSimilarInterests(Interest i, Set<Long> institutionFilter) {
        return getSimilarInterests(i, maxSimsPerInterest, absoluteThreshold, institutionFilter)
    }

    public SimilarInterestList getSimilarInterests(Interest i , int max , double similarityThreshold) {
        return getSimilarInterests(i, max, similarityThreshold, null)
    }

    public SimilarInterestList getSimilarInterests(Interest i , int max , double similarityThreshold, Set<Long> institutionFilter) {
        SimilarInterestList sims
        if (institutionFilter == null) {
            sims=databaseService.getSimilarInterests(i)
        } else {
            sims=databaseService.getSimilarInterests(i, institutionFilter)
        }
        if (sims.size() == 0) {
            return sims
        }
        int j = 0
        if (sims.get(0).similarity < similarityThreshold) {
            return new SimilarInterestList()
        }
        while (j < max && j < sims.size()) {
            if (sims.get(j).similarity > similarityThreshold) {
                j++
            } else {
                break
            }
        }
        return sims.getSublistTo(j)
    }

    def sessionFactory
    def propertyInstanceMap = org.codehaus.groovy.grails.plugins.DomainClassGrailsPlugin.PROPERTY_INSTANCE_MAP
    def cleanUpGorm() {
        def session = sessionFactory.currentSession
        session.flush()
        session.clear()
        propertyInstanceMap.get().clear()
    }
}
