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

    // Maximum number of neighbors per person
    int maxNeighbors = 20

    // This is a temporary value designed to allow the new methods to approximate the outputs of the old ones
    double threshold = refinedThreshold/roughThreshold

    boolean transactional = true
    Map<Interest, List<InterestRelation>> similarInterests = [:]
    Map<Person, List<Neighbors>> neighbors = [:]

    def personService
    def interestService
    def collaboratorRequestService

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
    }


    public void buildAllInterestRelations() {
        TfIdfDistance tfIdf = calculateDocumentDistances()
        List<Interest> interests = Interest.findAll()
        int setSize = (int) interests.size()*roughThreshold
        interests.each({
            SortedSet<InterestRelation> newIR = new TreeSet<InterestRelation>()
            for(Interest i : interests){
                if(i != it){
                    double sim = calculatePairwiseSimilarity(it, i, tfIdf)
                    if (setSize > newIR.size() || sim > newIR.last().similarity) {
                        InterestRelation ir = new InterestRelation(first:it, second:i, similarity:sim)
                            newIR.add(ir)
                        if (setSize <= newIR.size()) {
                            newIR.pollLast()
                        }
                    }
                }
            }
            newIR.each({
                Utils.safeSave(it)
                InterestRelation ir=new InterestRelation(first:it.second, second:it.first, similarity:it.similarity)
                Utils.safeSave(ir)
            })
        })
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
        //Are the next four lines necessary?
        log.info("training on ${Document.count()} documents")
        for (Document d : Document.findAll()) {
            tfIdf.handle(d.text.toCharArray(), 0, d.text.length());  //what does this do??
        }
        for(Interest i : interests){
            if(i != interest){
                double sim = calculatePairwiseSimilarity(interest, i, tfIdf)
                if (setSize > newIR.size() || sim > newIR.last().similarity) {
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

    def analyze(BlacklistRelations bl) {
        calculateSimilarInterests(bl)
        calculateNeighbors()
    }

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
    }

    def calculateNeighbors() {
        neighbors.clear()
        log.error("building neighbors...")
        int numNeighbors = 0
        def neighborCounts = []

        for (Person p1: Person.findAll()) {
            log.error("looking for neighbors for " + p1)
            def personNeighbors = [:]

            for (Interest i: p1.interests) {
                log.debug("matching interest $i")
                // Add all neighbors with the same interest
                InterestRelation ir1 = new InterestRelation(first: i, second: i, similarity: 1.0)
                for (Person p2: personService.findByInterest(i)) {
                    log.debug("match for $i: $p2")
                    addSharedInterest(personNeighbors, p1, p2, ir1)
                }

                // Add all neighbors with similar interests
                def irs = getSimilarInterests(i)
                if (irs == null) {
                    continue
                }
                if (irs.size() > numSimsPerNeighbor) {
                    irs = irs[0..numSimsPerNeighbor]
                }
                for (InterestRelation ir2: irs) {
                    log.debug("matching interest ${ir2.second}")
                    for (Person p2: personService.findByInterest(ir2.second)) {
                        log.debug("match for ${ir2.second}: $p2")
                        addSharedInterest(personNeighbors, p1, p2, ir2)
                    }
                }
            }
            neighbors[p1] = new ArrayList(personNeighbors.values())
            neighbors[p1].sort({-1*it.sharedInterests.size()})
            if (neighbors[p1].size() > maxNeighbors) {
                neighbors[p1] = neighbors[p1].subList(0, maxNeighbors)
            }
            //Collections.shuffle(neighbors[p1])
            for (Neighbors n: neighbors[p1]) {
                n.sortInterests()
                numNeighbors += 1
                log.debug("adding neighbor: $n")
            }
            neighborCounts.add(personNeighbors.size())
        }

        double mean = numNeighbors / neighbors.size()
        double median = neighborCounts[(neighborCounts.size() / 2) as int]


        log.error("found $numNeighbors total neighbor relations (mean $mean, median $median)")

        println neighbors[Person.findByEmail("ssen@macalester.edu")]
    }

    private def addSharedInterest(Map<Person, Neighbors> neighbors, Person p1, Person p2, InterestRelation ir) {
        if (p1 == p2) {
            return
        }
        if (!neighbors.containsKey(p2)) {
            neighbors.put(p2, new Neighbors(first: p1, second: p2))
        }
        if (!neighbors.get(p2).sharedInterests.contains(ir)) {
            neighbors.get(p2).sharedInterests.add(ir)
        }
    }

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
        for(InterestRelation ir : getSimilarInterests(interest)){
            if (ir.similarity > absoluteThreshold && graph.getInterests().size() < maxInterests + 1) {
                graph.addEdge(new Edge(interest:interest, relatedInterest:ir.second))
                for(Person p : personService.findByInterest(ir.second)){
                    if(graph.getPeople().size() + graph.getRequests().size() < maxPeople  || graph.getPeople().contains(p)){
                        graph.addEdge(new Edge(person:p, interest:ir.second))
                    }
                }
                for(CollaboratorRequest cr : collaboratorRequestService.findByInterest(ir.second)){
                    if(graph.getPeople().size() + graph.getRequests().size() < maxPeople || graph.getRequests().contains(cr)){
                        graph.addEdge(new Edge(request: cr, interest:ir.second))
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
        for(InterestRelation ir : getSimilarInterests(i)){
            if(!inner.contains(ir.second) && ir.similarity > absoluteThreshold) {
                for(Person p : personService.findByInterest(ir.second)){
                    if(graph.getPeople().size() + graph.getRequests().size() < maxPeople || graph.getPeople().contains(p)){
                        graph.addEdge(new Edge(person:p, interest:i, relatedInterest:ir.second))
                    }
                }
                for (CollaboratorRequest cr : collaboratorRequestService.findByInterest(ir.second)) {
                    if(graph.getPeople().size() + graph.getRequests().size()  < maxPeople || graph.getRequests().contains(cr)){
                        graph.addEdge(new Edge(request: cr, interest:i, relatedInterest:ir.second))
                    }
                }
            }
        }
        return graph
    }


    /**
     * Returns a list of similar interests in the form of interest relations.
     * The first interest in every interest relation will be the interest requested.
     * The second will be the similar interest.
     * The list is sorted by similarity in descending.  The size of the list depends
     * upon the relative threshold defined inside, as well as the roughThreshold variable.
     *
     * @param i the interest for which similar interests are being obtained
     *
     */
    public List<InterestRelation> getSimilarInterests(Interest i) {
        List<InterestRelation> simInterests=InterestRelation.findAllByFirst(i, [sort:"similarity", order:"desc" ])
        int lastIndex=(simInterests.size() + 1)*threshold as int
        return simInterests.subList(0, lastIndex)
    }

    /**
     * Return a list of neighbors with an exactly shared interest or a similar interest.
     * Each neighbor contains
     *   a first person (the parameter p),
     *   a second person (the neighbor),
     *   a list of interest relations sorted by similarity, with:
     *          the first interest in the relation the interest for p
     *          the second interest in the relation the interest for the neighbor 
     */
    public List<Neighbors> getNeighbors(Person p) {
        return neighbors.get(p, [])
    }

    public Neighbors getNeighbors(Person p1, Person p2) {
        for (Neighbors n: getNeighbors(p2)) {
            if (n.second == p1) {
                return n
            }
        }
        return null
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
