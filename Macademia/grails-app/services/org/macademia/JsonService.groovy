package org.macademia

import grails.converters.JSON

class JsonService {

      //max number of interests per interest-centric graph
    int DEFAULT_MAX_INTERESTS_INTEREST_CENTRIC = 25

    boolean transactional = true

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
            } else if (splitId[0] == "r") {
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

    def buildUserCentricGraph(Person person, Graph graph) {
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
        for (CollaboratorRequest r: graph.getRequests()) {
            def rid = "r_${r.id}"
            collaboratorRequestNodes[rid] = makeJsonForCollaboratorRequest(r)
            for (Edge e: graph.getAdjacentEdges(r)){

                def iid = "i_${e.interest.id}"
                if(!interestNodes[iid]){
                    interestNodes[iid] = makeJsonForInterest(e.interest)
                }
                if(e.hasRelatedInterest()){
                   collaboratorRequestNodes[rid]['data']['sharedInterest'].add(e.relatedInterest.text)
                } else if (e.hasSharedInterest()){
                   collaboratorRequestNodes[rid]['data']['sharedInterest'].add(e.interest.text)
                }
                collaboratorRequestNodes[rid]['adjacencies'].add(iid)
                interestNodes[iid]['adjacencies'].add(rid)
            }
        }

        adjacencyMap(personNodes)
        adjacencyMap(interestNodes)
        adjacencyMap(collaboratorRequestNodes)
        addColors(personNodes, interestNodes, collaboratorRequestNodes)

        return personNodes.values() + interestNodes.values() + collaboratorRequestNodes.values()
    }

    def buildInterestCentricGraph(Interest interest, Graph graph) {
        // Mapping from ids to nodes
        // Begin by adding root and root interests
        Map<Long, Object> personNodes = [:]
        Map<Long, Object> interestNodes = [:]
        Map<Long, Object> collaboratorRequestNodes = [:]
        def riid = "i_${interest.id}"
        interestNodes[riid] = makeJsonForInterest(interest)

        for (Interest i: graph.getInterests()) {
            def iid = "i_${i.id}"
            if(!interestNodes[iid]) {
               interestNodes[iid] = makeJsonForInterest(i)
            }
            if(iid!=riid){
                interestNodes[riid]['adjacencies'].add(iid)
                interestNodes[iid]['adjacencies'].add(riid)
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
                  def rid = "r_${e.request.id}"
                  if(!collaboratorRequestNodes[rid]) {
                      collaboratorRequestNodes[rid] = makeJsonForCollaboratorRequest(e.request)
                  }
                  interestNodes[iid]['adjacencies'].add(rid)
                  collaboratorRequestNodes[rid]['adjacencies'].add(iid)
                }
            }

        }

        adjacencyMap(personNodes)
        adjacencyMap(interestNodes)
        adjacencyMap(collaboratorRequestNodes)
        addColors(personNodes, interestNodes, collaboratorRequestNodes)

        return interestNodes.values() + collaboratorRequestNodes.values() + personNodes.values()
    }

    def buildCollaboratorRequestCentricGraph(CollaboratorRequest request, Graph graph) {
        // Mapping from ids to nodes
        // Begin by adding root and root interests
        // then collaboratorRequests related to root interests
        Map<Long, Object> personNodes = [:]
        Map<Long, Object> interestNodes = [:]
        Map<Long, Object> collaboratorRequestNodes = [:]
        collaboratorRequestNodes["r_" + request.id] = makeJsonForCollaboratorRequest(request)

        for (Interest i: request.keywords) {
            interestNodes["i_" + i.id] = makeJsonForInterest(i)
            interestNodes["i_" + i.id]['adjacencies'].add("r_" + request.id)
            collaboratorRequestNodes["r_" + request.id]['adjacencies'].add("i_" + i.id)
        }

        for (CollaboratorRequest r: graph.getRequests()){
            if(r==request){
               continue
            }
            def rid = "r_${r.id}"
            collaboratorRequestNodes[rid] = makeJsonForCollaboratorRequest(r)
            for (Edge e: graph.getAdjacentEdges(r)){
                def iid = "i_${e.interest.id}"
                if(!interestNodes[iid]){
                    interestNodes[iid] = makeJsonForInterest(e.interest)
                }
                if(e.hasRelatedInterest()){
                   collaboratorRequestNodes[rid]['data']['sharedInterest'].add(e.relatedInterest.text)
                } else if (e.hasSharedInterest()){
                   collaboratorRequestNodes[rid]['data']['sharedInterest'].add(e.interest.text)
                }
                collaboratorRequestNodes[rid]['adjacencies'].add(iid)
                interestNodes[iid]['adjacencies'].add(rid)
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

        for (String pid in personNodes.keySet()) {
//            int personId = jsIdToId(id).toInteger()
//            Person p = personService.get(personId)
            String fullName = personNodes[pid]['data']['name']
            int i = fullName.hashCode() % org.macademia.MacademiaConstants.COLORS.size()
            personAndRequestColors[pid] = org.macademia.MacademiaConstants.COLORS[i]
        }

        for (String rid in collaboratorRequestNodes.keySet()) {
//            int requestId = jsIdToId(id).toInteger()
//            CollaboratorRequest c = collaboratorRequestService.get(requestId)
            String creator = collaboratorRequestNodes[rid]['data']['creator']
            int i = creator.hashCode() % org.macademia.MacademiaConstants.COLORS.size()
            personAndRequestColors[rid] = "#FF0000"//org.macademia.MacademiaConstants.COLORS[i]
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
                } else{
                    adj['data'].putAt("\$color", "#0080FF")
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

    def makeJsonForCollaboratorRequest(CollaboratorRequest r) {
        return [
                id: "r_" + r.id,
                name: r.title,
                data: [
                        unmodifiedId: r.id,
                        name: r.title,
                        creator: r.creator.fullName,
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