package org.macademia


/**
 * The graph class contains two hashMaps, so that edges can be accessed by looking up either vertex.
 * The vertices are available in two parts: the set of Person vertices and the set of Interest vertices.
 * Edges of all types can be added using the addEdge method
 *
 * Authors: Nathaniel Miller and Alex Schneeman
 */
class Graph {
    Map<Interest,Set<Edge>> interestMap
    Map<Person, Set<Edge>> personMap
    Map<CollaboratorRequest, Set<Edge>> requestMap
    Map<Long, Interest> interestIdMap
    Map<Long, Person> personIdMap
    Map<Long, CollaboratorRequest> requestIdMap

    Map<Long, Double> personScores
    Map<Person, Set<Edge>> potentialPersonEdges

    int edges //needed for some unit tests

    /**
     *
     * @return an empty graph
     */
    public Graph() {
        interestMap=new HashMap<Interest, Set<Edge>>()
        personMap= new HashMap<Person, Set<Edge>>()
        requestMap= new HashMap<CollaboratorRequest, Set<Edge>>()
        interestIdMap = new HashMap<Long, Interest>()
        personIdMap = new HashMap<Long, Person>()
        requestIdMap = new HashMap<Long, CollaboratorRequest>()

        personScores = new HashMap<Long, Double>()
        potentialPersonEdges = new HashMap<Person, Set<Edge>>()

        edges = 0
    }

    /**
     * used to add edges to the graph
     * adding an edge also adds the nodes the edge connects
     * @param e an edge
     *
     */
    private void addEdge(Edge e) throws IllegalArgumentException {


        if (interestMap.containsKey(e.interest)) {
            if (!interestMap.get(e.interest).contains(e)) {
              edges++
            }
            interestMap.get(e.interest).add(e)
        } else {
            edges++
            HashSet<Edge> set = new HashSet()
            set.add(e)
            interestMap.put(e.interest, set)
        }

        //edges may either be interest-person edges, interest-collaborator
        // request edges, or interest-interest edges.
        //as of now no other types of edges should exist
        if (e.person != null) {
            if (potentialPersonEdges.containsKey(e.person)) {
                potentialPersonEdges.get(e.person).add(e)
            } else {
                HashSet<Edge> set = new HashSet()
                set.add(e)
                potentialPersonEdges.put(e.person, set)
            }
        } else if (e.request != null){
                    if (requestMap.containsKey(e.request)) {
                        requestMap.get(e.request).add(e)
                    } else {
                        HashSet<Edge> set = new HashSet()
                        set.add(e)
                        requestMap.put(e.request, set)
                    }
        } else if (e.relatedInterest != null) {
            if (interestMap.containsKey(e.relatedInterest)) {
                interestMap.get(e.relatedInterest).add(e)
            } else {
                HashSet<Edge> set = new HashSet()
                set.add(e)
                interestMap.put(e.relatedInterest, set)
            }
        } else  {
            throw new IllegalArgumentException("Second Vertex Needed")
        }

    }

    public void addEdge(Long personId, Long interestId, Long relatedInterestId, Long requestId) {
        Interest interest
        Person person = null
        CollaboratorRequest request = null
        Interest relatedInterest = null
        if (interestIdMap.containsKey(interestId)) {
            interest = interestIdMap.get(interestId)
        } else if (interestId != null) {
            interest = Interest.get(interestId)
            interestIdMap.put(interestId, interest)
        } else {
            return
        }

        if (personId != null) {
            if (personIdMap.containsKey(personId)) {
                person = personIdMap.get(personId)
            } else {
                person = Person.get(personId)
                personIdMap.put(personId, person)
            }
        } else if (requestId != null) {
            if (requestIdMap.containsKey(requestId)) {
                request = requestIdMap.get(requestId)
            } else {
                request = CollaboratorRequest.get(requestId)
                requestIdMap.put(requestId, request)
            }
        } else if (relatedInterestId == null) {
            throw new IllegalArgumentException("Second Vertex Needed")
        }

        if (relatedInterestId != null) {
            if (interestIdMap.containsKey(relatedInterestId)) {
                relatedInterest = interestIdMap.get(relatedInterestId)
            } else {
                relatedInterest = Interest.get(relatedInterestId)
                interestIdMap.put(relatedInterestId, relatedInterest)
            }
        }

        int nullCount =0
        if(interest==null){
            nullCount++
        }
        if(person== null){
            nullCount++
        }
        if(request == null){
            nullCount++
        }
        if(relatedInterest == null){
            nullCount++
        }
        if(nullCount>=3){
            return
        }
        addEdge(new Edge(interest: interest, person: person, request: request, relatedInterest: relatedInterest))
    }

    /**
     * Increments the score of a person
     * @param pid The id of the person whose score should be incremented
     * @param sim The increment value to add to the score associated with
     * the person specified by parameter pid
     */
    public void incrementPersonScore(Long pid, Double sim) {
        if (!personScores.containsKey(pid)) {
            personScores.put(pid, 0)
        }
        personScores.put(pid, personScores.get(pid) + sim)
    }

    /**
     * Final step to creating the graph. Sorts the people by relevance, and
     * then adds the most relevant people to the graph.
     * @param maxPeople The number of people to add the the graph
     */
    public void finalizeGraph(int maxPeople) {
        PersonScoreComparator psc = new PersonScoreComparator(personScores)
        Map<Long, Double> sortedPersonScores = new TreeMap<Long, Double>(psc)
        sortedPersonScores.putAll(personScores)
        
        if (personMap.keySet().size() != 0) {
            personMap.clear()
        }
        int peopleAdded = 0
        //System.out.println("Size of sortedPersonScores: " + sortedPersonScores.keySet().size())
        for (Long pid : sortedPersonScores.keySet()) {
            Person p = personIdMap.get(pid)
            personMap.put(p, potentialPersonEdges.get(p))
            //System.out.println(peopleAdded + " person id: " + pid + " sim: " + sortedPersonScores.get(pid))
            peopleAdded++
            if (peopleAdded >= (maxPeople + 1)) {
                return
            }
        }
    }

    /**
     *
     * @param person the person whose adjacent edges are needed
     * @return edges with the input person as one vertex
     */
    public Set<Edge> getAdjacentEdges (Person person) {
        personMap.get(person)
    }

    /**
     *
     * @param interest the interest for which adjacent edges are needed
     * @return edges with the input interest as one vertex.
     */
    public Set<Edge> getAdjacentEdges (Interest interest) {
        interestMap.get(interest)
    }


    /**
     *
     * @param interest the collaborator request for which adjacent edges are needed
     * @return edges with the input collaborator request as one vertex.
     */
    public Set<Edge> getAdjacentEdges (CollaboratorRequest request) {
        requestMap.get(request)
    }

    /**
     *
     * @return the set of people who are vertices in the graph
     */
    public Set<Person> getPeople() {
        return personMap.keySet()
    }

    /**
     *
     * @return the set of interests that are vertices in the graph
     */
    public Set<Interest> getInterests() {
        return interestMap.keySet()
    }

    public Set<CollaboratorRequest> getRequests(){
        return requestMap.keySet()
    }

    /**
     *
     * @param person checks whether this person is in the graph
     * @return returns true if the person is in the graph
     */
    public boolean containsNode(Person person){
        return personMap.containsKey(person)
    }

    /**
     *
     * @param interest checks whether this interest is in the graph
     * @return returns true if the interest is in the graph
     */
    public boolean containsNode(Interest interest){
        return interestMap.containsKey(interest)
    }

    public boolean containsNode(CollaboratorRequest request){
        return requestMap.containsKey(request)
    }


    /**
     *
     * @return the number of edges between the people and interests in the graph
     */
    public int edgeSize(){
        return edges
    }

    public boolean containsInterestId(long id) {
        return interestIdMap.containsKey(id)
    }

    public boolean containsPersonId(long id) {
        return personIdMap.containsKey(id)
    }

    public boolean containsRequestId(long id) {
        return requestIdMap.containsKey(id)
    }
    /*
    public boolean containsEdge(Person person, Interest interest){
        Edge edge=new Edge(person:person, interest:interest)
        if(interestMap.containsKey(interest) && personMap.containsKey(Person)){
            return interestMap.get(person).contains(edge)
        }

        return false
    }

    public boolean containsEdge(Interest interest1, Interest interest2){
        Edge edge=new Edge(interest:interest1, relatedInterest:interest2)
        if(interestMap.containsKey(interest1) && interestMap.containsKey(interest2)){
            return interestMap.get(interest1).contains(edge)
        }
        return false
    }*/


    /**
     * Comparator for use in sorting the list of personScores
     */
    class PersonScoreComparator implements Comparator {
        Map<Long, Double> base

        /**
         * @param base The Map<Long, Double> personScores in need of sorting
         */
        public PersonScoreComparator(Map<Long, Double> base) {
            this.base = base
        }

        public int compare(object1, object2) {
            if((Double)base.get(object1) < (Double)base.get(object2)) {
                return 1;
            } else if((Double)base.get(object1) == (Double)base.get(object2)) {
                return 0;
            } else {
                return -1;
            }
        }

    }
}
