package org.macademia

import grails.converters.JSON
import org.json.JSONObject

class InstitutionController {
    def institutionService
    def institutionGroupService
    def springcacheService

    def jsonService

    def index = { }

    def filter = {
        def model = springcacheService.doWithCache(
                'institutionCache',
                'filterModel',
                {
                    def institutions = Institution.list().sort({it.name.toLowerCase()})
                    def igList = InstitutionGroup.list().sort({it.name.toLowerCase()})
                    def igMap = jsonService.makeJsonForIgMap()
                    return [institutions: institutions, igMap: igMap as JSON, igList: igList]
                }
            )
        render(view: "/templates/macademia/_collegeFilterDialog", model: model)
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
            if (request.authenticated.memberOfAny(g.institutions.id)) {
                render (params.group)
                return
            }
        }
        InstitutionGroup all = institutionGroupService.getAllGroup()
        log.error(request.authenticated.memberships)
        // should ACM be in caps? should we lowercaps everything? this may cause problems in future
        if (request.authenticated.memberOfAny(institutionGroupService.findByAbbrev("ACM"))) {
            render(institutionGroupService.findByAbbrev("ACM").abbrev)
        } else if (request.authenticated.memberships.size() > 0) {
            render (request.authenticated.memberships.toList()[0].institution.institutionGroups.toList()[0].abbrev)
        } else {
            render(all.abbrev)
        }
    }

    def institutionsFromGroup = {
        def institutions = institutionGroupService.retrieveInstitutions(institutionGroupService.findByAbbrev(params.group)).asList()
        if (institutions.size() == 1){
            if (institutions[0].type == Institution.TYPE_SCHOOL) {
                render ([type:'school', institution:institutions[0].name, url:institutions[0].webUrl] as JSON)
            } else if (institutions[0].type == Institution.TYPE_GROUP){
                render ([type:'group', institution:''+institutions[0].name, url:''+institutions[0].webUrl] as JSON)
            }
        } else {
            render([type:'notSingle'] as JSON)
        }
    }
}

