package org.macademia

import grails.test.*

/**
 * Authors: Nathaniel Miller and Alex Schneeman
 */
class CollaboratorRequestServiceIntegrationTests extends GrailsUnitTestCase {

    def collaboratorRequestService
    def personService

    protected void setUp() {
        super.setUp()
    }

    protected void tearDown() {
        super.tearDown()
    }

    void testSave(){
        Institution mac = new Institution(name: "Macalester", emailDomain: "@macalester.edu")
        Utils.safeSave(mac)
        Person creator = new Person(fullName: "joe", email: "joe@macalester.edu", institution: mac, department: "Math/CS")
        def user = new User()
        user.username = "joe@macalester.edu"
        user.pass = 'useR123!'
        user.passConfirm = 'useR123!'
        user.enabled = true
        creator.owner = user
        user.profile = creator
        personService.save(creator)
        CollaboratorRequest cr = new CollaboratorRequest(title: "Macademia", creator: creator, description: "kld", dateCreated: new Date(), expiration: new Date())
        Interest i1 = new Interest("macademia")
        Interest i2 = new Interest("tagging")
        assertEquals(Interest.findAllByText("macademia").size(),0)
        cr.addToKeywords(i1)
        cr.addToKeywords(i2)
        assertTrue(Interest.findByText("macademia")==null)
        collaboratorRequestService.save(cr)
        assertEquals(CollaboratorRequest.findAll().size(),1)
        assertTrue(CollaboratorRequest.findByTitle("Macademia")!=null)
        assertTrue(Interest.findByText("macademia")!=null)
    }

}
