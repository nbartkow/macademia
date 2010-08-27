package org.macademia

import grails.converters.*
import org.codehaus.groovy.grails.web.json.*

class  PersonController{

    def personService
    def similarityService
    def jsonService
    def collaboratorRequestService
    def userService
    def institutionService
    
    def index = {
        Random r = new Random()
        List<Long> ids = new ArrayList<Long>(Person.findAll().collect({it.id}))
        long id = ids[r.nextInt(ids.size())]
        redirect(uri: "/person/jit/#/?nodeId=p_${id}&navVisibility=true&navFunction=search&institutions=all")
    }

    def tooltip = {
        def target = personService.get((params.id as long))
        def link = null
        if (params.root) {
            if(params.root.contains("p")){
                link = personService.get((params.root.split("_")[1]) as long)
            } else if (params.root.contains("r")){
                link = collaboratorRequestService.get((params.root.split("_")[1]) as long)
            }

        }

        def exact = [:]
        def close = [:]
        def linkName = ''

        // Are we mousing over a user who has a link to the root?
        if (link != null && target != link) {
            def allInterests = []
            if(params.root.contains("p")){
                allInterests = link.interests
                linkName = link.fullName
            } else if (params.root.contains("r")){
                allInterests = link.keywords
                linkName = link.title
            }
            for(Interest i: allInterests) {
                if(target.interests.contains(i)){
                    exact[i] = i
                }
                for(SimilarInterest sim: similarityService.getSimilarInterests(i).list){
                    //println("first: $ir.first second: $ir.second")
                    Interest second = Interest.findById(sim.interestId)

                    if(target.interests.contains(second)){
                        if (!close[second]) {
                            close[second] = []
                        }
                        close[second].add(i)
                    }
                }
            }
            for (Interest ci: close.keySet()) {
                close[ci] = close[ci].collect({it.text}).join(", ")
            }
        }

        [target: target, link: link, close: close, exact: exact, linkName: linkName]
    }

    def asynchJit = {
    }

    def jit = {
        [ authenticatedUser : request.authenticated ]
    }

    def json = {
        def root = personService.get((params.id as long))
        Graph graph
        if(params.institutions.equals("all")){
            graph = similarityService.calculatePersonNeighbors(root, jsonService.DEFAULT_MAX_NEIGHBORS_PERSON_CENTRIC)
        }
        else{
            Set<Long> institutionFilter = institutionService.getFilteredIds(params.institutions)
            graph = similarityService.calculatePersonNeighbors(root, jsonService.DEFAULT_MAX_NEIGHBORS_PERSON_CENTRIC, institutionFilter)
        }
        def data = jsonService.buildUserCentricGraph(root, graph)
        render(data as JSON)
    }

    def show = {
        def person = Person.get(params.id)
        if (!person) {
            render("no person with id ${params.id}")
            return
        }
        def auth = request.authenticated && request.authenticated.canEdit(person)
        def interests = person.interests
        def collaboratorRequests = collaboratorRequestService.findAllByCreator(person)
        render(view : 'show', model : [
                person: person,
                interests: interests,
                collaboratorRequests: collaboratorRequests,
                authenticatedUser:request.authenticated,
                auth: auth
        ])
    }

}