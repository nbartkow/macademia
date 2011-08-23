package org.macademia.nbrviz

import org.macademia.*

/**
 * This Service supplies data in the JSON format for the construction
 * of the new visualizations.  This data will be slightly different
 * depending upon which graph type, query or exploration, is being
 * constructed.
 *
 * The following JSON is common between the two visualizations, and
 * is the return value of buildJsonForGraph:
 *
 * [
 *      people: [
 *          p_id1: [
 *              id: long
 *              name: string
 *              pic: string
 *              relevance: [
 *                  overall: double,
 *                  id1: double
 *                  ...
 *              ]
 *              interests: [id1, id2 ...]
 *          ],
 *          p_id2: [ ... ], ...
 *      ]
 *      interests: [
 *          i_id1: [
 *              id: long
 *              name: string
 *              cluster: int
 *          ],
 *          i_id2: [ ... ], ...
 *      ]
 * ]
 *
 * The query visualization adds the following data to the common
 * datastructure by calling buildQueryCentricGraph:
 *
 * [queries: [id1, id2...]] + jsonForGraph
 *
 *
 * Meanwhile, the exploration visualization adds the following data
 * by calling buildExplorationCentricGraph:
 *
 * [root: id] + jsonForGraph
 *
 */
class Json2Service {

    def collaboratorRequestService
      //max number of interests per interest-centric graph
    static int DEFAULT_MAX_INTERESTS_INTEREST_CENTRIC = 25
    def similarity2Service
    def interestService
    def pseudonymService

    boolean transactional = true

    def makeJsonPerson(Person p, Long sid) {
        def interests = []
        for (i in p.interests){
            interests.add(i.id)
        }
        def fakedata = pseudonymService.getFakeData(sid, p.id)
        return [
                id: p.id,
                fid: fakedata.id,
                name: fakedata.name,
                pic: fakedata.pic,
                relevance: [:],
                interests: interests
        ]
    }

    def makeJsonInterest(Interest i){
        return [
                id: i.id,
                name: i.text,
                cluster:-1
        ]
    }

    /**
     * Too much branching logic. This should be split up into three separate modes. Yuck
     * @param graph
     * @param sid
     * @return
     */
    def buildJsonForGraph(Graph graph, Long sid){
        Map<Long, Object> personNodes = [:]
        Map<Long, Object> interestNodes = [:]
        graph.clusterRootInterests()
        for (Person p: graph.getPeople()){
            personNodes[p.id] = makeJsonPerson(p, sid)
            personNodes[p.id]['relevance']['overall'] = graph.personScores[p.id].score[0]
            for (Edge e: graph.getAdjacentEdges(p)){
                e.reify()
                [e.interest, e.relatedInterest].each({
                    if (it && !interestNodes[it.id]) {
                        interestNodes[it.id] = makeJsonInterest(it)
                    }
                })
            }
        }
        for (Map.Entry<Long, Integer> entry : graph.interestClusters.entrySet()){
            if (interestNodes.containsKey(entry.key)){
                interestNodes[entry.key]['cluster'] = entry.value
            }
        }
        return ['people':personNodes] + ['interests':interestNodes]
    }


    def buildQueryCentricGraph(Set<Long> queryIds, Graph graph, Long sid){
        Map<Long, Map> personNodes = [:]
        Map<Long, Map> interestNodes = [:]
        for (Person p: graph.getPeople()){
            personNodes[p.id] = makeJsonPerson(p, sid)
            personNodes[p.id]['relevance']['overall'] = graph.personScores[p.id].score[0]
            for (Edge e: graph.getAdjacentEdges(p)){
                e.reify()
                [e.interest, e.relatedInterest].each({
                    if (it && !interestNodes[it.id]) {
                        interestNodes[it.id] = makeJsonInterest(it)
                    }
                })
                if (e.relatedInterest && e.relatedInterestId != e.interestId) {
                    interestNodes[e.relatedInterestId].cluster = e.interestId
                }
            }
        }
        for (Person p : graph.getPeople()){
            for (Interest i : p.interests) {
                if (!interestNodes[i.id]) {
                    interestNodes[i.id] = makeJsonInterest(i)
//                    interestNodes[i.id].cluster = -1
                }
            }
            for (Edge e : graph.getAdjacentEdges(p)){
                if (queryIds.contains(e.interestId)){
                    personNodes[p.id]['relevance'][e.interestId] = e.sim
                }
            }
        }
        return [
                'people':personNodes,
                'interests':interestNodes,
                'queries' : queryIds
        ]
    }

    def buildExplorationCentricGraph(Object root, Graph graph, Long sid){
        def basejson = buildJsonForGraph(graph, sid)
        Map<Long, List> clusters= [:]
        for(MapEntry e: graph.interestClusters.entrySet()){
            if(!clusters[e.value]){
                clusters[e.value] = []
            }
            clusters[e.value].add(e.key)
        }
        for (Person p : graph.getPeople()){
            for (Integer cid : clusters.keySet()){
                def something = graph.clusterSimilarity(p.interests.collect({ it.id }) as Collection<Long>, clusters[cid] as Collection<Long> )
                basejson['people'][p.id]['relevance'][cid] = something
            }
        }
        return ['root':root.id] + basejson
    }

    def buildExplorationCentricGraph(Object root, Graph graph){
        return buildExplorationCentricGraph(root,graph,0)
    }

    def buildQueryCentricGraph(Set<Long> qset, Graph graph){
        return buildQueryCentricGraph(qset, graph, 0)
    }
}