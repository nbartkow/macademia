package org.macademia

import grails.converters.JSON;

class InterestController {

    def interestService
    def institutionService
    def similarityService
    def personService
    def jsonService

    def index = { }

    def list = {
        [interestList: Interest.list()]
    }

    def json = {
        def maxPeople
        def maxInterests
        if(params.maxPerson){
            maxPeople = params.maxPerson.toInteger()
        }
        else if (params.density) {
            maxPeople = (params.density as int) * 8
            maxInterests = (params.density as int) * 5
        }
        else{
            maxPeople = 25
            maxInterests = 15
        }
        def root = interestService.get((params.id as long))
        Graph graph
        if(params.institutions.equals("all")){
            graph = similarityService.calculateInterestNeighbors(root, maxPeople, maxInterests)
        }
        else{
            Set<Long> institutionFilter = institutionService.getFilteredIds(params.institutions)
            graph = similarityService.calculateInterestNeighbors(root, maxPeople, maxInterests, institutionFilter)
        }
        def data = jsonService.buildInterestCentricGraph(root, graph)
        render(data as JSON)
    }


    def jit = {

      [:]
      //[interest: interestService.get((params.id as long))]

    }

    def tooltip = {
        def interest = interestService.get((params.id as long))
        def simInts = similarityService.getSimilarInterests(interest).collect({Interest.get(it.interestId)})
        def related = new ArrayList<String>()
        for(Interest i: simInts){
            if(i!=null){
                related.add(i.text)
            }
        }
        if(related.isEmpty()){
            related.add("No related Interests")
        }
        [
            interest : interest,
            people : personService.findByInterest(interest),
            related : related,
        ]
    }

    def show = {
        def interest = Interest.get(params.id)
        def peopleWithInterest = interest.people
        def relatedInterests = similarityService.getSimilarInterests(interest).list.collect({Interest.findById(it.interestId)})
        if(relatedInterests.contains(interest)){
            relatedInterests.remove(interest)
        }
        if (!interest) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'collaboratorRequest.label', default: 'CollaboratorRequest'), params.id])}"
            redirect(action: "list")
        }
        else {
            [interest: interest, peopleWithInterest: peopleWithInterest, relatedInterests: relatedInterests]
        }
    }

    def analyze = {
        if (!params.interest) {
            render('unknown')
        } else {
            Interest interest = interestService.findByText(params.interest)
            if (interest == null) {
                interest = new Interest(params.interest)
                interestService.save(interest, Utils.getIpAddress(request))
            }
            render(interest.articleName)
        }
    }

    def rebuild = {
        response.contentType = "text/plain"

        def outs = response.outputStream
        def interests = Interest.findAll()
        outs << "building relations for ${interests.size()} interests\n"
        def i = 0
        interests.each() {
            i++
            outs << "($i of ${interests.size()}): building interest doc for ${it.text}\n"
            interestService.buildDocuments(it, null)
            outs.flush()
        }
        outs.close()
    }

    def reanalyze = {
        response.contentType = "text/plain"

        def outs = response.outputStream
        def interests = Interest.findAll()
        outs << "building relations for ${interests.size()} interests\n"
        def i = 0
        interests.each() {
            i++
            outs << "($i of ${interests.size()}): building relation for ${it.text}\n"
            similarityService.buildInterestRelations(it)
            outs.flush()
        }
        outs.close()
    }

}
