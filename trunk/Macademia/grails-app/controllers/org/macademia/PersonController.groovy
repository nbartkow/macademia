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
    
    def recent = {
        StringBuffer buff = new StringBuffer()
        buff.append('<html>\n')
        buff.append('<h1>Recent account activity:</h1>\n')
        buff.append('<table border="1">\n')
        buff.append('<tr><td>name</td><td>email</td><td>num-interests</td><td>updated</td><td>created</td></tr>\n')
        for (Person p : Person.list(sort : 'lastUpdated', order : 'desc')) {
            buff.append("<tr>")
            buff.append("<td>${p.fullName}</td>")
            buff.append("<td>${p.email}</td>")
            buff.append("<td>${p.interests.size()}</td>")
            buff.append("<td>${p.lastUpdated}</td>")
            buff.append("<td>${p.dateCreated}</td>")
            buff.append("</tr>\n")
        }
        buff.append('</table>\n')
        buff.append('</html>\n')
        render(buff.toString())
    }


    def index = {
        Random r = new Random()
        List<Long> ids = new ArrayList<Long>(Person.findAll().collect({it.id}))
        long id = ids[r.nextInt(ids.size())]
        redirect(uri: "/person/jit/#/?nodeId=p_${id}&navVisibility=true&navFunction=person&institutions=all&personId=${id}")
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
        def max
        if(params.maxPerson){
            max = params.maxPerson as int
        }
        else if (params.density) {
            max = (params.density as int) * 8
        }
        else{
            max = 25
        }
        def root = personService.get((params.id as long))
        Graph graph
        if(params.institutions.equals("all")){
            graph = similarityService.calculatePersonNeighbors(root, max)
          println('max:     '+max)
        }
        else{
            Set<Long> institutionFilter = institutionService.getFilteredIds(params.institutions)
            graph = similarityService.calculatePersonNeighbors(root, max, institutionFilter)

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
