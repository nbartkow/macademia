package org.macademia

import grails.converters.JSON

class InstitutionController {
    def institutionService

    def index = { }

    def filter = {

        def c = Person.createCriteria()
        Collection<Institution> institutions = c.list {
            projections {
                distinct('institution')
            }
        }

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

