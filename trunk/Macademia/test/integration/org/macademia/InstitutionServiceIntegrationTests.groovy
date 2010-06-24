package org.macademia

import grails.test.*

/**
 * Authors: Nathaniel Miller and Alex Schneeman
 */
class InstitutionServiceIntegrationTests extends GrailsUnitTestCase {
    def institutionService

    protected void setUp() {
        super.setUp()
    }

    protected void tearDown() {
        super.tearDown()
    }

    void testFindByEmailDomain(){
        Institution mac = institutionService.findByEmailDomain("macalester.edu")
        assertNotNull(mac)
    }
}
