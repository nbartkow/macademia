package org.macademia

import grails.converters.JSON;

class InterestController {

    def interestService
    def similarityService
    def personService
    def jsonService

    def index = { }

    def list = {
        [interestList: Interest.list()]
    }

    def json = {
        def interest = interestService.get((params.id as long))
        def data = jsonService.buildInterestCentricGraph(interest)
        render(data as JSON)
    }


    def jit = {

        [interest: interestService.get((params.id as long))]

    }

    def tooltip = {
        def interest = interestService.get((params.id as long))
        [
            interest : interest,
            people : personService.findByInterest(interest),
            related : similarityService.getSimilarInterests(interest).collect({it.second.text}),        
        ]
    }

}
