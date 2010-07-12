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
        def root = interestService.get((params.id as long))
        Graph graph
        if(params.institutions.equals("all")){
            graph = similarityService.calculateInterestNeighbors(root, jsonService.DEFAULT_MAX_NEIGHBORS_INTEREST_CENTRIC, jsonService.DEFAULT_MAX_INTERESTS_INTEREST_CENTRIC)
        }
        else{
            Set<Long> institutionFilter = institutionService.getFilteredIds(params.institutions)
            graph = similarityService.calculateInterestNeighbors(root, jsonService.DEFAULT_MAX_NEIGHBORS_INTEREST_CENTRIC, jsonService.DEFAULT_MAX_INTERESTS_INTEREST_CENTRIC, institutionFilter)
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
        def simInts = similarityService.getSimilarInterests(interest).collect({Interest.findById(it.interestId)})
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

}
