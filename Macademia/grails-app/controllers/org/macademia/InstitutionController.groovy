package org.macademia

class InstitutionController {
    def institutionService

    def index = { }

    def filter = {
        log.info("I AM HERE! I AM HERE!@")
      List<Institution> institutions = []
      log.info(params)
        if (params.institutions) {
            if(params.institutions =="all"){
                institutions = institutionService.findAll()
                log.info(institutions)
            }
            else {
                def institutionIds = institutionService.getFilteredIds(params.institutions)
                for(Long id: institutionIds){
                    def institution = institutionService.get(id)
                    institutions.add(institution)
                }
            }
        }
        render(view: "/templates/macademia/_collegeFilterDialog", model: [institutions: institutions])
    }
}

