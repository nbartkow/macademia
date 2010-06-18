package org.macademia

class CollaboratorRequestController {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index = {
        redirect(action: "list", params: params)
    }

    def list = {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [collaboratorRequestInstanceList: CollaboratorRequest.list(params), collaboratorRequestInstanceTotal: CollaboratorRequest.count()]
    }

    def create = {
        def collaboratorRequestInstance = new CollaboratorRequest()
        collaboratorRequestInstance.properties = params
        return [collaboratorRequestInstance: collaboratorRequestInstance]
    }

    def save = {
        def collaboratorRequestInstance = new CollaboratorRequest(params)
        if (collaboratorRequestInstance.save(flush: true)) {
            flash.message = "${message(code: 'default.created.message', args: [message(code: 'collaboratorRequest.label', default: 'CollaboratorRequest'), collaboratorRequestInstance.id])}"
            redirect(action: "show", id: collaboratorRequestInstance.id)
        }
        else {
            render(view: "create", model: [collaboratorRequestInstance: collaboratorRequestInstance])
        }
    }

    def show = {
        def collaboratorRequestInstance = CollaboratorRequest.get(params.id)
        if (!collaboratorRequestInstance) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'collaboratorRequest.label', default: 'CollaboratorRequest'), params.id])}"
            redirect(action: "list")
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
