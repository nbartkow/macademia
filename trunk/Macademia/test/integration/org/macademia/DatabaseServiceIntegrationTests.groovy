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
        databaseService.clearCache()
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
        Set<Long> interests = databaseService.getUserInterests(1)
        assertEquals(interests.size(),15)
        assertEquals(interestService.findByText("online communities").text,"online communities")
    }

//    /*
    void testGetUserInstitution() {
        //databaseService.addUser(Person.findByEmail("ssen@macalester.edu"))
        assertEquals(databaseService.getUserInstitution(1),Institution.findByName("Macalester College").id)
    }

    void testGetUserInterests() {
        Set<Long> interestSet = databaseService.getUserInterests(1)
        ArrayList<Long> interests = new ArrayList<Long>();
        interests.addAll(interestSet);
        interests.sort()
        for (int i = 0; i < 15; i++) {
            assertEquals(interests.get(i), i + 1)
        }
        assertEquals(interests.size(),15)
        assertEquals(interests.get(1),2)
        assertEquals(interests.get(12),13)
    }

    /**
     * Test to see if the "addToInterests()" method works by adding similar interests to "web2.0"
     * Also tests if removeLowestSimilarity works
     */
    void testAddToInterests() {
        int sizeOne = databaseService.getSimilarInterests(interestService.findByText("web2.0")).size()
        databaseService.addToInterests(interestService.findByText("web2.0"),interestService.findByText("social networking"),0.01812)
        assertEquals(databaseService.getSimilarInterests(interestService.findByText("web2.0")).size(), sizeOne+1)
        databaseService.addToInterests(interestService.findByText("web2.0"),interestService.findByText("ngos"),0.01812)
        assertEquals(databaseService.getSimilarInterests(interestService.findByText("web2.0")).size(), sizeOne+2)
        databaseService.removeLowestSimilarity(interestService.findByText("web2.0"))
        assertEquals(databaseService.getSimilarInterests(interestService.findByText("web2.0")).size(), sizeOne+1)
    }

    void testAddCollaboratorRequests(){
        CollaboratorRequest rfc = new CollaboratorRequest(title:"Test RFC", description:"This is a test request for collaboratorRequest", creator:Person.findById(5), dateCreated: new Date(), expiration: new Date())
        rfc.addToKeywords(interestService.findByText("web2.0"))
        rfc.addToKeywords(interestService.findByText("social networking"))
        collaboratorRequestService.save(rfc)
        long id = CollaboratorRequest.findByCreator(rfc.creator).id
        assertEquals(databaseService.getCollaboratorRequestInstitution(id), rfc.creator.institution.id)
        assertEquals(databaseService.getCollaboratorRequestCreator(id), rfc.creator.id)
        assertEquals(databaseService.getRequestKeywords(id).size(),2)
        databaseService.removeCollaboratorRequest(rfc)
    }

    void testReplaceLowestSimilarity() {
        databaseService.addToInterests(interestService.findByText("web2.0"),interestService.findByText("ngos"),0.01812)
        int sizeOne = databaseService.getSimilarInterests(interestService.findByText("web2.0")).size()
        databaseService.replaceLowestSimilarity(interestService.findByText("web2.0"), interestService.findByText("nationalism"), 0.2)
        assertEquals(databaseService.getSimilarInterests(interestService.findByText("web2.0")).size(),sizeOne)
        databaseService.removeSimilarInterest(interestService.findByText("web2.0"), interestService.findByText("nationalism"))
        assertEquals(databaseService.getSimilarInterests(interestService.findByText("web2.0")).size(),sizeOne-1)
    }
}