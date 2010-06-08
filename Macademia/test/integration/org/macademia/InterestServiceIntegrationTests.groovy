package org.macademia

import grails.test.*

/**
 * Authors: Nathaniel Miller and Alex Schneeman
 */
class InterestServiceIntegrationTests extends GrailsUnitTestCase {
  def interestService
  def similarityService

  protected void setUp() {
    super.setUp()
  }

  protected void tearDown() {
    super.tearDown()
  }

  void testSave() {
    Person p = new Person(name: "foo", email: "bar", department: "CS")
    //There is some problem with normalize text for the space character
    Interest interest = new Interest("web 3.0")
    interestService.save(interest)
    assertEquals(Interest.findByText("web 3.0"),interest)
    interest.addToPeople(p)
    interestService.save(interest)
    assertTrue(Interest.findByText("web 3.0").people.contains(p))
    assertEquals(InterestRelation.findAllByFirst(interest).size(),11)

  }

}
