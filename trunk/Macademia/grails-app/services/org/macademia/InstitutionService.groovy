package org.macademia

/**
 * Authors: Nathaniel Miller and Alex Schneeman
 */
class InstitutionService {

    static transactional = true

    def save(Institution institution) {
        if (institution.memberships == null) {
            institution.memberships = [] as Set
        }
        Utils.safeSave(institution, true)
    }

    def get(long id) {
        return Institution.get(id)
    }

    public Institution findByEmailDomain(String domain) {
        return Institution.findByEmailDomain(domain)
    }

    public List<Institution> findAll() {
        return Institution.findAll()
    }

    public Set<Long> getFilteredIds(String institutions) {
        List<String> collegesAsStrings = institutions.tokenize("c_")
        Set<Long> collegeIds = new HashSet<Long>()
        for(String college: collegesAsStrings){
            Long id = college.toLong()
            collegeIds.add(id)
        }
        return collegeIds
    }
}
