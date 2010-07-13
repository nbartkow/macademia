package org.macademia

class InstitutionController {
  def institutionService

  def index = { }

  def filter = {
    List<Institution> institutions = []
    if(params.institutions =="all"){
      institutions = institutionService.findAll()
    }
    else if(params.institutions) {
      def institutionIds = institutionService.getFilteredIds(params.institutions)
      for(Long id: institutionIds){
        def institution = institutionService.get(id)
        institutions.add(institution)
      }
    }
    render(view: "/templates/macademia/_collegeFilterDialog", model: [institutions: institutions])
  }
}

