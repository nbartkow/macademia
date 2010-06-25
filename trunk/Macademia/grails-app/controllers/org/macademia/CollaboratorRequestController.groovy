package org.macademia

class CollaboratorRequestController {
    def collaboratorRequestService

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index = {
        redirect(action: "manage", params: params)
    }

    def manage = {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        def User user = User.findByid(authenticatedUser.id)
        def CollaboratorRequestList = CollaboratorRequest.list(params)
        //uncomment this, and comment the line above in order to lock collaboratorRequests to a single user
//        def CollaboratorRequestList = CollaboratorRequest.findAllByCreator(user.profile)
        [collaboratorRequestList: CollaboratorRequestList, collaboratorRequestInstanceTotal: CollaboratorRequest.count(), user:user ]
    }

    def create = {
        def collaboratorRequest = new CollaboratorRequest()
        collaboratorRequest.properties = params
//        def user = authenticatedUser
        return [collaboratorRequest: collaboratorRequest]
    }

    def save = {
        def collaboratorRequest = new CollaboratorRequest(params)
        collaboratorRequest.creator = authenticatedUser.profile
        collaboratorRequestService.save(collaboratorRequest)
        redirect(action: "manage")
    }

    def show = {
        def collaboratorRequestInstance = CollaboratorRequest.get(params.id)
        if (!collaboratorRequestInstance) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'collaboratorRequest.label', default: 'CollaboratorRequest'), params.id])}"
            redirect(action: "manage")
        }
        else {
            [collaboratorRequestInstance: collaboratorRequestInstance]
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
}
