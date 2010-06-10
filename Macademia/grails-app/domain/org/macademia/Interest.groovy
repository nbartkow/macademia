package org.macademia

/**
 * A research interest denoted by a text field
 */
class Interest {
    String text
    String normalizedText
    Date lastAnalyzed

    static searchable = true
    static constraints = {
        normalizedText(unique: true)
        lastAnalyzed(nullable : true)
    }

    static belongsTo = [Person, CollaboratorRequest]
    static hasMany = [
            people: Person,
            documents: InterestDocument,
            requests: CollaboratorRequest
    ]

    static mapping = {
        documents column : 'interest_id'
    }

    static def normalize = {text ->
        return (text.toLowerCase() =~ /[^a-zA-Z0-9]+/).replaceAll("")
    }

    public Interest() {}

    public Interest(String text) {
        this.text = text
        this.normalizedText = normalize(text)
    }

    public int hashCode() {
        return normalizedText.hashCode()
    }

    public int compareTo(Object other) {
        if (Interest.class.isInstance(other)) {
            return normalizedText.compareTo(other.normalizedText)
        } else {
            return -1
        }
    }

    public boolean equals(Object other) {
        return (compareTo(other) == 0)
    }

    public String toString() {
        return "<$text>"
    }
    public Document findMostRelevantDocument() {
        double bestRelevance = -1.0
        Document best = null
        for (InterestDocument id : documents) {
            if (bestRelevance < id.weight) {
                bestRelevance = id.weight
                best = id.document
            }
        }
        return best
    }


}

