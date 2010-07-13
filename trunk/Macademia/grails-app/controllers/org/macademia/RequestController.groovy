package org.macademia

import grails.converters.JSON

class RequestController {
    def collaboratorRequestService
    def jsonService
    def similarityService
    def interestService
    def personService
    def userService
    def institutionService
    def autocompleteService

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index = {
        redirect(action: "manage", params: params)
    }

    def list = {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        def CollaboratorRequestList = CollaboratorRequest.list(params)
        [collaboratorRequestList: CollaboratorRequestList, collaboratorRequestInstanceTotal: CollaboratorRequest.count()]
    }

    def manage = {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        User user = null;
        if (!params.id){
            user = User.get(authenticatedUser.id)
        }
        else {
            user = User.get(params.id)
            //admin check
            if (user.id != authenticatedUser.id && !userService.isAdmin(authenticatedUser, user)){
                redirect(controller: 'auth', action:'unauthorized')
            }
        }
        def CollaboratorRequestList = CollaboratorRequest.findAllByCreator(user.profile)
        [collaboratorRequestList: CollaboratorRequestList, collaboratorRequestInstanceTotal: CollaboratorRequest.count(), user:user ]
    }

    def create = {
        def collaboratorRequest = new CollaboratorRequest()
//        def fields = grailsApplication.config.nimble.fields.collaboratorRequestEdit.collaboratorRequest
        //collaboratorRequest.properties[fields] = params
//        def user = authenticatedUser
        return [collaboratorRequest: collaboratorRequest]
    }

//            String allInterests = params.interests
//        String[] tokens = allInterests.trim().split(",")
//        for (i in tokens){
//            Interest existingInterest = interestService.findByText(i);
//            if (existingInterest != null){
//                user.profile.addToInterests(existingInterest)
//            } else {
//                Interest newInterest = new Interest(i);
//                user.profile.addToInterests(newInterest)
//            }
//        }


    def save = {
        def collaboratorRequest = new CollaboratorRequest()
        def fields = grailsApplication.config.nimble.fields.collaboratorRequestEdit.collaboratorRequest
        collaboratorRequest.properties[fields] = params
        collaboratorRequest.creator = authenticatedUser.profile
        log.info(params)
        if (params.keywords){
            log.info ("they are here. make parsing here. but jeremy is sleepy and will do it tomorrow >_<")
            String allkeywords = params.keywords
            String[] tokens = allkeywords.trim().split(",")
            for (i in tokens){
                Interest existingInterest = interestService.findByText(i);
                if (existingInterest != null){
                    collaboratorRequest.addToKeywords(existingInterest)
                } else {
                    Interest newInterest = new Interest(i);
                    collaboratorRequest.addToKeywords(newInterest)
                }
            }
        }
        collaboratorRequestService.save(collaboratorRequest)
        autocompleteService.addRequest(collaboratorRequest)
        redirect(uri: "/")
        [collaboratorRequest: collaboratorRequest]
    }

//                def fields = grailsApplication.config.nimble.fields.enduserEdit.user
//            user.properties[fields] = params
//            def String allInterests = ""
//            for (interest in user.profile.interests){
//                allInterests += interest.text+" ,"
//            }
//            log.info(allInterests)
//	        [user: authenticatedUser, allInterests: allInterests]

    def show = {
        def collaboratorRequest = CollaboratorRequest.get(params.id)
        if (!collaboratorRequest) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'collaboratorRequest.label', default: 'CollaboratorRequest'), params.id])}"
            redirect(action: "manage")
        }
        else {
            def String allKeywords = ""
            for (keyword in collaboratorRequest.keywords){
                allKeywords += keyword.text+" ,"
            }
            [collaboratorRequest: collaboratorRequest, allKeywords: allKeywords]
        }
    }

    def edit = {
        def collaboratorRequestInstance = CollaboratorRequest.get(params.id)
        if (!collaboratorRequestInstance) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'collaboratorRequest.label', default: 'CollaboratorRequest'), params.id])}"
            redirect(action: "list")

        }
        else {
            return [collaboratorRequestInstance: collaboratorRequestInstance]
        }
    }

    def update = {
        def collaboratorRequestInstance = CollaboratorRequest.get(params.id)
        if (collaboratorRequestInstance) {
            if (params.version) {
                def version = params.version.toLong()
                if (collaboratorRequestInstance.version > version) {
                    
                    collaboratorRequestInstance.errors.rejectValue("version", "default.optimistic.locking.failure", [message(code: 'collaboratorRequest.label', default: 'CollaboratorRequest')] as Object[], "Another user has updated this CollaboratorRequest while you were editing")
                    render(view: "edit", model: [collaboratorRequestInstance: collaboratorRequestInstance])
                    return
                }
            }
            collaboratorRequestInstance.properties = params
            if (!collaboratorRequestInstance.hasErrors() && collaboratorRequestInstance.save(flush: true)) {
                flash.message = "${message(code: 'default.updated.message', args: [message(code: 'collaboratorRequest.label', default: 'CollaboratorRequest'), collaboratorRequestInstance.id])}"
                redirect(action: "show", id: collaboratorRequestInstance.id)
            }
            else {
                render(view: "edit", model: [collaboratorRequestInstance: collaboratorRequestInstance])
            }
        }
        else {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'collaboratorRequest.label', default: 'CollaboratorRequest'), params.id])}"
            redirect(action: "list")
        }
    }

    def delete = {
        def collaboratorRequestInstance = CollaboratorRequest.get(params.id)
        if (collaboratorRequestInstance) {
            try {
                collaboratorRequestInstance.delete(flush: true)
                flash.message = "${message(code: 'default.deleted.message', args: [message(code: 'collaboratorRequest.label', default: 'CollaboratorRequest'), params.id])}"
                redirect(action: "list")
            }
            catch (org.springframework.dao.DataIntegrityViolationException e) {
                flash.message = "${message(code: 'default.not.deleted.message', args: [message(code: 'collaboratorRequest.label', default: 'CollaboratorRequest'), params.id])}"
                redirect(action: "show", id: params.id)
            }
        }
        else {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'collaboratorRequest.label', default: 'CollaboratorRequest'), params.id])}"
            redirect(action: "list")
        }
    }

    def json = {
        def root = collaboratorRequestService.get((params.id as long))
        Graph graph
        if(params.institutions.equals("all")){
            graph = similarityService.calculateRequestNeighbors(root, jsonService.DEFAULT_MAX_NEIGHBORS_PERSON_CENTRIC)
        }
        else{
            Set<Long> institutionFilter = institutionService.getFilteredIds(params.institutions)
            graph = similarityService.calculateRequestNeighbors(root, jsonService.DEFAULT_MAX_NEIGHBORS_PERSON_CENTRIC, institutionFilter)
        }
        def data = jsonService.buildCollaboratorRequestCentricGraph(root, graph)
        render(data as JSON)
    }

    def jit = {

      [:]
      //[collaboratorRequest: collaboratorRequestService.get((params.id as long))]

    }

    def tooltip = {
        def target = collaboratorRequestService.get((params.id as long))
        def link = null
        log.info("this is $params")
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
                if(target.keywords.contains(i)){
                    exact[i] = i
                }
                for(SimilarInterest sim: similarityService.getSimilarInterests(i).list){
                    //println("first: $ir.first second: $ir.second")
                    Interest second = Interest.findById(sim.interestId)

                    if(target.keywords.contains(second)){
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

}
