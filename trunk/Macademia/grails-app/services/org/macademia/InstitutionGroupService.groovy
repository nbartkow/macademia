package org.macademia

class InstitutionGroupService {

    static transactional = true

    Collection<InstitutionGroup> findAll() {
        return InstitutionGroup.list()
    }

    Collection<InstitutionGroup> findAllByInstitution(Institution i) {
        return i.institutionGroups
    }

    InstitutionGroup findByAbbrev(String name) {
        return InstitutionGroup.findByAbbrev(name)
    }

    InstitutionGroup getAllGroup() {
        return findByAbbrev("all")
    }

    InstitutionGroup getDefaultGroup() {
        return findByAbbrev(MacademiaConstants.DEFAULT_GROUP)
    }

    Set<Long> getInstitutionIdsFromParams(params) {
        if (params.institutions == null || params.institutions == 'null') {
            params.institutions = 'all'
        }
        if (params.institutions == 'all') {
            InstitutionGroup ig = findByAbbrev(params.group)
            return new HashSet<Long>(ig.institutions.collect{it.id})
        } else {
            def splitInstitutions = ("+" + params.institutions).tokenize("//+c_")
            Set<Long> institutions = new HashSet<Long>()
            for(String id: splitInstitutions) {
                def institutionId = id.toLong()
                institutions.add(institutionId)
            }
            return institutions
        }
    }
}
