package org.macademia

import com.aliasi.tokenizer.IndoEuropeanTokenizerFactory
import com.aliasi.tokenizer.TokenizerFactory
import com.aliasi.spell.TfIdfDistance
import org.codehaus.groovy.grails.commons.ConfigurationHolder

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
    double roughThreshold = ConfigurationHolder.config.roughThreshold

    // Only similarities in the top fraction are used to fill out interests
    double refinedThreshold = ConfigurationHolder.config.refinedThreshold

    // The lowest possible acceptable similarity score
    double absoluteThreshold = ConfigurationHolder.config.absoluteThreshold

    // This is a temporary value designed to allow the new methods to approximate the outputs of the old ones
    double threshold = refinedThreshold/roughThreshold

    //Has buildInterestRelations() been run?
    boolean relationsBuilt = true

    boolean transactional = true

    TimingAnalysis timing = new TimingAnalysis()

    def databaseService
    def sessionFactory

   public void buildInterestRelations() {
        for (Interest interest : Interest.findAll()) {
            log.info("interest Id is $interest.id related article is $interest.articleId and relations Built is $relationsBuilt")
            if(!(interest.articleId==null || interest.articleId<0)){
                databaseService.buildInterestRelations(interest.id, interest.articleId, relationsBuilt)
            }  else{
                log.info("Interest $interest has no related article")
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
            SimilarInterestList list = new SimilarInterestList()
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
                    if (setSize > list.size() || sim > list.get(list.size()-1).similarity) {
                        SimilarInterest s = new SimilarInterest(id, sim)
                        list.add(s)
                        if (setSize <= list.size()) {
                            list.removeLowest()
                        }
                    }
                }
                if (count % 50 == 0) {
                    //log.info("Cleaning up Gorm at interest relation $count for interest ${i}")
                    log.info("Cleaning up Gorm at interest relation $count")
                    Utils.cleanUpGorm(sessionFactory)
                }
            }
            long intId = it
            for (SimilarInterest s : list.list){
                databaseService.addToInterests(intId, s.interestId, s.similarity)
                //we could use this to do a lot less work later (some interest relations would be precomputed)
                //InterestRelation ir=new InterestRelation(first:it.second, second:it.first, similarity:it.similarity)
                //Utils.safeSave(ir)
            }
            Utils.cleanUpGorm(sessionFactory)
        })
        relationsBuilt = true
    }

   /**
    *
    * @param i
    */
    public void buildInterestRelations(Interest interest){
        if ((interest.articleId != null) && (interest.articleId > 0)) {
            databaseService.buildInterestRelations(interest.id, interest.articleId, relationsBuilt)
        } else {
            log.info("Interest $interest has no related article")
        }
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
        return calculateInterestNeighbors(interest, maxPeople, maxInterests, null)
    }

    public Graph calculateInterestNeighbors(Interest interest, int maxPeople, int maxInterests, Set<Long> institutionFilter){
        Graph graph = new Graph()
        //interestCache = new HashMap<Long, Interest>()
        //adds the edges to the set between people who have the central interest
        graph = findPeopleAndRequests(graph, maxPeople, interest.id, null, institutionFilter)
        //loops over the InterestRelations representing similar interests, then looks for people with similar interests
        //SimilarInterestList similarInterests = getSimilarInterests(interest, maxSimsPerInterest, absoluteThreshold)
        for(SimilarInterest ir : getSimilarInterests(interest.id, maxSimsPerInterest, absoluteThreshold, institutionFilter)){
            //log.info("Similar interest ID: "+ir.interestId+" similarity score "+ir.similarity)
            if (graph.getInterests().size() < maxInterests + 1) {
                graph.addEdge(null, interest.id, ir.interestId, null)
                graph = findPeopleAndRequests(graph, maxPeople, ir.interestId, null, institutionFilter)
            }
        }
        //interestCache = new HashMap<Long, Interest>()
        return graph
    }

    /**
     * Finds the local graph given a person to go at the center and a maximum number of people
     * to go in the graph.
     * @param person the person at the center of the graph
     * @param maxPeople the maximum number of people in the graph
     * @return A graph that contains the nodes and edges to them of the neighbor of the passed person
     */
    public Graph calculatePersonNeighbors(Person person, int maxPeople) {
        return calculatePersonNeighbors(person, maxPeople, null)
    }


    public Graph calculatePersonNeighbors( Person person, int maxPeople, Set<Long> institutionFilter){
        Graph graph= new Graph()
        //loops over the central person's interests, adds people who have those interests, then people who have interests
        //similar to the central person's interests
        long graphStart = Calendar.getInstance().getTimeInMillis()
        for(long i : databaseService.getUserInterests(person.id)){
            graph = calculateNeighbors(i, graph, maxPeople, (Set<Long>)person.interests.collect({it.id}), institutionFilter)
        }
        long graphEnd =Calendar.getInstance().getTimeInMillis()
        long graphTime=graphEnd-graphStart
        log.info("It took $graphTime to build $person graph")
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
        return calculateRequestNeighbors(request, maxPeople, null)
    }

    public Graph calculateRequestNeighbors(CollaboratorRequest request, int maxPeople, Set<Long> institutionFilter) {
        Graph graph = new Graph()
        //loops over the collaborator request's interests, adds people who have those interests, then people who have interests
        //similar to the requests's interests
        for (long i : databaseService.getRequestKeywords(request.id)) {
             graph = calculateNeighbors(i, graph, maxPeople, (Set<Long>)request.keywords.collect({it.id}), institutionFilter)
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
    public Graph calculateNeighbors(Long i, Graph graph, int maxPeople, Set<Long> inner, Set<Long> institutionFilter) {
        if(i == null){
            return graph
        }
        timing.startTime()
        graph = findPeopleAndRequests(graph, maxPeople, i, null, institutionFilter)
        for(SimilarInterest ir : getSimilarInterests(i, maxSimsPerInterest, absoluteThreshold, institutionFilter)){
            //log.info("Similar interest ID: "+ir.interestId+" similarity score "+ir.similarity+"calculate neighbors")
            if(ir.interestId!=null){
                if(!inner.contains(ir.interestId)) {
                    graph = findPeopleAndRequests(graph, maxPeople, i, ir.interestId, institutionFilter)
                }
            }

        }
        return graph
    }

    public Graph findPeopleAndRequests(Graph graph, int maxPeople, Long i, Long ir, Set<Long> institutionFilter) {
        timing.startTime()
        Long foo
        if (ir == null) {
            foo = i
        } else {
            foo = ir
        }
        timing.recordTime("find People And Requests overhead")
        for(long p : databaseService.getInterestUsers(foo)){
            if(graph.getPeople().size() + graph.getRequests().size() < maxPeople || graph.containsPersonId(p)){
                timing.startTime()
                if (institutionFilter == null) {
                    graph.addEdge(p, i, ir, null)
                    timing.recordTime("Adding edge without Institution Filter")
                } else {
                    if (institutionFilter.contains(databaseService.getUserInstitution(p))) {
                        graph.addEdge(p, i, ir, null)
                    }
                    timing.recordTime("Adding edge with Institution Filter overhead")
                }
            }
        }
        for (long cr : databaseService.getInterestRequests(foo)) {
            if(graph.getPeople().size() + graph.getRequests().size()  < maxPeople || graph.containsRequestId(cr)){
                timing.startTime()
                if (institutionFilter == null) {
                    graph.addEdge(null, i, ir, cr)
                    timing.recordTime("Adding edge without Institution Filter")
                } else {
                    if (institutionFilter.contains(databaseService.getCollaboratorRequestInstitution(cr))) {
                        graph.addEdge(null, i, ir, cr)
                    }
                    timing.recordTime("Adding edge with Institution Filter overhead")
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
        return getSimilarInterests(i.id, maxSimsPerInterest, absoluteThreshold, null);
    }

    public SimilarInterestList getSimilarInterests(Interest i, Set<Long> institutionFilter) {
        return getSimilarInterests(i.id, maxSimsPerInterest, absoluteThreshold, institutionFilter)
    }

    public SimilarInterestList getSimilarInterests(Interest i , int max , double similarityThreshold) {
        return getSimilarInterests(i.id, max, similarityThreshold, null)
    }

    public SimilarInterestList getSimilarInterests(Long i , int max , double similarityThreshold) {
        return getSimilarInterests(i, max, similarityThreshold, null)
    }

    public SimilarInterestList getSimilarInterests(Long i , int max , double similarityThreshold, Set<Long> institutionFilter) {
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

    public void analyzeTimes() {
        timing.analyze()
    }

}
