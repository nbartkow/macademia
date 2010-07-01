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
    static searchable = [only: ['title', 'description']]
    Person creator
    static mapping = {
        description type:'text'
    }
    
    public String toString() {
        return "$title ($creator?.fullName)"
    }

    public int compareTo(Object other) {
        def r = -1
        if (CollaboratorRequest.class.isInstance(other)){
            r = title.compareTo(other.title)
            if (r == 0) {
                r = dateCreated.compareTo(other.dateCreated)
            }
        }
        return r

    }

    public boolean equals(Object other) {
        return (compareTo(other) == 0)
    }

    public int hashCode() {
        return title.hashCode()
    }

}
