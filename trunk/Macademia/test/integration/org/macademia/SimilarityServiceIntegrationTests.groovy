package org.macademia

import grails.test.*

class SimilarityServiceIntegrationTests extends GrailsUnitTestCase {
    def similarityService
    def interestService
    
    protected void setUp() {
        super.setUp()

        similarityService.refinedThreshold = 0.8
        similarityService.minSimsPerInterest = 1
        similarityService.numSimsPerInterest = 2
        similarityService.maxSimsPerInterest = 3
        //similarityService.analyze()     // rebuild
    }

    protected void tearDown() {
        super.tearDown()
    }

    /*void testSimilariInterests() {
        Interest africa = interestService.findByText("africa")
        Interest darfur = interestService.findByText("darfur")
        Interest humanRights = interestService.findByText("human rights")
        Interest modernism = interestService.findByText("modernism")
        Interest law = interestService.findByText("law")
        assertNotNull(africa)
        assertNotNull(darfur)
        assertNotNull(humanRights)
        assertNotNull(modernism)
        assertNotNull(law)

        List<InterestRelation> sim1 = similarityService.getSimilarInterests(africa)
        List<InterestRelation> sim2 = similarityService.getSimilarInterests(darfur)
        List<InterestRelation> sim3 = similarityService.getSimilarInterests(humanRights)
        List<InterestRelation> sim4 = similarityService.getSimilarInterests(modernism)
        List<InterestRelation> sim5 = similarityService.getSimilarInterests(law)

        assertEquals(sim1.size(), 2)
        assertEquals(sim2.size(), 2)
        assertEquals(sim3.size(), 2)
        assertEquals(sim4.size(), 2)
        assertEquals(sim5.size(), 2)

//        assertTrue(sim1[0].second == darfur)
//        assertTrue(sim2[0].second == africa)
    }

    void testNeighbors() {
        Person shilad = Person.findByEmail("ssen@macalester.edu")
        Person diane = Person.findByEmail("michelfelder@macalester.edu")
        Person dianna = Person.findByEmail("shandy@macalester.edu")
        Person arjun = Person.findByEmail("guneratne@macalester.edu")
        Person jaine = Person.findByEmail("strauss@macalester.edu")

        assertNotNull(shilad)
        assertNotNull(diane)
        assertNotNull(dianna)
        assertNotNull(arjun)
        assertNotNull(jaine)

        List<Neighbors> n1 = similarityService.getNeighbors(shilad)
        List<Neighbors> n2 = similarityService.getNeighbors(diane)
        List<Neighbors> n3 = similarityService.getNeighbors(dianna)
        List<Neighbors> n4 = similarityService.getNeighbors(arjun)
        List<Neighbors> n5 = similarityService.getNeighbors(jaine)

//        assertEquals(n1.size(), 4)
//        assertEquals(n2.size(), 4)
//        assertEquals(n3.size(), 4)
//        assertEquals(n4.size(), 4)
//        assertEquals(n5.size(), 4)

        boolean found = false
        for (Neighbors n : n1) {
            assertEquals(n.first, shilad)
            if (n.second == diane) {
                found = true
          //      assertEquals(n.sharedInterests.size(), 2)
                assertEquals(n.sharedInterests[0].first.normalizedText, "web20")
                assertEquals(n.sharedInterests[0].second.normalizedText, "web20")
                assertEquals(n.sharedInterests[0].similarity, 1.0, 0.001)
                assertEquals(n.sharedInterests[1].first.normalizedText, "collaborativecomputing")
                assertEquals(n.sharedInterests[1].second.normalizedText, "therighttoprivacy")
        //       assertEquals(n.sharedInterests[1].similarity, 0.5, 0.001)
            }
        }
        assertTrue(found)
        
        println n1
        println n2
        println n3
        println n4
        println n5

    }    */

    void testGetSimilarInterests(){
        Interest interest= Interest.findByText("web2.0")
        List<InterestRelation> list= similarityService.getSimilarInterests(interest)
        assertEquals(list.size(),6)
        InterestRelation ir =list.get(0)
        Interest second= Interest.findByText("globalization")
        assertEquals(ir.second, second)
        ir=list.get(3)
        second= Interest.findByText("anthropology")
        assertEquals(ir.second, second)
        ir=list.get(5)
        assertTrue(ir.similarity>0.136)
        assertTrue(ir.similarity<0.142)
        ir=list.get(0)
        assertTrue(ir.similarity<0.2)
        InterestRelation ir2=list.get(1)
        assertTrue(ir.similarity>ir2.similarity)
    }

    void testCalculateInterestNeighbors() {
        Interest interest = Interest.findByText("web2.0")
        Graph graph = similarityService.calculateInterestNeighbors(interest, 4)
        assertEquals(graph.edgeSize(),8)
        Edge e1= new Edge(interest:interest, relatedInterest:Interest.findByText("online communities"))
        assertTrue(graph.getAdjacentEdges(interest).contains(e1))
        Edge e2= new Edge(interest:Interest.findByText("online communities"), relatedInterest:interest)
        assertTrue(graph.getAdjacentEdges(interest).contains(e2))
        Edge e3= new Edge(person:Person.findById(1) ,interest:Interest.findById(14))
        assertTrue(graph.getAdjacentEdges(Person.findById(1)).contains(e3))
        Edge e4 = new Edge(interest: interest, relatedInterest:Interest.findById(30))
        assertTrue(graph.getAdjacentEdges(interest).contains(e4))

    }

    void testCalculatePersonNeighbors () {
        Person p=Person.findById(4)
        Graph graph= similarityService.calculatePersonNeighbors(p,10)
        Edge e = new Edge(person:Person.findById(1),interest:Interest.findById(42), relatedInterest:Interest.findById(5))
        assertFalse(graph.getAdjacentEdges(Person.findById(1)).contains(e))
        assertEquals(graph.edgeSize(),36)
        e=new Edge(person:Person.findById(4),interest:Interest.findById(42))
        assertTrue(graph.getAdjacentEdges(Person.findById(4)).contains(e))
        e=new Edge(person:Person.findById(3),interest:Interest.findById(5), relatedInterest:Interest.findById(30))
        assertTrue(graph.getAdjacentEdges(Person.findById(3)).contains(e))
    }

    void testBuildInterestRelations () {
        Interest i = new Interest("GIS")
        Interest i2= new Interest("GIS (Geographic Information System)")
        Interest i3= new Interest("e-democracy")
        i.save()
        i2.save()
        i3.save()
        Interest interest= Interest.findByText("web2.0")
        List<InterestRelation> list= InterestRelation.findAllByFirst(interest, [sort:"similarity", order:"desc"])
        assertEquals(list.size(),11)
        interestService.buildDocuments(i)
        interestService.buildDocuments(i2)
        interestService.buildDocuments(i3)
        similarityService.buildInterestRelations(i)
        similarityService.buildInterestRelations(i2)
        similarityService.buildInterestRelations(i3)
        assertTrue(InterestRelation.findByFirst(i)!=null)
        assertTrue(InterestRelation.findAllByFirst(i, [sort:"similarity", order:"desc"]).get(0).similarity>0.2)
        List<InterestRelation> nlist= InterestRelation.findAllByFirst(interest)
        Set<InterestRelation> nset= new HashSet<InterestRelation>()
        for(InterestRelation ir : nlist){
            nset.add(ir)
        }
        assertEquals(nset.size(),11)
        List<InterestRelation> nlist2= InterestRelation.findAllByFirst(i, [sort:"similarity", order:"desc"])
        assertEquals(nlist2.size(),13)
        InterestRelation ir = nlist2.get(0)
        InterestRelation ir2= new InterestRelation(first:ir.second, second:ir.first, similarity:ir.similarity)
        List<InterestRelation> nlist3=InterestRelation.findAllByFirst(ir.second)
        Set<InterestRelation> nset2= new HashSet<InterestRelation>()
        for(InterestRelation iir: nlist3){
            nset2.add(iir)
        }
        assertTrue(nset2.contains(ir2))

    }
}
