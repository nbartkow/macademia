package org.macademia

/**
 * Authors: Nathaniel Miller and Alex Schneeman
 */

class InstitutionService {

    static transactional = true

    public Institution findByEmailDomain(String domain) {
        return Institution.findByEmailDomain(domain)
    }
}
