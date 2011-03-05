package org.macademia

import edu.macalester.acs.AutocompleteTree
import edu.macalester.acs.AutocompleteEntry
import org.hibernate.event.PostInsertEventListener
import org.hibernate.event.PostInsertEvent
import org.hibernate.SessionFactory
import org.hibernate.event.PostUpdateEvent
import org.hibernate.event.PostUpdateEventListener

/**
 * Created by IntelliJ IDEA.
 * User: equeirosnunes, shilad
 * This is a wrapper for the autocomplete plugin
 */
class AutocompleteService implements PostInsertEventListener, PostUpdateEventListener {

    public static class GroupTree {
        AutocompleteTree<String, AutocompleteEntity> institutionTree = new AutocompleteTree<String, AutocompleteEntity>()
        AutocompleteTree<Long, AutocompleteEntity> interestTree = new AutocompleteTree<Long, AutocompleteEntity>()
        AutocompleteTree<Long, AutocompleteEntity> overallTree = new AutocompleteTree<Long, AutocompleteEntity>()
    }

    //this is the maximum number of autocomplete results
    static int MAX_NUMBER_RES = 10

    Map<String, GroupTree> groupTrees = [:]
    SessionFactory sessionFactory
    InstitutionGroupService institutionGroupService

    def init() {

        for (InstitutionGroup ig : InstitutionGroup.list()) {
            getTree(ig.abbrev).institutionTree.clear()
            getTree(ig.abbrev).interestTree.clear()
            getTree(ig.abbrev).overallTree.clear()
        }

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
            postUpdateEventListeners = addListener(sessionFactory.eventListeners.postUpdateEventListeners)
        }
    }

    GroupTree getTree(String abbrev) {
        GroupTree t = groupTrees.get(abbrev)
        if (t == null) {
            t = new GroupTree()
            groupTrees[abbrev] = t
        }
        return t
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
        if (CollaboratorRequest.isInstance(entity)){
            addRequest(entity)
        }
    }

    void onPostUpdate(PostUpdateEvent postUpdateEvent) {
        Object entity = postUpdateEvent.getEntity()
        println("got onPostUpdate for " + entity)
        if (Person.isInstance(entity)) {
            addPerson(entity)
        }
    }

    def addPerson = { person ->
        Collection<InstitutionGroup> igs = institutionGroupService.findAllByInstitution(person.institution)
        for (InstitutionGroup ig : igs) {
//            println("adding person ${person.fullName} to group ${ig.abbrev}")
            GroupTree gt = getTree(ig.abbrev)
            if (!gt.overallTree.contains("p" + person.id)) {
                def entity1 = new AutocompleteEntity(person.id, person.fullName, Person.class)
                gt.overallTree.add("p" + person.id, entity1)
            }
            for (Interest interest : person.interests) {
                def entity2 = new AutocompleteEntity(interest.id, interest.text, Interest.class)
                if (!gt.overallTree.contains("i" + interest.id)) {
                    gt.overallTree.add("i" + interest.id, entity2)
                }
                if (!gt.interestTree.contains(interest.id)) {
                    gt.interestTree.add(interest.id, entity2)
                }
            }
        }
    }

    def addInterest = { interest ->
        // This is now handled in addPerson
    }

    def addInstitution = { institution ->
        def entity = new AutocompleteEntity(institution.id, institution.name, Institution.class)
        Collection<InstitutionGroup> igs = institutionGroupService.findAllByInstitution(institution)
        for (InstitutionGroup ig : igs) {
            getTree(ig.abbrev).institutionTree.add(institution.id, entity)
        }
    }
    def addRequest = { collaboratorRequest ->
        def entity = new AutocompleteEntity(collaboratorRequest.id, collaboratorRequest.title, CollaboratorRequest.class)
        Collection<InstitutionGroup> igs = institutionGroupService.findAllByInstitution(collaboratorRequest.creator.institution)
        for (InstitutionGroup ig : igs) {
            getTree(ig.abbrev).overallTree.add("r"+ collaboratorRequest.id, entity)
        }
    }

    public def removePerson = { person ->
        Collection<InstitutionGroup> igs = institutionGroupService.findAllByInstitution(person.institution)
        for (InstitutionGroup ig : igs) {
            getTree(ig.abbrev).overallTree.remove("p" + person.id)
        }
    }
    public def removeInterest = { interest ->
        // FIXME: figure out how to manage groups properly.
//        overallTree.remove("i" + id)
//        interestTree.remove(id)
    }

    public def removeRequest = { request ->
        Collection<InstitutionGroup> igs = institutionGroupService.findAllByInstitution(request.creator.institution)
        for (InstitutionGroup ig : igs) {
            getTree(ig.abbrev).overallTree.remove("r"+ request.id)
        }
    }

    Collection<AutocompleteEntity> getInstitutionAutocomplete(String group, String query, int maxResults) {
        // Returns the top three cities that start with "ch" ordered by score.
        List<AutocompleteEntity> institutions = new ArrayList<AutocompleteEntity>()
        SortedSet<AutocompleteEntry<Long, AutocompleteEntity>> results = getTree(group).institutionTree.autocomplete(query, maxResults)
        for (AutocompleteEntry<Long, AutocompleteEntity> entry: results) {
            institutions.add((AutocompleteEntity) entry.getValue())
        }
        return institutions
    }

    Collection<AutocompleteEntity> getInterestAutocomplete(String group, String query, int maxResults) {
        // Returns the top three cities that start with "ch" ordered by score.
        List<AutocompleteEntity> interests = new ArrayList<AutocompleteEntity>()
        SortedSet<AutocompleteEntry<Long, AutocompleteEntity>> results = getTree(group).interestTree.autocomplete(query, maxResults)
        for (AutocompleteEntry<Long, AutocompleteEntity> entry: results) {
            interests.add((AutocompleteEntity) entry.getValue())
        }
        return interests
    }
    Map<Class, Collection<AutocompleteEntity>> getOverallAutocomplete(String group, String query, int maxResults) {
        Map<Class, Collection<AutocompleteEntity>> result = [:]
        SortedSet<AutocompleteEntry<Long, AutocompleteEntity>> results = getTree(group).overallTree.autocomplete(query, maxResults)
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



