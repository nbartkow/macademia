package org.macademia

import grails.test.*
import org.codehaus.groovy.grails.commons.ConfigurationHolder

/**
 * Authors: Nathaniel Miller and Alex Schneeman
 */
class InterestServiceIntegrationTests extends GrailsUnitTestCase {
    def interestService
    def similarityService
    def databaseService
    
    protected void setUp() {
        super.setUp()
        databaseService.switchToCopyDB((String)ConfigurationHolder.config.dataSource.mongoDbName)

        similarityService.relationsBuilt = true
    }

    protected void tearDown() {
        super.tearDown()
        databaseService.dropCurrentDB()
        databaseService.changeDB((String)ConfigurationHolder.config.dataSource.mongoDbName)
    }

    void testSave() {
        //There is some problem with normalize text for the space character
        Interest interest = new Interest("web 3.0")
        interestService.save(interest)
        assertEquals(Interest.findByText("web 3.0"),interest)
        Person p = Person.findByEmail("guneratne@macalester.edu")
        interest.addToPeople(p)
        interestService.save(interest)
        assertTrue(Interest.findByText("web 3.0").people.contains(p))
        assertTrue(databaseService.getSimilarInterests(Interest.findByText("web 3.0")) != null)
        //assertEquals(InterestRelation.findAllByFirst(interest).size(),10)
    }

}
