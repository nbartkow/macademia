package org.macademia

import grails.converters.JSON;

class SimilarityController {

    def similarityService

    def index = { }

    def show = {

    }

    def list = {

    }
    def json = {
        if (!params.max) params.max = 10
        render Interest.list(params) as JSON
    }
    def dumpSims = {
        BlacklistRelations bl = new BlacklistRelations(MacademiaConstants.PATH_SIM_ADJUSTEMENTS)

        for (Interest i : Interest.getAll()) {
            for (InterestRelation ir : similarityService.getSimilarInterests(i)) {
                bl.append(ir)
            }
        }
        
        render('done')
    }

}
