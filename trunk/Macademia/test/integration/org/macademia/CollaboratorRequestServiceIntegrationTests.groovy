package org.macademia

import grails.test.*

/**
 * Authors: Nathaniel Miller and Alex Schneeman
 */
class CollaboratorRequestServiceIntegrationTests extends GrailsUnitTestCase {

    def collaboratorRequestService

    protected void setUp() {
        super.setUp()
    }

    protected void tearDown() {
        super.tearDown()
    }

    void testSave(){
        CollaboratorRequest cr = new CollaboratorRequest(title: "Macademia", creator: Person.findById(1), description: "kld", dateCreated: new Date(), expiration: new Date())
        Interest i1 = new Interest("macademia")
        Interest i2 = new Interest("tagging")
        assertEquals(Interest.findAllByText("macademia").size(),0)
        cr.addToKeywords(i1)
        cr.addToKeywords(i2)
        assertTrue(Interest.findByText("Macademia")==null)
        collaboratorRequestService.save(cr)
        assertEquals(CollaboratorRequest.findAll().size(),1)
        assertTrue(CollaboratorRequest.findByTitle("Macademia")!=null)
        assertTrue(Interest.findByText("macademia")!=null)
    }

}
