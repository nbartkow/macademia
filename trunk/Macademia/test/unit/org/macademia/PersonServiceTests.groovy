package org.macademia

import grails.test.*

class PersonServiceTests extends GrailsUnitTestCase {
    protected void setUp() {
        super.setUp()
    }

    protected void tearDown() {
        super.tearDown()
    }

    void testSomething() {
        mockDomain(Person, [])
        Person p1 = new Person(name : "Shilad", department : "MSCS", email : "ssen@macalester.edu")
        Person p2 = new Person(name : "Tm", department : "MSCS", email : "halverson@macalester.edu")
        p1.save()
        p2.save()
        def service = new PersonService()
        assertNotNull(service.findByEmail("ssen@macalester.edu"))
        assertNotNull(service.findByEmail("halverson@macalester.edu"))
        assertNull(service.findByEmail("halverson@macalesterasd.edu"))
    }
}
