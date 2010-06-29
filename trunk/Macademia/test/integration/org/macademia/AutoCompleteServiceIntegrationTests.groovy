package org.macademia

import grails.test.GrailsUnitTestCase

/**
 * Authors: Shilad
 */

class AutoCompleteServiceIntegrationTests extends GrailsUnitTestCase {
    def interestService
    def sessionFactory
    def databaseService
    def autocompleteService
    def similarityService

    protected void setUp() {
        super.setUp()
        databaseService.switchToCopyDB("test")
        similarityService.relationsBuilt = true
        autoCompleteService.init()
    }

    protected void tearDown() {
        super.tearDown()
        databaseService.dropCurrentDB()
        databaseService.changeDB("test")
    }

    void testSimple() {
        Collection<AutocompleteEntity> results = autocompleteService.getInterestAutocomplete('web', 5)
        assertEquals(1, results.size())
        //There is some problem with normalize text for the space character
        Interest interest = new Interest("web 3.0")
        interestService.save(interest)
//        Utils.cleanUpGorm(sessionFactory)
        results = autoCompleteService.getInterestAutocomplete('web', 5)
        assertEquals(2, results.size())
    }

}