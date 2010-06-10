package org.macademia

/**
 * Authors: Nathaniel Miller and Alex Schneeman
 */
class CollaboratorRequest {

    String title
    String description
    Date dateCreated
    Date expiration
    static hasMany = [keywords : Interest]
    static searchable = true
    Person creator

    static mapping = {
        keywords fetch: "join", cache: true
    }
    
    public String toString() {
        return "$title ($creator.name)"
    }

    public int compareTo(Object other) {
        if (CollaboratorRequest.class.isInstance(other)){
            return title.compareTo(other.title)
        } else {
            return -1
        }

    }

    public boolean equals(Object other) {
        return (compareTo(other) == 0)
    }

    public int hashCode() {
        return title.hashCode()
    }

}
