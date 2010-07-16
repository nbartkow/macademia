package org.macademia

import grails.converters.JSON

class InstitutionController {
  def institutionService

  def index = { }

  def filter = {
    List<Institution> institutions = []
    render(view: "/templates/macademia/_collegeFilterDialog", model: [institutions: Institution.findAll()])
  }


  def idstonames= {
    ArrayList institutionList = new ArrayList()
    if (params.ids){
      def institutionIds
      if(params.ids.equals('all')){
        institutionIds = 'all'
      }else{
        institutionIds= institutionService.getFilteredIds(params.ids)
        for(Long id: institutionIds){
          def institution = institutionService.get(id)
          institutionList.add(institution.name)
        }
      }
    }

    render(institutionList as JSON)
  }
}

