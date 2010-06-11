package org.macademia

class InterestRelation implements Comparable {
    Interest first
    Interest second
    double similarity

    static mapping = {
        first column: "first", fetch: 'join', cache: true, index : 'firstIdx'
        second column: "second", fetch: 'join', cache: true, index : 'secondIdx'
    }

    static constraints = {
    }

    public int compareTo(Object other) {
        if (!(other instanceof InterestRelation)) {
            return -1;
        } else if (first.equals(other.first) && second.equals(other.second)) {
            return 0
        } else if (similarity > other.similarity) {
            return -1
        } else if (similarity < other.similarity) {
            return +1
        } else if (!first.equals(other.first)) {
            return first.compareTo(other.first)
        } else {
            return second.compareTo(other.second)
        }
    }

    public boolean equals(Object other) {
        return (compareTo(other) == 0)
    }

    public int hashCode() {
        return first.hashCode() + 2 * second.hashCode()
    }

    public String toString() {
        return "<$first, $second, $similarity>"
    }

    public String description() {
        return "$first.text and $second.text"
    }
}
