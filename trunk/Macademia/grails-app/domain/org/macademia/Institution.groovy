package org.macademia

class Institution {
  String name
  String emailDomain

  static searchable = true
  static constraints = {
    emailDomain(unique: true)
  }
  public String toString() {
    return "$name"
  }

  public int compareTo(Object p2) {
    if (Institution.class.isInstance(p2)) {
      return emailDomain.compareTo(p2.emailDomain)
    } else {
      return -1;
    }
  }

  public boolean equals(Object p2) {
    return (compareTo(p2) == 0)
  }

  public int hashCode() {
    return emailDomain.hashCode()
  }
}
