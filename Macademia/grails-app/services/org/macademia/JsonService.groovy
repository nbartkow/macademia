package org.macademia

import grails.converters.JSON
import java.awt.Color

class JsonService {

      //max number of people (and collaboration requests) per person-centric (and collaboration-centric) graph
    int DEFAULT_MAX_NEIGHBORS_PERSON_CENTRIC = 25

      //max number of people (and collaboration requests) per interest-centric graph
    int DEFAULT_MAX_NEIGHBORS_INTEREST_CENTRIC = 25

      //max number of interests per interest-centric graph
    int DEFAULT_MAX_INTERESTS_INTEREST_CENTRIC = 25

    boolean transactional = true

    SimilarityService similarityService
    InterestService interestService
    PersonService personService
    CollaboratorRequestService collaboratorRequestService

    def parseJsonToGroovy(json) {
        def data = JSON.parse(json)
        // println("recieved JSON: "+data)
        Map<Long, Object> personNodes = [:]
        Map<Long, Object> interestNodes = [:]
        Map<Long, Object> collaboratorRequestNodes = [:]
        def splitId

        //rebuilding the separate personNodes, collaboratorRequestNodes and interestNodes maps
        for (Object node in data) {
            splitId = node['id'].split("_")
            if (splitId[0] == "p") {
                //this is a person node
                node['id'] = splitId[1]
                personNodes.putAt(splitId[1], node)
            } else if (splitId[0] == "i") {
                node['id'] = splitId[1]
                interestNodes.putAt(splitId[1], node)
            } else if (splitId[0] == "c") {
                node['id'] = splitId[1]
                collaboratorRequestNodes.putAt(splitId[1], node)
            }
        }

        for (Map<Object, Object> node in personNodes.values()) {
            def adjacencies = []
            for (Object adj in node['adjacencies']) {
                splitId = adj['nodeTo'].split("_")
                adj['nodeTo'] = splitId[1]
                adjacencies.add(adj['nodeTo'])
                //println(adjacencies)
            }
            //println(adjacencies)
            node['adjacencies'] = adjacencies
        }

        for (Map<Object, Object> node in interestNodes.values()) {
            def adjacencies = []
            for (Object adj in node['adjacencies']) {
                splitId = adj['nodeTo'].split("_")
                adj['nodeTo'] = splitId[1]
                adjacencies.add(adj['nodeTo'])
            }

            node['adjacencies'] = adjacencies
        }

        for (Map<Object, Object> node in collaboratorRequestNodes.values()) {
            def adjacencies = []
            for (Object adj in node['adjacencies']) {
                splitId = adj['nodeTo'].split("_")
                adj['nodeTo'] = splitId[1]
                adjacencies.add(adj['nodeTo'])
            }

            node['adjacencies'] = adjacencies
        }

        return [personNodes: personNodes, interestNodes: interestNodes, collaboratorRequestNodes: collaboratorRequestNodes]
    }

    def buildUserCentricGraph(Person person) {
        // Mapping from ids to nodes
        // Begin by adding root and root interests
        // then collaboratorRequests related to root interests
        Map<Long, Object> personNodes = [:]
        Map<Long, Object> interestNodes = [:]
        Map<Long, Object> collaboratorRequestNodes = [:]
        personNodes["p_" + person.id] = makeJsonForPerson(person)

        for (Interest i: person.interests) {
            interestNodes["i_" + i.id] = makeJsonForInterest(i)
            interestNodes["i_" + i.id]['adjacencies'].add("p_" + person.id)
            personNodes["p_" + person.id]['adjacencies'].add("i_" + i.id)
        }

        Graph graph = similarityService.calculatePersonNeighbors(person, DEFAULT_MAX_NEIGHBORS_PERSON_CENTRIC)
        for (Person p: graph.getPeople()){
            if(p==person){
                continue
            }
            def pid = "p_${p.id}"
            personNodes[pid] = makeJsonForPerson(p)
            for (Edge e: graph.getAdjacentEdges(p)){
                def iid = "i_${e.interest.id}"
                if(!interestNodes[iid]){
                    interestNodes[iid] = makeJsonForInterest(e.interest)
                }
                if(e.hasRelatedInterest()){
                   personNodes[pid]['data']['sharedInterest'].add(e.relatedInterest.text)
                } else if (e.hasSharedInterest()){
                   personNodes[pid]['data']['sharedInterest'].add(e.interest.text)
                }
                personNodes[pid]['adjacencies'].add(iid)
                interestNodes[iid]['adjacencies'].add(pid)
            }
        }
        for (CollaboratorRequest c: graph.getRequests()) {
            def cid = "c_${c.id}"
            collaboratorRequestNodes[cid] = makeJsonForCollaboratorRequest(c)
            for (Edge e: graph.getAdjacentEdges(c)){

                def iid = "i_${e.interest.id}"
                if(!interestNodes[iid]){
                    interestNodes[iid] = makeJsonForInterest(e.interest)
                }
                if(e.hasRelatedInterest()){
                   collaboratorRequestNodes[cid]['data']['sharedInterest'].add(e.relatedInterest.text)
                } else if (e.hasSharedInterest()){
                   collaboratorRequestNodes[cid]['data']['sharedInterest'].add(e.interest.text)
                }
                collaboratorRequestNodes[cid]['adjacencies'].add(iid)
                interestNodes[iid]['adjacencies'].add(cid)
            }
        }

        adjacencyMap(personNodes)
        adjacencyMap(interestNodes)
        adjacencyMap(collaboratorRequestNodes)
        addColors(personNodes, interestNodes, collaboratorRequestNodes)

        return personNodes.values() + interestNodes.values() + collaboratorRequestNodes.values()
    }

    def buildInterestCentricGraph(Interest root) {
        // Mapping from ids to nodes
        // Begin by adding root and root interests
        Map<Long, Object> personNodes = [:]
        Map<Long, Object> interestNodes = [:]
        Map<Long, Object> collaboratorRequestNodes = [:]
        def rid = "i_${root.id}"
        interestNodes[rid] = makeJsonForInterest(root)

        Graph graph = similarityService.calculateInterestNeighbors(root, DEFAULT_MAX_NEIGHBORS_INTEREST_CENTRIC, DEFAULT_MAX_INTERESTS_INTEREST_CENTRIC)

        for (Interest i: graph.getInterests()) {
            def iid = "i_${i.id}"
            if(!interestNodes[iid]) {
               interestNodes[iid] = makeJsonForInterest(i)
            }
            if(iid!=rid){
                interestNodes[rid]['adjacencies'].add(iid)
                interestNodes[iid]['adjacencies'].add(rid)
            }
            for(Edge e: graph.getAdjacentEdges(i)){
                if(e.person){
                    def pid = "p_${e.person.id}"
                    if(!personNodes[pid]) {
                        personNodes[pid] = makeJsonForPerson(e.person)
                    }
                    interestNodes[iid]['adjacencies'].add(pid)
                    personNodes[pid]['adjacencies'].add(iid)
                }
                if(e.request){
                  def cid = "c_${e.request.id}"
                  if(!personNodes[cid]) {
                      personNodes[cid] = makeJsonForCollaboratorRequest(e.request)
                  }
                  interestNodes[iid]['adjacencies'].add(cid)
                  collaboratorRequestNodes[cid]['adjacencies'].add(iid)
                }
            }

        }

        adjacencyMap(personNodes)
        adjacencyMap(interestNodes)
        adjacencyMap(collaboratorRequestNodes)
        addColors(personNodes, interestNodes, collaboratorRequestNodes)

        return interestNodes.values() + personNodes.values() + collaboratorRequestNodes.values()
    }

    def buildCollaboratorRequestCentricGraph(CollaboratorRequest request) {
        // Mapping from ids to nodes
        // Begin by adding root and root interests
        // then collaboratorRequests related to root interests
        Map<Long, Object> personNodes = [:]
        Map<Long, Object> interestNodes = [:]
        Map<Long, Object> collaboratorRequestNodes = [:]
        collaboratorRequestNodes["c_" + request.id] = makeJsonForCollaboratorRequest(request)

        for (Interest i: request.keywords) {
            interestNodes["i_" + i.id] = makeJsonForInterest(i)
            interestNodes["i_" + i.id]['adjacencies'].add("c_" + request.id)
            collaboratorRequestNodes["c_" + request.id]['adjacencies'].add("i_" + i.id)
        }

        Graph graph = similarityService.calculateRequestNeighbors(request, DEFAULT_MAX_NEIGHBORS_PERSON_CENTRIC)
        for (CollaboratorRequest c: graph.getRequests()){
            if(c==request){
               continue
            }
            def cid = "c_${c.id}"
            collaboratorRequestNodes[cid] = makeJsonForCollaboratorRequest(c)
            for (Edge e: graph.getAdjacentEdges(c)){
                def iid = "i_${e.interest.id}"
                if(!interestNodes[iid]){
                    interestNodes[iid] = makeJsonForInterest(e.interest)
                }
                if(e.hasRelatedInterest()){
                   collaboratorRequestNodes[cid]['data']['sharedInterest'].add(e.relatedInterest.text)
                } else if (e.hasSharedInterest()){
                   collaboratorRequestNodes[cid]['data']['sharedInterest'].add(e.interest.text)
                }
                collaboratorRequestNodes[cid]['adjacencies'].add(iid)
                interestNodes[iid]['adjacencies'].add(cid)
            }
        }
        for (Person p: graph.getPeople()){
            def pid = "p_${p.id}"
            personNodes[pid] = makeJsonForPerson(p)
            for (Edge e: graph.getAdjacentEdges(p)){
                def iid = "i_${e.interest.id}"
                if(!interestNodes[iid]){
                    interestNodes[iid] = makeJsonForInterest(e.interest)
                }
                if(e.hasRelatedInterest()){
                   personNodes[pid]['data']['sharedInterest'].add(e.relatedInterest.text)
                } else if (e.hasSharedInterest()){
                   personNodes[pid]['data']['sharedInterest'].add(e.interest.text)
                }
                personNodes[pid]['adjacencies'].add(iid)
                interestNodes[iid]['adjacencies'].add(pid)
            }
        }

        adjacencyMap(personNodes)
        adjacencyMap(interestNodes)
        adjacencyMap(collaboratorRequestNodes)
        addColors(personNodes, interestNodes, collaboratorRequestNodes)

        return collaboratorRequestNodes.values() + personNodes.values() + interestNodes.values()
    }

    //Hash the people to colours
    def addColors(personNodes, interestNodes, collaboratorRequestNodes) {
        def personAndRequestColors = [:]

        for (String id in personNodes.keySet()) {
            int personId = jsIdToId(id).toInteger()
            Person p = personService.get(personId)
            int i = p.hashCode() % org.macademia.MacademiaConstants.COLORS.size()
            personAndRequestColors[id] = org.macademia.MacademiaConstants.COLORS[i]
        }

        for (String id in collaboratorRequestNodes.keySet()) {
            int requestId = jsIdToId(id).toInteger()
            CollaboratorRequest c = collaboratorRequestService.get(requestId)
            int i = c.creator.hashCode() % org.macademia.MacademiaConstants.COLORS.size()
            personAndRequestColors[id] = org.macademia.MacademiaConstants.COLORS[i]
        }

        for (String pid in personNodes.keySet()) {
            Map<Object, Object> node = personNodes[pid]
            String c = personAndRequestColors[pid]
            for (Object adj in node['adjacencies']) {
                adj['data'].putAt("\$color", c)
            }
        }

        for (String cid in collaboratorRequestNodes.keySet()) {
            Map<Object, Object> node = collaboratorRequestNodes[cid]
            String c = personAndRequestColors[cid]
            for (Object adj in node['adjacencies']) {
                adj['data'].putAt("\$color", c)
            }
        }


        for (Map<Object, Object> node in interestNodes.values()) {
            for (Object adj in node['adjacencies']) {
                String id = adj['nodeTo']
                if (personAndRequestColors[id]) {
                    adj['data'].putAt("\$color", personAndRequestColors[id])
                }
            }
        }
    }

    /**
     * Since people appear more than once, people nodes must not
     * use their own id.
     */
    def makeJsonForPerson(Person p) {
        return [
                id: "p_" + p.id,
                name: p.fullName,
                data: [
                        unmodifiedId: p.id,
                        name: p.fullName,
                        email: p.email,
                        department: p.department,
                        sharedInterest: [],
                        type: 'person'
                ],
                adjacencies: []
        ]
    }

    def makeJsonForInterest(Interest i) {
        return [
                id: "i_" + i.id,
                name: i.text,
                data: [
                        unmodifiedId: i.id,
                        type: 'interest'
                ],
                adjacencies: []
        ]
    }

    def makeJsonForCollaboratorRequest(CollaboratorRequest c) {
        return [
                id: "c_" + c.id,
                name: c.title,
                data: [
                        unmodifiedId: c.id,
                        name: c.title,
                        creator: c.creator,
                        sharedInterest: [],
                        type: 'request'
                ],
                adjacencies: []
        ]
    }

    //make the adjacency list a map so we can store data about adjacencies
    def adjacencyMap(Map<Long, Object> objectNodes) {
        for (Map<Object, Object> node: objectNodes.values()) {
            node["adjacencies"] = node["adjacencies"].collect({
                [
                        "nodeTo": it,
                        "data": [:]
                ]
            })
        }
    }

    def jsIdToId(String jsId) {
        return jsId.split("_")[1]
    }


}
