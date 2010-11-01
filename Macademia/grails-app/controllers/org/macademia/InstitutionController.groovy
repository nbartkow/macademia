package org.macademia

import grails.converters.JSON

class InstitutionController {
    def institutionService

    def index = { }

    def filter = {

        Collection<Institution> institutions = Institution.findAll()

        // TODO: Remove me after launch
        institutions = institutions.findAll({
            it.emailDomain == 'macalester.edu' || it.emailDomain == 'carleton.edu' || it.emailDomain == 'acm.edu'
        })

        render(view: "/templates/macademia/_collegeFilterDialog", model: [institutions: institutions])
    }


    def idstonames = {
        ArrayList institutionList = new ArrayList()
        if (params.ids) {
            def institutionIds
            if (params.ids.equals('all')) {
                institutionIds = 'all'
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
}

