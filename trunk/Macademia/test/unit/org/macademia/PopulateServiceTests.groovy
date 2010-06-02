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

    public static mockLogger(Class klazz) {
        def logger = new Expando(
                debug: { println it },
                info: { println it },
                warn: { println it },
                error: { println it })
        klazz.metaClass.getLog = { -> logger }
    }
}
