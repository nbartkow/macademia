package org.macademia

import edu.macalester.acs.AutocompleteTree
import edu.macalester.acs.AutocompleteEntry
import org.hibernate.event.PostInsertEventListener
import org.hibernate.event.PostInsertEvent
import org.hibernate.SessionFactory

/**
 * Created by IntelliJ IDEA.
 * User: equeirosnunes, shilad
 * This is a wrapper for the autocomplete plugin
 */
class AutoCompleteService implements PostInsertEventListener {

    //this is the maximum number of autocomplete results
    static int MAX_NUMBER_RES = 10
    AutocompleteTree<Long, NamedEntity> personTree = new AutocompleteTree<Long, NamedEntity>()
    AutocompleteTree<Long, NamedEntity> institutionTree = new AutocompleteTree<Long, NamedEntity>()
    AutocompleteTree<Long, NamedEntity> interestTree = new AutocompleteTree<Long, NamedEntity>()
    SessionFactory sessionFactory

    def init() {
        log.info("processing autocomplete people...")
        Person.findAll().each { addPerson(it) }
        log.info("processing autocomplete institution...")
        Institution.findAll().each { addInstitution(it) }
        log.info("processing autocomplete interest...")
        Interest.findAll().each { addInterest(it) }
        
        sessionFactory.eventListeners.with {
            postInsertEventListeners = addListener(sessionFactory.eventListeners.postInsertEventListeners)
        }
    }

    Object[] addListener(final Object[] array) {
        def size = array?.length ?: 0
        def expanded = new Object[size + 1]
        if (array) {
            System.arraycopy(array, 0, expanded, 0, array.length)
        }
        expanded[-1] = this
        return expanded
    }

    // creates a tree for person class

    void onPostInsert(PostInsertEvent postInsertEvent) {
        Object entity = postInsertEvent.getEntity()
        println("got onPostInsert for " + entity)
        if (Person.isInstance(entity)) {
            addPerson(entity)
        }
        if (Institution.isInstance(entity)) {
            addInstitution(entity)
        }
        if (Interest.isInstance(entity)) {
            addInterest(entity)
        }
    }

    def addPerson = { person ->
            personTree.add(person.id, new NamedEntity(person.id, person.fullName))
    }
    def addInstitution = { institution ->
            institutionTree.add(institution.id, new NamedEntity(institution.id, institution.name))
    }
    def addInterest = { interest ->
            interestTree.add(interest.id, new NamedEntity(interest.id, interest.text))
    }

    Collection<NamedEntity> getPersonAutocomplete(String query, int maxResults) {
        // Returns the top three cities that start with "ch" ordered by score.
        List<NamedEntity> people = new ArrayList<NamedEntity>()
        SortedSet<AutocompleteEntry<Long, NamedEntity>> results = personTree.autocomplete(query, maxResults)
        for (AutocompleteEntry<Long, NamedEntity> entry: results) {
            people.add((NamedEntity) entry.getValue())
        }
        return people
    }

    Collection<NamedEntity> getInstitutionAutocomplete(String query, int maxResults) {
        // Returns the top three cities that start with "ch" ordered by score.
        List<NamedEntity> institutions = new ArrayList<NamedEntity>()
        SortedSet<AutocompleteEntry<Long, NamedEntity>> results = institutionTree.autocomplete(query, maxResults)
        for (AutocompleteEntry<Long, NamedEntity> entry: results) {
            institutions.add((NamedEntity) entry.getValue())
        }
        return institutions
    }

    Collection<NamedEntity> getInterestAutocomplete(String query, int maxResults) {
        // Returns the top three cities that start with "ch" ordered by score.
        List<NamedEntity> interests = new ArrayList<NamedEntity>()
        SortedSet<AutocompleteEntry<Long, NamedEntity>> results = interestTree.autocomplete(query, maxResults)
        for (AutocompleteEntry<Long, NamedEntity> entry: results) {
            interests.add((NamedEntity) entry.getValue())
        }
        return interests
    }
}



