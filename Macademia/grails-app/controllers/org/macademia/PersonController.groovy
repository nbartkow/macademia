package org.macademia

import grails.converters.*
import org.codehaus.groovy.grails.web.json.*

class PersonController {

    def personService
    def similarityService
    def jsonService

    def index = {
        Random r = new Random()
        List<Long> ids = new ArrayList<Long>(Person.findAll().collect({it.id}))
        long id = ids[r.nextInt(ids.size())]
        redirect(uri: "/person/jit/#?nodeId=p_${id}&navVisibility=true")
    }


    def tooltip = {
        def target = personService.get((params.id as long))
        def link = null
        if (params.root) {
            link = personService.get((params.root.split("_")[1]) as long)
        }

        def exact = [:]
        def close = [:]

        // Are we mousing over a user who has a link to the root?
        if (link != null && target != link) {
            for(Interest i: link.interests) {
                for(InterestRelation ir: similarityService.getSimilarInterests(i)){
                    //println("first: $ir.first second: $ir.second")
                    if(target.interests.contains(ir.second)){
                        if (ir.first == ir.second) {
                            exact[ir.first] = ir.first
                        } else {
                            if (!close[ir.first]) {
                                close[ir.first] = []
                            }
                            close[ir.first].add(ir.second)
                        }
                    }
                }
            }
            for (Interest ci: close.keySet()) {
                close[ci] = close[ci].collect({it.text}).join(", ")
            }
        }

        [target: target, link: link, close: close, exact: exact]
    }

    def asynchJit = {

    }

 def jit = {
        [:]
        //[person: personService.get((params.id as long))]

    }


    def json = {
        def person = personService.get((params.id as long))
        def data = jsonService.buildUserCentricGraph(person)
        render(data as JSON)
    }

    def show = {
        def person = Person.get(params.id)
        if (!person) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'collaboratorRequest.label', default: 'CollaboratorRequest'), params.id])}"
            redirect(action: "list")
        }
        else {
            [person: person]
        }
    }

}