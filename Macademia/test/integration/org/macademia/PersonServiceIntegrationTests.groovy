package org.macademia

import grails.test.GrailsUnitTestCase
import grails.plugins.nimble.InstanceGenerator

/**
 * Created by IntelliJ IDEA.
 * User: aschneem
 * Date: Jun 8, 2010
 * Time: 1:07:29 PM
 * To change this template use File | Settings | File Templates.
 */
class PersonServiceIntegrationTests extends GrailsUnitTestCase {
    def personService
   // def googleServiceProxy
   // def wikipediaServiceProxy
    
    protected void setUp() {
        super.setUp()
    }

    protected void tearDown() {
        super.tearDown()
    }

    void testSave(){
        Person p = new Person(owner: InstanceGenerator.user(), fullName:"Alicia Johnson",email:"ajohns24@macalester.edu",department:"Mathematics, Statistics, and Computer Science" )
        Interest i1 = new Interest("statistics")
        Interest i2 = new Interest("applied statistics")
        Interest i3 = new Interest("probability")
        Interest i4 = new Interest("Markov chain Monte Carlo")
        Interest i5 = new Interest("public health")
        assertEquals(Interest.findAllByText("Markov chain Monte Carlo").size(),0)
        p.addToInterests(i1)
        p.addToInterests(i2)
        p.addToInterests(i3)
        p.addToInterests(i4)
        p.addToInterests(i5)
        assertTrue(Person.findByEmail("ajohns24@macalester.edu")==null)
        personService.save(p)
        assertTrue(Person.findByEmail("ajohns24@macalester.edu")!=null)
        assertTrue(Interest.findByText("Markov chain Monte Carlo")!=null)

    }

}
