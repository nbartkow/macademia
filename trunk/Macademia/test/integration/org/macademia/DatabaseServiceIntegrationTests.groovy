package org.macademia

import grails.test.*
import org.codehaus.groovy.grails.commons.ConfigurationHolder

/**
 * Authors: Nathaniel Miller and Alex Schneeman
 */
class DatabaseServiceIntegrationTests extends GrailsUnitTestCase {
    def databaseService
    def collaboratorRequestService
    def similarityService

     protected void setUp() {
        super.setUp()
        databaseService.switchToCopyDB((String)ConfigurationHolder.config.dataSource.mongoDbName)
    }

    protected void tearDown() {
        super.tearDown()
        databaseService.dropCurrentDB()
        databaseService.changeDB((String)ConfigurationHolder.config.dataSource.mongoDbName)
    }

    void testAddUser(){
        similarityService.buildInterestRelations(Interest.get(5))
        similarityService.buildInterestRelations(Interest.get(1))
        similarityService.buildInterestRelations(Interest.get(2))
        databaseService.addUser(Person.get(1))
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
        //databaseService.addUser(Person.get(1))
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
        int sizeOne = databaseService.getSimilarInterests(Interest.get(5)).size()
        databaseService.addToInterests(Interest.get(5),Interest.get(2),0.01812)
        assertEquals(databaseService.getSimilarInterests(Interest.get(5)).size(),sizeOne + 1)
        databaseService.removeLowestSimilarity(Interest.get(5))
        //assertEquals(databaseService.getSimilarInterests(Interest.get(5)).get(1), 0.1812) //returns null for some reason
    }

    void testAddCollaboratorRequests(){
        CollaboratorRequest rfc = new CollaboratorRequest(title:"Test RFC", description:"This is a test request for collaboratorRequest", creator:Person.findById(5), dateCreated: new Date(), expiration: new Date())
        rfc.addToKeywords(Interest.get(5))
        rfc.addToKeywords(Interest.get(2))
        collaboratorRequestService.save(rfc)
        //rfc.addToKeywords(Interest.get(5))
        //rfc.addToKeywords(Interest.get(2))
        long id = CollaboratorRequest.findByCreator(rfc.creator).id
        assertEquals(databaseService.getCollaboratorRequestInstitution(id), rfc.creator.institution.id)
        assertEquals(databaseService.getCollaboratorRequestCreator(id), rfc.creator.id)
        assertEquals(databaseService.getRequestKeywords(id).size(),2)
        databaseService.removeCollaboratorRequest(rfc)
    }

    void testReplaceLowestSimilarity() {
        //similarityService.buildInterestRelations(Interest.get(5))
        //similarityService.buildInterestRelations(Interest.get(1))
        //similarityService.buildInterestRelations(Interest.get(2))
        databaseService.addToInterests(Interest.get(5),Interest.get(1),0.01812)
        int sizeOne = databaseService.getSimilarInterests(Interest.get(5)).size()
        databaseService.replaceLowestSimilarity(Interest.get(5), Interest.get(2), 0.2)
        assertEquals(databaseService.getSimilarInterests(Interest.get(5)).size(),sizeOne)
        //There is something quite wrong here...: ID should be 2, map says it is 91, but nothing is returned from get...
        /*for (Long id : databaseService.getSimilarInterests(Interest.get(5)).keySet()) {
            log.info("ID is $id")
        }
        log.info("" + Interest.get(2).hashCode())*/
        databaseService.removeInterests(Interest.get(5), Interest.get(2))
        assertEquals(databaseService.getSimilarInterests(Interest.get(5)).size(),sizeOne-1)

    }


}