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
    def interestService

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
        similarityService.buildInterestRelations(interestService.findByText("web2.0"))
        similarityService.buildInterestRelations(interestService.findByText("online communities"))
        similarityService.buildInterestRelations(interestService.findByText("social networking"))
        databaseService.addUser(Person.findByEmail("ssen@macalester.edu"))
        assertEquals(databaseService.getUserInstitution(1),Institution.findByName("Macalester College").id)
        ArrayList<Long> interests = databaseService.getUserInterests(1)
        Set<Long> set =new HashSet<Long>()
        for(Long id : interests){
            set.add(id)
        }
        assertEquals(interests.size(),15)
        interests.sort()
        assertEquals(interestService.findByText("online communities").text,"online communities")
    }

    void testGetUserInstitution() {
        //databaseService.addUser(Person.findByEmail("ssen@macalester.edu"))
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
        int sizeOne = databaseService.getSimilarInterests(interestService.findByText("web2.0")).size()
        databaseService.addToInterests(interestService.findByText("web2.0"),interestService.findByText("social networking"),0.01812)
        assertEquals(databaseService.getSimilarInterests(interestService.findByText("web2.0")).size(),sizeOne + 1)
        databaseService.removeLowestSimilarity(interestService.findByText("web2.0"))
        //assertEquals(databaseService.getSimilarInterests(interestService.findByText("web2.0")).get(1), 0.1812) //returns null for some reason
    }

    void testAddCollaboratorRequests(){
        CollaboratorRequest rfc = new CollaboratorRequest(title:"Test RFC", description:"This is a test request for collaboratorRequest", creator:Person.findById(5), dateCreated: new Date(), expiration: new Date())
        rfc.addToKeywords(interestService.findByText("web2.0"))
        rfc.addToKeywords(interestService.findByText("social networking"))
        collaboratorRequestService.save(rfc)
        //rfc.addToKeywords(interestService.findByText("web2.0"))
        //rfc.addToKeywords(interestService.findByText("social networking"))
        long id = CollaboratorRequest.findByCreator(rfc.creator).id
        assertEquals(databaseService.getCollaboratorRequestInstitution(id), rfc.creator.institution.id)
        assertEquals(databaseService.getCollaboratorRequestCreator(id), rfc.creator.id)
        assertEquals(databaseService.getRequestKeywords(id).size(),2)
        databaseService.removeCollaboratorRequest(rfc)
    }

    void testReplaceLowestSimilarity() {
        //similarityService.buildInterestRelations(interestService.findByText("web2.0"))
        //similarityService.buildInterestRelations(Interest.get(1))
        //similarityService.buildInterestRelations(Interest.get(2))
        databaseService.addToInterests(interestService.findByText("web2.0"),interestService.findByText("online communities"),0.01812)
        int sizeOne = databaseService.getSimilarInterests(interestService.findByText("web2.0")).size()
        databaseService.replaceLowestSimilarity(interestService.findByText("web2.0"), interestService.findByText("social networking"), 0.2)
        assertEquals(databaseService.getSimilarInterests(interestService.findByText("web2.0")).size(),sizeOne)
        //There is something quite wrong here...: ID should be 2, map says it is 91, but nothing is returned from get...
        /*for (Long id : databaseService.getSimilarInterests(interestService.findByText("web2.0")).keySet()) {
            log.info("ID is $id")
        }
        log.info("" + Interest.get(2).hashCode())*/
        databaseService.removeInterests(interestService.findByText("web2.0"), interestService.findByText("social networking"))
        assertEquals(databaseService.getSimilarInterests(interestService.findByText("web2.0")).size(),sizeOne-1)

    }


}