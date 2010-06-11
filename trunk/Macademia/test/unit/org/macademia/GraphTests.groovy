package org.macademia

import grails.test.GrailsUnitTestCase

/**
 * Authors: Nathaniel Miller and Alex Schneeman
 */
class GraphTests extends GrailsUnitTestCase {
    protected void setUp() {
        super.setUp()
    }

    protected void tearDown() {
        super.tearDown()
    }

    public void testGraph(){
        Graph graph=new Graph()
        Person p= new Person(name:"foo" , email:"1", department:"CS")
        Interest i= new Interest("Web 2.0")
        Interest i2 = new Interest("Anthropology")
        CollaboratorRequest cr = new CollaboratorRequest(title:"lakd")
        Edge e1 = new Edge(person:p, interest:i)
        Edge e2 = new Edge(interest:i, relatedInterest:i2)
        Edge e3 = new Edge(interest:i, request: cr)
        graph.addEdge(e1)
        graph.addEdge(e2)
        graph.addEdge(e3)
        assertTrue(graph.getAdjacentEdges(p).contains(e1))
        assertTrue(graph.getAdjacentEdges(i).contains(e2))
        assertTrue(graph.getPeople().contains(p))
        assertEquals(graph.getPeople().size(),1)
        assertTrue(graph.getInterests().contains(i))
        assertEquals(graph.getInterests().size(),2)
        assertTrue(graph.containsNode(p))
        assertTrue(graph.containsNode(i))
        assertTrue(graph.containsNode(cr))
        assertTrue(graph.getAdjacentEdges(cr).contains(e3))
        assertTrue(graph.getAdjacentEdges(i).contains(e3))
        assertEquals(graph.interestMap.size() + graph.personMap.size(),3)
        Set<Edge> edges = new HashSet<Edge>()
        for (Set<Edge> s : graph.interestMap.values()) {
            for (Edge e : s) {
                edges.add(e)
            } 
        }
        assertEquals(edges.size(),3)

    }
}