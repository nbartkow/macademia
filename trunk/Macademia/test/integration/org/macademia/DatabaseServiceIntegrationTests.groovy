package org.macademia

import grails.test.*

/**
 * Authors: Nathaniel Miller and Alex Schneeman
 */
class DatabaseServiceIntegrationTests extends GrailsUnitTestCase {
    def databaseService
    def collaboratorRequestService
    def similarityService

     protected void setUp() {
        super.setUp()
        databaseService.switchToCopyDB("test")
    }

    protected void tearDown() {
        super.tearDown()
        databaseService.dropCurrentDB()
        databaseService.changeDB("test")
    }

    void testAddUser(){
        similarityService.buildInterestRelations(Interest.findById(5))
        similarityService.buildInterestRelations(Interest.findById(1))
        similarityService.buildInterestRelations(Interest.findById(2))
        databaseService.addUser(Person.findById(1))
        assertEquals(databaseService.getUserInstitution(1),Institution.findByName("Macalester College").id)
        ArrayList<Long> interests = databaseService.getUserInterests(1)
        Set<Long> set =new HashSet<Long>()
        for(Long id : interests){
            set.add(id)
        }
        assertEquals(interests.size(),15)
        interests.sort()
        assertEquals(interests.get(1),new Long(2))
    }

    void testGetUserInstitution() {
        //databaseService.addUser(Person.findById(1))
        assertEquals(databaseService.getUserInstitution(1),Institution.findByName("Macalester College").id)
    }

    void testGetUserInterests() {
        ArrayList<Long> interests = databaseService.getUserInterests(1)
        interests.sort()
        for (int i = 0; i < 15; i++) {
            assertEquals(interests.get(i), i + 1)
        }
        assertEquals(interests.size(),15)
        assertEquals(interests.get(1),2)
        assertEquals(interests.get(12),13)
    }

    void testAddToInterests() {
        databaseService.addToInterests(Interest.findById(5),Interest.findById(2),0.01812)
        assertEquals(databaseService.getSimilarInterests(Interest.findById(5)).size(),10)
        databaseService.removeLowestSimilarity(Interest.findById(5))
        //assertEquals(databaseService.getSimilarInterests(Interest.findById(5)).get(1), 0.1812) //returns null for some reason
    }

    void testAddCollaboratorRequests(){
        CollaboratorRequest rfc = new CollaboratorRequest(title:"Test RFC", description:"This is a test request for collaboratorRequest", creator:Person.findById(5), dateCreated: new Date(), expiration: new Date())
        rfc.addToKeywords(Interest.findById(5))
        rfc.addToKeywords(Interest.findById(2))
        collaboratorRequestService.save(rfc)
        //rfc.addToKeywords(Interest.findById(5))
        //rfc.addToKeywords(Interest.findById(2))
        long id = CollaboratorRequest.findByCreator(rfc.creator).id
        assertEquals(databaseService.getCollaboratorRequestInstitution(id), rfc.creator.institution.id)
        assertEquals(databaseService.getCollaboratorRequestCreator(id), rfc.creator.id)
        assertEquals(databaseService.getCollaboratorRequestKeywords(id).size(),2)
        databaseService.removeCollaboratorRequest(rfc)
    }

    void testReplaceLowestSimilarity() {
        //similarityService.buildInterestRelations(Interest.findById(5))
        //similarityService.buildInterestRelations(Interest.findById(1))
        //similarityService.buildInterestRelations(Interest.findById(2))
        databaseService.addToInterests(Interest.findById(5),Interest.findById(1),0.01812)
        assertEquals(databaseService.getSimilarInterests(Interest.findById(5)).size(),9)
        databaseService.replaceLowestSimilarity(Interest.findById(5), Interest.findById(2), 0.2)
        assertEquals(databaseService.getSimilarInterests(Interest.findById(5)).size(),9)
        //There is something quite wrong here...: ID should be 2, map says it is 91, but nothing is returned from get...
        /*for (Long id : databaseService.getSimilarInterests(Interest.findById(5)).keySet()) {
            log.info("ID is $id")
        }
        log.info("" + Interest.findById(2).hashCode())*/
        databaseService.removeInterests(Interest.findById(5), Interest.findById(2))
        assertEquals(databaseService.getSimilarInterests(Interest.findById(5)).size(),8)

    }


}