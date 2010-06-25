package org.macademia

import grails.test.*
import grails.plugins.nimble.InstanceGenerator

/**
 * Authors: Nathaniel Miller and Alex Schneeman
 */
class InterestServiceIntegrationTests extends GrailsUnitTestCase {
    def interestService
    def similarityService
    def databaseService
    
    protected void setUp() {
        super.setUp()
        databaseService.switchToCopyDB("test")

        similarityService.relationsBuilt = true
    }

    protected void tearDown() {
        super.tearDown()
        databaseService.dropCurrentDB()
        databaseService.changeDB("test")
    }

    void testSave() {
        //There is some problem with normalize text for the space character
        Interest interest = new Interest("web 3.0")
        interestService.save(interest)
        assertEquals(Interest.findByText("web 3.0"),interest)
        Person p = Person.findById(3)
        interest.addToPeople(p)
        interestService.save(interest)
        assertTrue(Interest.findByText("web 3.0").people.contains(p))
        assertEquals(InterestRelation.findAllByFirst(interest).size(),10)

    }

}
