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
        redirect(action:"jit", params:["id":id], id:id)
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
            Neighbors n = similarityService.getNeighbors(target, link)

            for (InterestRelation ir: n.sharedInterests) {
                if (ir.first == ir.second) {
                    exact[ir.first] = ir.first
                } else {
                    if (!close[ir.first]) {
                        close[ir.first] = []
                    }
                    close[ir.first].add(ir.second)
                }
            }
            for (Interest i: close.keySet()) {
                close[i] = close[i].collect({it.text}).join(", ")
            }
        }

        [target: target, link: link, close: close, exact: exact]
    }

    def asynchJit = {

    }


    def jit = {

        [person: personService.get((params.id as long))]

    }

    def json = {
        println("1 here ${params.id}")
        def person = personService.get((params.id as long))
        println("2 here ${params.id}")
        def data = jsonService.buildUserCentricGraph(person)
        println("3 here ${params.id}")
        def jsonStr = "" + (data as JSON)
        println("4 here ${params.id}")
        render(json)
    }

}