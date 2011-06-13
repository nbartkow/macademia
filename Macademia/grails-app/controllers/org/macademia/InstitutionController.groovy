package org.macademia

import grails.converters.JSON
import org.json.JSONObject

class InstitutionController {
    def institutionService
    def institutionGroupService
    def jsonService

    def index = { }

    def filter = {
        def institutions = Institution.list()
        def igList = InstitutionGroup.list()
        def igMap = jsonService.makeJsonForIgMap()
        render(view: "/templates/macademia/_collegeFilterDialog", model: [institutions: institutions, igMap: igMap as JSON, igList: igList])
    }



    def idsToNames = {
        ArrayList institutionList = new ArrayList()
        if (params.ids) {
            def institutionIds
            if (params.ids.equals('all')) {
                institutionList = 'all'
            } else {
                institutionIds = institutionService.getFilteredIds(params.ids)
                for (Long id: institutionIds) {
                    def institution = institutionService.get(id)
                    institutionList.add(institution.name)
                }
            }
        }

        render(institutionList as JSON)
    }

    def primaryGroup = {
        if (!request.authenticated) {
            render(MacademiaConstants.DEFAULT_GROUP)
            return
        } else if (params.group) {
            InstitutionGroup g = institutionGroupService.findByAbbrev(params.group)
            if (g.institutions.contains(request.authenticated.institution)) {
                render (params.group)
                return
            }
        }
        InstitutionGroup smallest = null
        InstitutionGroup all = institutionGroupService.getAllGroup()
        for (InstitutionGroup ig : institutionGroupService.findAllByInstitution(request.authenticated.institution)) {
            if (smallest == null || (ig != all && ig.institutions.size() < smallest.institutions.size())) {
                smallest = ig
            }
        }
        if (smallest != null) {
            println("smallest is ${smallest.name} with abbrev ${smallest.abbrev}")
            render(smallest.abbrev)
        } else {
            render(all.abbrev)
        }
    }
}

