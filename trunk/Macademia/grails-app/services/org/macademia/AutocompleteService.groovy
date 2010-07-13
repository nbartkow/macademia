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
class AutocompleteService implements PostInsertEventListener {

    //this is the maximum number of autocomplete results
    static int MAX_NUMBER_RES = 10
    AutocompleteTree<String, AutocompleteEntity> institutionTree = new AutocompleteTree<String, AutocompleteEntity>()
    AutocompleteTree<String, AutocompleteEntity> interestTree = new AutocompleteTree<String, AutocompleteEntity>()
    AutocompleteTree<String, AutocompleteEntity> overallTree = new AutocompleteTree<String, AutocompleteEntity>()
    SessionFactory sessionFactory

    def init() {
        log.info("processing autocomplete people...")
        Person.findAll().each { addPerson(it) }
        log.info("processing autocomplete institution...")
        Institution.findAll().each { addInstitution(it) }
        log.info("processing autocomplete interest...")
        Interest.findAll().each { addInterest(it) }
        log.info("processing autocomplete collaborator requests...")
        CollaboratorRequest.findAll().each { addRequest(it) }
        
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
        if (CollaboratorRequest.isInstance(entity)){
            addRequest(entity)
        }
    }

    def addPerson = { person ->
        def entity = new AutocompleteEntity(person.id, person.fullName, Person.class)
        overallTree.add("p" + person.id, entity)
    }
    def addInstitution = { institution ->
        def entity = new AutocompleteEntity(institution.id, institution.name, Institution.class)
        institutionTree.add(institution.id, entity)
    }
    def addInterest = { interest ->
        def entity = new AutocompleteEntity(interest.id, interest.text, Interest.class)
        overallTree.add("i" + interest.id, entity)
        interestTree.add(interest.id, entity)
    }

    def addRequest = { collaboratorRequest ->
        def entity = new AutocompleteEntity(collaboratorRequest.id, collaboratorRequest.title, CollaboratorRequest.class)
        overallTree.add("r"+ collaboratorRequest.id, entity)
    }

    Collection<AutocompleteEntity> getInstitutionAutocomplete(String query, int maxResults) {
        // Returns the top three cities that start with "ch" ordered by score.
        List<AutocompleteEntity> institutions = new ArrayList<AutocompleteEntity>()
        SortedSet<AutocompleteEntry<Long, AutocompleteEntity>> results = institutionTree.autocomplete(query, maxResults)
        for (AutocompleteEntry<Long, AutocompleteEntity> entry: results) {
            institutions.add((AutocompleteEntity) entry.getValue())
        }
        return institutions
    }

    Collection<AutocompleteEntity> getInterestAutocomplete(String query, int maxResults) {
        // Returns the top three cities that start with "ch" ordered by score.
        List<AutocompleteEntity> interests = new ArrayList<AutocompleteEntity>()
        SortedSet<AutocompleteEntry<Long, AutocompleteEntity>> results = interestTree.autocomplete(query, maxResults)
        for (AutocompleteEntry<Long, AutocompleteEntity> entry: results) {
            interests.add((AutocompleteEntity) entry.getValue())
        }
        return interests
    }
    Map<Class, Collection<AutocompleteEntity>> getOverallAutocomplete(String query, int maxResults) {
        Map<Class, Collection<AutocompleteEntity>> result = [:]
        SortedSet<AutocompleteEntry<Long, AutocompleteEntity>> results = overallTree.autocomplete(query, maxResults)
        for (AutocompleteEntry<Long, AutocompleteEntity> entry: results) {
            AutocompleteEntity entity = entry.getValue()
            if (!result.containsKey(entity.getKlass())) {
                result[entity.getKlass()] = []
            }
            result[entity.getKlass()].add(entity)
        }
        return result
    }
}



