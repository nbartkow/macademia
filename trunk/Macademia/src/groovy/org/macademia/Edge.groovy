package org.macademia


/**
 * The Edge class represents an edge on a graph. There are three types of edges:
 * Person to Interest edges, Interest to Related Interest, and Person to Interest
 * through the Related Interest or intermediary. If any of the fields are not
 * involved in the edge they are null
 *
 * Authors: Nathaniel Miller and Alex Schneeman
 */
class Edge {
  Person person
  Interest interest
  Interest relatedInterest

  public boolean equals(Object other){
    if(Edge.class.isInstance(other)){
      return (person==other.person && interest==other.interest && relatedInterest==other.relatedInterest) || ( person==other.person && relatedInterest==other.interest && interest==other.relatedInterest )
    }
    return false
  }

  public int hashCode(){
    if(person !=null){
      if(relatedInterest != null){
        return person.hashCode()*interest.hashCode()*relatedInterest.hashCode()
      }
      return person.hashCode()*interest.hashCode()
    }
    return interest.hashCode()*relatedInterest.hashCode()
  }
}
