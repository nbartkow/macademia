package org.macademia

import grails.test.*

class SimilarityServiceIntegrationTests extends GrailsUnitTestCase {
    def similarityService
    def interestService
    def collaboratorRequestService

    protected void setUp() {
        super.setUp()

        similarityService.refinedThreshold = 0.8
        similarityService.minSimsPerInterest = 1
        similarityService.numSimsPerInterest = 2
        similarityService.maxSimsPerInterest = 3
        //similarityService.analyze()     // rebuild
    }

    protected void tearDown() {
        super.tearDown()
    }


    void testGetSimilarInterests(){
        Interest interest= interestService.findByText("web2.0")
        List<InterestRelation> list= similarityService.getSimilarInterests(interest)
        assertEquals(list.size(),6)
        InterestRelation ir =list.get(0)
        Interest second= interestService.findByText("collaborative computing")
        assertEquals(ir.second, second)
        ir=list.get(3)
        second= interestService.findByText("onlinecommunities")
        assertEquals(ir.second, second)
        ir=list.get(0)
        InterestRelation ir2=list.get(1)
        assertTrue(ir.similarity>ir2.similarity)
    }

    void testCalculateInterestNeighbors() {
        def shilad = Person.findByEmail("ssen@macalester.edu")
        assertNotNull(shilad)
        Interest interest = interestService.findByText("web2.0")
        Graph graph = similarityService.calculateInterestNeighbors(interest, 4)
        assertEquals(graph.edgeSize(),14)
        Edge e1= new Edge(interest:interest, relatedInterest:interestService.findByText("online communities"))
        assertTrue(graph.getAdjacentEdges(interest).contains(e1))
        Edge e2= new Edge(interest:interestService.findByText("online communities"), relatedInterest:interest)
        assertTrue(graph.getAdjacentEdges(interest).contains(e2))
        Edge e3= new Edge(person:shilad ,interest: interestService.findByText("socialnetworks"))
        assertTrue(graph.getAdjacentEdges(shilad).contains(e3))
        Edge e4 = new Edge(interest: interest, relatedInterest:interestService.findByText("globalization"))
        assertTrue(graph.getAdjacentEdges(interest).contains(e4))
        CollaboratorRequest cr = new CollaboratorRequest(title:"Test RFC", description:"This is a test request for collaboratorRequest", creator:Person.findById(1), dateCreated: new Date(), expiration: new Date())
        cr.addToKeywords(interestService.findByText("web20"))
        collaboratorRequestService.save(cr)
        graph= similarityService.calculateInterestNeighbors(interest,10)
        assertEquals(graph.getRequests().size(),1)
        assertEquals(graph.getPeople().size(),3)
        assertEquals(graph.getInterests().size(),7)

    }

    void testCalculatePersonNeighbors () {
        Person p=Person.findById(4)
        Graph graph= similarityService.calculatePersonNeighbors(p,10)
        assertEquals(graph.getAdjacentEdges(p).size(),15)
        Edge e = new Edge(person:Person.findById(1),interest:Interest.findById(42), relatedInterest:Interest.findById(5))
        assertFalse(graph.getAdjacentEdges(Person.findById(1)).contains(e))
        e=new Edge(person:Person.findById(4),interest:Interest.findById(42))
        assertTrue(graph.getAdjacentEdges(Person.findById(4)).contains(e))
        e=new Edge(person:Person.findById(3),interest:Interest.findById(5), relatedInterest:Interest.findById(30))
        assertTrue(graph.getAdjacentEdges(Person.findById(3)).contains(e))
        assertEquals(graph.getPeople().size(),5)
        assertEquals(graph.getInterests().size(),15)
        CollaboratorRequest cr = new CollaboratorRequest(title:"Test RFC", description:"This is a test request for collaboratorRequest", creator:Person.findById(1), dateCreated: new Date(), expiration: new Date())
        cr.addToKeywords(Interest.findById(5))
        collaboratorRequestService.save(cr)
        graph= similarityService.calculatePersonNeighbors(p,10)
        assertEquals(graph.getRequests().size(),1)
        assertEquals(graph.getPeople().size(),5)
        assertEquals(graph.getInterests().size(),15)
    }

    void testCalculateRequestNeighbors () {
        CollaboratorRequest cr = new CollaboratorRequest(title:"Test RFC", description:"This is a test request for collaboratorRequest", creator:Person.findById(1), dateCreated: new Date(), expiration: new Date())
        cr.addToKeywords(Interest.findById(5))
        cr.addToKeywords(Interest.findById(1))
        cr.addToKeywords(Interest.findById(2))
        cr.addToKeywords(Interest.findById(3))
        collaboratorRequestService.save(cr)
        Graph graph= similarityService.calculateRequestNeighbors(cr,10)
        assertTrue(graph.getInterests().contains(Interest.findById(1)))
        assertTrue(graph.getInterests().contains(Interest.findById(2)))
        assertTrue(graph.getInterests().contains(Interest.findById(3)))
        assertTrue(graph.getInterests().contains(Interest.findById(5)))
        assertTrue(graph.getPeople().contains(Person.findById(1)))
        assertTrue(graph.getPeople().contains(Person.findById(2)))
        assertTrue(graph.getPeople().contains(Person.findById(3)))
        assertTrue(graph.getPeople().contains(Person.findById(4)))
        assertEquals(graph.getPeople().size(),5)
        assertEquals(graph.getInterests().size(),4)
        assertEquals(graph.getRequests().size(),1)
        assertEquals(graph.getAdjacentEdges(cr).size(),4)
        assertEquals(graph.getAdjacentEdges(Person.findById(4)).size(),4)
        assertEquals(graph.getAdjacentEdges(Person.findById(3)).size(),2)
        assertEquals(graph.getAdjacentEdges(Person.findById(2)).size(),2)
        assertEquals(graph.getAdjacentEdges(Interest.findById(1)).size(),7)
        assertEquals(graph.getAdjacentEdges(Interest.findById(3)).size(),8)
        assertEquals(graph.getAdjacentEdges(Interest.findById(2)).size(),2)
        Edge e1 = new Edge(person:Person.findById(1),interest:Interest.findById(5),relatedInterest:Interest.findById(14))
        Edge e2 = new Edge(person:Person.findById(1),interest:Interest.findById(5),relatedInterest:Interest.findById(3))
        Edge e3 = new Edge(person:Person.findById(1),interest:Interest.findById(5),relatedInterest:Interest.findById(2))
        assertTrue(graph.getAdjacentEdges(Person.findById(1)).contains(e1))
        assertFalse(graph.getAdjacentEdges(Person.findById(1)).contains(e2))
        assertFalse(graph.getAdjacentEdges(Person.findById(1)).contains(e3))


    }

    void testBuildInterestRelations () {
        Interest i = new Interest("GIS")
        Interest i2= new Interest("GIS (Geographic Information System)")
        Interest i3= new Interest("e-democracy")
        i.save()
        i2.save()
        i3.save()
        Interest interest= interestService.findByText("web2.0")
        List<InterestRelation> list= InterestRelation.findAllByFirst(interest, [sort:"similarity", order:"desc"])
        assertEquals(list.size(),11)
        interestService.buildDocuments(i)
        interestService.buildDocuments(i2)
        interestService.buildDocuments(i3)
        similarityService.buildInterestRelations(i)
        similarityService.buildInterestRelations(i2)
        similarityService.buildInterestRelations(i3)
        assertTrue(InterestRelation.findByFirst(i)!=null)
        assertTrue(InterestRelation.findAllByFirst(i, [sort:"similarity", order:"desc"]).get(0).similarity>0.2)
        List<InterestRelation> nlist= InterestRelation.findAllByFirst(interest)
        Set<InterestRelation> nset= new HashSet<InterestRelation>()
        for(InterestRelation ir : nlist){
            nset.add(ir)
        }
        assertEquals(nset.size(),14)
        List<InterestRelation> nlist2= InterestRelation.findAllByFirst(i, [sort:"similarity", order:"desc"])
        assertEquals(nlist2.size(),13)
        InterestRelation ir = nlist2.get(0)
        InterestRelation ir2= new InterestRelation(first:ir.second, second:ir.first, similarity:ir.similarity)
        List<InterestRelation> nlist3=InterestRelation.findAllByFirst(ir.second)
        Set<InterestRelation> nset2= new HashSet<InterestRelation>()
        for(InterestRelation iir: nlist3){
            nset2.add(iir)
        }
        assertTrue(nset2.contains(ir2))

    }
}
