package org.macademia

import grails.test.GrailsUnitTestCase

/**
 * Authors: Shilad
 */

class AutocompleteServiceIntegrationTests extends GrailsUnitTestCase {
    def interestService
    def sessionFactory
    def databaseService
    def autocompleteService
    def similarityService

    protected void setUp() {
        super.setUp()
        databaseService.switchToCopyDB("test")
        similarityService.relationsBuilt = true
        autocompleteService.init()
    }

    protected void tearDown() {
        super.tearDown()
        databaseService.dropCurrentDB()
        databaseService.changeDB("test")
    }

    void testSimple() {
        Interest i = Interest.findByText("web 3.0")
        assertNull(i)
        Collection<AutocompleteEntity> results = autocompleteService.getInterestAutocomplete('web', 5)
        System.out.println(results)
        assertEquals(1, results.size())
        System.out.println(results)
        //There is some problem with normalize text for the space character
        Interest interest = new Interest("web 3.0")
        interestService.save(interest)
        Utils.cleanUpGorm(sessionFactory)
        
        results = autocompleteService.getInterestAutocomplete('web', 5)
        assertEquals(2, results.size())
    }

}