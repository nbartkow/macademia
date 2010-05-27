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

    // Only similarities in the top fraction are used to fill out interests
    double threshold = 0.2

    // Maximum number of neighbors per person
    int maxNeighbors = 20

    // Minimum similarity that is stored in the database.
    double minSimilarity = 0.1

    boolean transactional = true
    Map<Interest, List<InterestRelation>> similarInterests = [:]
    Map<Person, List<Neighbors>> neighbors = [:]

    def personService
    def interestService

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
        for (Interest i : Interest.findAll()) {
//        Interest i = interestService.findByText("web2.0")
            log.info("calculating all similarities for ${i}")
            for (Interest j : Interest.findAll()) {
                if (!i.equals(j)) {
                    double sim = calculatePairwiseSimilarity(i, j, tfIdf)
                    InterestRelation ir = new InterestRelation(first : i, second: j, similarity : sim)
                    println("${sim}\t${i}\t${j}")
                    ir.save()
                }
            }
            if (((++counter) % 10) == 0) {
                cleanUpGorm()                
            }
        }
    }

    def calculatePairwiseSimilarity(Interest i1, Interest i2, TfIdfDistance tfIdf) {
        Document d1 = i1.findMostRelevantDocument()
        Document d2 = i2.findMostRelevantDocument()
        return tfIdf.proximity(d1.text, d2.text)
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
                && (bl.isRetained(ir))
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
        def maxIndex = all.size() * threshold as int
        for (InterestRelation ir: all[0..maxIndex]) {
            if ((!used.contains(ir))
            && (bl.isRetained(ir))
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
     * Returns a list of similar interests in the form of interest relations.
     * The first interest in every interest relation will be the interest requested.
     * The second will be the similar interest.
     * The list is sorted by similarity.
     */
    public List<InterestRelation> getSimilarInterests(Interest i) {
        return similarInterests[i]
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
