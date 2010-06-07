package org.macademia

import grails.test.*

class SearchIntegrationTests extends GrailsUnitTestCase {
  def searchService
  def personService
  def autoCompleteService

  protected void setUp() {
    super.setUp()
  }

  protected void tearDown() {
    super.tearDown()
  }

  void testPersonSearch() {
    def shilad = personService.findByEmail("ssen@macalester.edu")
    def arjun = personService.findByEmail("guneratne@macalester.edu")
    def dianna = personService.findByEmail("shandy@macalester.edu")

    assertNotNull(shilad)
    assertNotNull(arjun)
    assertNotNull(dianna)
    autoCompleteService.addPerson(shilad)
    assertEquals(autoCompleteService.getPersonAutocomplete("sh" , 1).size() , 1)
    def people = searchService.searchPeople("shilad")
    assertEquals(people.size(), 1)
    assertEquals(people[0], shilad)
    people = searchService.searchPeople("ssen@macalester.edu")
    assertEquals(people.size(), 1)
    assertEquals(people[0], shilad)
    people = searchService.searchPeople("anthropology")
    assertEquals(people.size(), 2)
    assertTrue(people[0].equals(arjun) || people[1].equals(arjun))
    assertTrue(people[0].equals(dianna) || people[1].equals(dianna))
  }

  void testInterestSearch() {
    def interests = searchService.searchInterests("foo")
    assertEquals(interests.size(), 0)
    interests = searchService.searchInterests("web20")
    assertEquals(interests.size(), 1)
    for (Interest interest: interests){
      autoCompleteService.addInterest(interest)
    }
    assertEquals(autoCompleteService.getInterestAutocomplete("web" , 2).size() , 1)

    interests = searchService.searchInterests("psychology")

    assertEquals(interests.size(), 5)
  }

  void testInstitutionSearch() {
    Institution institution = Institution.findByName("macalester")
    if(institution == null){
      institution = new Institution(name: "macalester", emailDomain: "@macalester.edu")
      institution.save()
    }
    def institutions = searchService.searchInstitutions("macalester")
    assertEquals(institutions.size(), 1)
    autoCompleteService.addInstitution(institution)
    assertEquals(autoCompleteService.getInstitutionAutocomplete("mac",2).size() , 1)

  }


}
