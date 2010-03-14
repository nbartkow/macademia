package org.macademia

class Person {
    String name
    String email
    String department

    static hasMany = [interests: Interest]
    static searchable = true
    static constraints = {
        email(unique: true)
    }
    static mapping = {
        interests fetch: "join", cache: true
    }

    public String toString() {
        return "$name ($department)"
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
