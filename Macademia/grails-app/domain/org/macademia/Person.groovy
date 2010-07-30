package org.macademia

class Person extends grails.plugins.nimble.core.ProfileBase {
    // fullName, owner, and email are inherited.
    
    String title
    String department
    Institution institution    
    String imageSubpath
    String links  // represented as a series of <li><a href="http://foo.com">foo</a></li> items

    static hasMany = [interests: Interest]
    static searchable = [only: ['fullName', 'email', 'department']]
    static constraints = {
        imageSubpath(nullable : true, blank:false)
        fullName(nullable: false, blank:false)
        email(nullable:false, blank:false, email: true, unique: true)
        links(nullable:true)
        department(nullable:true)
        title(nullable:true)
    }
    
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
