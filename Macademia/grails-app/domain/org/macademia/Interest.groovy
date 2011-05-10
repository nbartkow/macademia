package org.macademia

/**
 * A research interest denoted by a text field
 */
class Interest implements Comparable {
    String text
    String normalizedText
    Date lastAnalyzed
    String articleName
    Long articleId


    static searchable = [only: ['text', 'normalizedText']]
    static constraints = {
        normalizedText(unique: true)
        lastAnalyzed(nullable : true)
        articleName(nullable : true)
        articleId(nullable : true)
        requests(nullable: true)
        people(nullable: true)
    }

    static belongsTo = [Person, CollaboratorRequest]
    static hasMany = [
            people: Person,
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

    public void setText(String text) {
        this.text = text
        this.normalizedText = normalize(text)
    }

}

