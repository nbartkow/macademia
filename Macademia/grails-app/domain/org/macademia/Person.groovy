package org.macademia

class Person extends grails.plugins.nimble.core.ProfileBase {
    // fullName, owner, and email are inherited.
    
    String department
    Institution institution

    static hasMany = [interests: Interest]
    static searchable = [only: ['fullName', 'email', 'department']]
    
    static mapping = {
        interests fetch: "join", cache: true
    }

    public String toString() {
        return "$fullName ($department)"
    }

    public int compareTo(Object p2) {
        if (Person.class.isInstance(p2)) {
            return email.compareTo(p2.email)
        } else {
            return -1;
        }
    }

    public boolean equals(Object p2) {
        return (compareTo(p2) == 0)
    }

    public int hashCode() {
        return email.hashCode()
    }

}
