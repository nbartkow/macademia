package org.macademia

import grails.test.*

class PopulateServiceTests extends GrailsUnitTestCase {
    protected void setUp() {
        super.setUp()
    }

    protected void tearDown() {
        super.tearDown()
    }

    void testReadPeople() {
        def people = []
        def interests = []

        mockDomain(Person, people)
        mockDomain(Interest, interests)
        mockLogger(PopulateService.class)

        def interestService = new InterestService()
        def populateService = new PopulateService()
        populateService.interestService = new InterestService()
        populateService.readPeople(new File("db/test/people.txt"))

        assertEquals(Person.count(), 5)
        assertEquals(Interest.count(), 69) // Shilad and Diane share web20 and politics

        def shilad = Person.findByEmail("ssen@macalester.edu")
        assertNotNull(shilad)
        assertEquals(shilad.department, "Math/CS")
        assertEquals(shilad.name, "Shilad Sen")

        def diane = Person.findByEmail("michelfelder@macalester.edu")
        assertNotNull(diane)

        def politics = interestService.findByText("PO LITICS")
        def web20 = interestService.findByText("WEB 2.0")
        def africa = interestService.findByText("A FRICA")
        assertNotNull(politics)
        assertNotNull(web20)
        assertNotNull(africa)

        assertTrue(shilad.interests.contains(politics))
        assertTrue(shilad.interests.contains(web20))
        assertFalse(shilad.interests.contains(africa))
        assertFalse(diane.interests.contains(politics))
        assertTrue(diane.interests.contains(web20))
        assertFalse(diane.interests.contains(africa))
    }


    void testReadSims() {
        def people = []
        def interests = []
        def interestRelations = []

        mockDomain(Person, people)
        mockDomain(Interest, interests)
        mockDomain(InterestRelation, interestRelations)
        mockLogger(PopulateService.class)

        def interestService = new InterestService()
        def populateService = new PopulateService()
        populateService.interestService = new InterestService()
        populateService.readPeople(new File("db/test/people.txt"))
        populateService.readSimilarities(new File("db/test/sims.txt"))

        assertEquals(Person.count(), 5)
        assertEquals(Interest.count(), 69) // Shilad and Diane share web20 and politics
        assertEquals(InterestRelation.count(), 8)

        def sims = interestService.findSimilarities("foo")
        assertEquals(sims.size(), 0)
        sims = interestService.findSimilarities("POL ITICS")
        assertEquals(sims.size(), 1)
        sims = interestService.findSimilarities("citi&zenSHIP")
        assertEquals(sims.size(), 2)
        def simMap = interestService.findSimilaritiesAsMap("citi&zenSHIP")
        assertEquals(simMap.size(), 2)
        println simMap
        assertEquals(simMap['politics'], 0.7, 0.001)
        assertEquals(simMap['law'], 0.4, 0.0001)

    }

    public static mockLogger(Class klazz) {
        def logger = new Expando(
                debug: { println it },
                info: { println it },
                warn: { println it },
                error: { println it })
        klazz.metaClass.getLog = { -> logger }
    }
}
