package org.macademia

import grails.test.GrailsUnitTestCase
import grails.plugins.nimble.InstanceGenerator
import org.codehaus.groovy.grails.commons.ConfigurationHolder

/**
 * Created by IntelliJ IDEA.
 * User: aschneem
 * Date: Jun 8, 2010
 * Time: 1:07:29 PM
 * To change this template use File | Settings | File Templates.
 */
class PersonServiceIntegrationTests extends GrailsUnitTestCase {
    def personService
    def databaseService
   // def googleServiceProxy
   // def wikipediaServiceProxy
    
    protected void setUp() {
        super.setUp()
        databaseService.switchToCopyDB((String)ConfigurationHolder.config.dataSource.mongoDbName)
    }

    protected void tearDown() {
        super.tearDown()
        databaseService.dropCurrentDB()
        databaseService.changeDB((String)ConfigurationHolder.config.dataSource.mongoDbName)
    }

    void testSave(){
        Institution mac = new Institution(name: "Macalester", emailDomain: "@macalester.edu")
        Utils.safeSave(mac)
        Person p = new Person(institution: mac, owner: InstanceGenerator.user(), fullName:"Alicia Johnson",email:"ajohns24@macalester.edu",department:"Mathematics, Statistics, and Computer Science" )
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
