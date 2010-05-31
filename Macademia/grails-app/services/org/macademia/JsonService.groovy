package org.macademia

import grails.converters.JSON
import java.awt.Color

class JsonService {

    boolean transactional = true

    SimilarityService similarityService
    InterestService interestService
    PersonService personService

    def parseJsonToGroovy(json) {
        def data = JSON.parse(json)
        // println("recieved JSON: "+data)
        Map<Long, Object> personNodes = [:]
        Map<Long, Object> interestNodes = [:]
        def splitId

        //rebuilding the separate personNodes and interestNodes maps
        for (Object node in data) {
            splitId = node['id'].split("_")
            if (splitId[0] == "p") {
                //this is a person node
                node['id'] = splitId[1]
                personNodes.putAt(splitId[1], node)
            } else if (splitId[0] == "i") {
                node['id'] = splitId[1]
                interestNodes.putAt(splitId[1], node)
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

        return [personNodes: personNodes, interestNodes: interestNodes]
    }

    def buildUserCentricGraph(Person person) {
        // Mapping from ids to nodes
        // Begin by adding root and root interests
        Map<Long, Object> personNodes = [:]
        Map<Long, Object> interestNodes = [:]
        personNodes["p_" + person.id] = makeJsonForPerson(person)

        for (Interest i: person.interests) {
            interestNodes["i_" + i.id] = makeJsonForInterest(i)
            interestNodes["i_" + i.id]['adjacencies'].add("p_" + person.id)
            personNodes["p_" + person.id]['adjacencies'].add("i_" + i.id)
        }


        for (Neighbors n: similarityService.getNeighbors(person)) {
            int i = 0
            // println("sharedInterests: "+n.sharedInterests)
            for (InterestRelation ir: n.sharedInterests) {
                def pid = "p_${n.second.id}"
                def iid = "i_${ir.first.id}"

                // Create the person, and store the shared interest
                if (!personNodes[pid]) {
                    personNodes[pid] = makeJsonForPerson(n.second)
                }
                personNodes[pid]['data']['sharedInterest'].add(ir.second.text)
                def irList = ['first': ir.first, 'second': ir.second]
//                personNodes[pid]['data']['irs'].add(irList);

                // add two way relation between
                personNodes[pid]['adjacencies'].add(iid)
                interestNodes[iid]['adjacencies'].add(pid)
                i++
            }
        }

        // go in and make all people / interest ids unique by prefixing with i and p
        //also make the adjacency list a map so we can store data about adjacencies
        for (Map<Object, Object> node: personNodes.values()) {
            node["adjacencies"] = node["adjacencies"].collect({
                [
                        "nodeTo": it,
                        "data": [:]
                ]
            })
        }
        for (Map<Object, Object> node: interestNodes.values()) {
            node["adjacencies"] = node["adjacencies"].collect({
                [
                        "nodeTo": it,
                        "data": [:]
                ]
            })
        }

        addColors(personNodes, interestNodes)

        return personNodes.values() + interestNodes.values()
    }

    def buildInterestCentricGraph(Interest root) {
        // Mapping from ids to nodes
        // Begin by adding root and root interests
        Map<Long, Object> personNodes = [:]
        Map<Long, Object> interestNodes = [:]
        def rid = "i_${root.id}"
        interestNodes[rid] = makeJsonForInterest(root)

        for (InterestRelation ir: similarityService.getSimilarInterests(root)) {
            if (ir.second == ir.first) {
                continue
            }
            def i = ir.second
            def iid = "i_${i.id}"
            if (!interestNodes[iid]) {
                interestNodes[iid] = makeJsonForInterest(i)
            }
            // add the adjacency in both directions
            interestNodes[iid]['adjacencies'].add(rid)
            interestNodes[rid]['adjacencies'].add(iid)
        }


        for (String iid: interestNodes.keySet()) {
            long longIid = iid.split("_")[1] as long
            Interest i = interestService.get(longIid)
            for (Person p: personService.findByInterest(i)) {
                def pid = "p_${p.id}"
                // Create the person, and store the shared interest
                if (!personNodes[pid]) {
                    personNodes[pid] = makeJsonForPerson(p)
                }
                personNodes[pid]['adjacencies'].add(iid)
                interestNodes[iid]['adjacencies'].add(pid)
            }
        }

        // go in and make all people / interest ids unique by prefixing with i and p
        //also make the adjacency list a map so we can store data about adjacencies
        for (Map<Object, Object> node: personNodes.values()) {
            node["adjacencies"] = node["adjacencies"].collect({
                [
                        "nodeTo": it,
                        "data": [:]
                ]
            })
        }
        for (Map<Object, Object> node: interestNodes.values()) {
            node["adjacencies"] = node["adjacencies"].collect({
                [
                        "nodeTo": it,
                        "data": [:]
                ]
            })
        }

        addColors(personNodes, interestNodes)

        return interestNodes.values() + personNodes.values()
    }

    def addColors(personNodes, interestNodes) {
        def personColors = [:]
        for (String pid in personNodes.keySet()) {
            int i = personColors.size() % org.macademia.MacademiaConstants.COLORS.size()
            personColors[pid] = org.macademia.MacademiaConstants.COLORS[i]
        }

        for (String pid in personNodes.keySet()) {
            Map<Object, Object> node = personNodes[pid]
            String c = personColors[pid]
            for (Object adj in node['adjacencies']) {
                adj['data'].putAt("\$color", c)
            }
        }


        for (Map<Object, Object> node in interestNodes.values()) {
            for (Object adj in node['adjacencies']) {
                String id = adj['nodeTo']
                if (personColors[id]) {
                    adj['data'].putAt("\$color", personColors[id])
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
                name: p.name,
                data: [
                        unmodifiedId: p.id,
                        name: p.name,
                        email: p.email,
                        department: p.department,
                        sharedInterest: [],
                        irs: [],
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

    def jsIdToId(String jsId) {
        return node['id'].split("_")[1]
    }
}
