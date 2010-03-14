package org.macademia

import grails.test.*

class SimilarityServiceIntegrationTests extends GrailsUnitTestCase {
    def similarityService
    def interestService
    
    protected void setUp() {
        super.setUp()

        similarityService.threshold = 0.8
        similarityService.minSimsPerInterest = 1
        similarityService.numSimsPerInterest = 2
        similarityService.maxSimsPerInterest = 3
        similarityService.analyze()     // rebuild
    }

    protected void tearDown() {
        super.tearDown()
    }

    void testSimilariInterests() {
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
        assertEquals(sim5.size(), 1)

        assertTrue(sim1[0].second == darfur)
        assertTrue(sim2[0].second == africa)
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

        assertEquals(n1.size(), 4)
        assertEquals(n2.size(), 3)
        assertEquals(n3.size(), 2)
        assertEquals(n4.size(), 4)
        assertEquals(n5.size(), 4)

        boolean found = false
        for (Neighbors n : n1) {
            assertEquals(n.first, shilad)
            if (n.second == diane) {
                found = true
                assertEquals(n.sharedInterests.size(), 2)
                assertEquals(n.sharedInterests[0].first.normalizedText, "web20")
                assertEquals(n.sharedInterests[0].second.normalizedText, "web20")
                assertEquals(n.sharedInterests[0].similarity, 1.0, 0.001)
                assertEquals(n.sharedInterests[1].first.normalizedText, "socialnetworks")
                assertEquals(n.sharedInterests[1].second.normalizedText, "web20")
                assertEquals(n.sharedInterests[1].similarity, 0.5, 0.001)
            }
        }
        assertTrue(found)
        
        println n1
        println n2
        println n3
        println n4
        println n5
    }
}
