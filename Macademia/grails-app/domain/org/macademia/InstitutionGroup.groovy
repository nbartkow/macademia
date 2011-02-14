package org.macademia

class InstitutionGroup {
    String name
    String abbrev

    static hasMany = [ institutions : Institution ]
    static mapping = {
        institutions batchSize: 100
    }

    static constraints = {
        abbrev(unique: true)
    }
}
