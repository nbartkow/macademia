package org.macademia

import edu.macalester.acs.AutocompleteTree
import edu.macalester.acs.AutocompleteEntry

/**
 * Created by IntelliJ IDEA.
 * User: equeirosnunes
 * Date: Jun 3, 2010
 * Time: 3:24:04 PM
 * This is a wrapper for the autocomplete plugin
 */
class AutoCompleteService {

  //this is the maximum number of autocomplete results
  static int MAX_NUMBER_RES = 10
  AutocompleteTree<String, Person> personTree = new AutocompleteTree<String, Person>()
  AutocompleteTree<String,Institution> institutionTree = new  AutocompleteTree<String,Institution>()
  AutocompleteTree<String,Interest> interestTree = new  AutocompleteTree<String,Interest>()

  // creates a tree for person class
  def addPerson(Person person){

    personTree.add(person.email,person)

  }

  //creates and autocomplete  tree for intitutions
  def addInstitution(Institution institution){
    institutionTree.add(institution.emailDomain, institution)
  }

  def addInterest(Interest interest){
    interestTree.add(interest.normalizedText, interest)

  }

  def getPersonAutocomplete(String query,int MAX_NUMBER_RES){
    // Returns the top three cities that start with "ch" ordered by score.
    List<Person> people = new ArrayList<Person> ()
    SortedSet<AutocompleteEntry<String, Person>> results = personTree.autocomplete(query, MAX_NUMBER_RES)
    for (AutocompleteEntry<String, Person> entry : results) {
      people.add((Person)entry.getValue())
      System.out.println("Person " + entry.getValue() + " with score " + entry.getScore())
    }

    return people

  }

  def getInstitutionAutocomplete(String query,int MAX_NUMBER_RES){
    // Returns the top three cities that start with "ch" ordered by score.
    List<Institution> institutions = new ArrayList<Institution> ()
    SortedSet<AutocompleteEntry<String, Institution>> results = institutionTree.autocomplete(query, MAX_NUMBER_RES)
    for (AutocompleteEntry<String, Institution> entry : results) {
      institutions.add((Institution)entry.getValue())
      System.out.println("Institution " + entry.getValue() + " with score " + entry.getScore())
    }

    return institutions

  }

  def getInterestAutocomplete(String query,int MAX_NUMBER_RES){
    // Returns the top three cities that start with "ch" ordered by score.
    List<Interest> interests = new ArrayList<Interest> ()
    SortedSet<AutocompleteEntry<String, Interest>> results = interestTree.autocomplete(query, MAX_NUMBER_RES)
    for (AutocompleteEntry<String, Interest> entry : results) {
      interests.add((Interest)entry.getValue())
      System.out.println("Interest " + entry.getValue() + " with score " + entry.getScore())
    }

    return interests

  }
}



