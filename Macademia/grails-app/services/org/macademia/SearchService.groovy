package org.macademia

class SearchService {
  boolean transactional = true
  int maxFilteredResults = 10

  Collection<Person> searchPeople(String query, int offset, int max) {
    return Person.search(query, [reload: true, offset:offset, max:max]).results
  }

  Collection<Interest> searchInterests(String query, int offset, int max) {
    return Interest.search(query, [reload: true, offset:offset, max:max]).results

  }

  Collection<CollaboratorRequest> searchCollaboratorRequests(String query, int offset, int max) {
    return CollaboratorRequest.search(query, [reload: true, offset:offset, max:max]).results
  }

  Collection<Institution> searchInstitutions(String query) {
    return Institution.search(query, [reload: true], ).results
  }

  int numPersonResults(String query) {
    return Person.search(query, [reload: true]).total
  }

  int numRequestResults(String query) {
    return CollaboratorRequest.search(query, [reload: true]).total
  }

  int numInterestResults(String query) {
    return Interest.search(query, [reload: true]).total
  }

  Collection<Person> filterPeopleByInstitution(Collection<Person> pResults, Set<Long> institutionFilter) {
    Collection<Person> filteredPeople = new ArrayList<Person>()
    for(Person p: pResults){
      if(institutionFilter.contains(p.institution.id)){
        filteredPeople.add(p)
      }
      if(filteredPeople.size() >= maxFilteredResults){
        break;
      }
    }
    return filteredPeople
  }

  Collection<Interest> filterInterestsByInstitution(Collection<Interest> iResults, Set<Long> institutionFilter) {
    Collection<Interest> filteredInterests = new ArrayList<Interest>()
    for(Interest i: iResults){
      for(Person p: i.people){
        if(institutionFilter.contains(p.institution.id)){
          filteredInterests.add(i)
          break
        }
      }
      if(filteredInterests.size() >= maxFilteredResults){
        break;
      }
    }
    return filteredInterests
  }

  Collection<CollaboratorRequest> filterRequestsByInstitution(Collection<CollaboratorRequest> rResults, Set<Long> institutionFilter) {
    Collection<CollaboratorRequest> filteredRequests = new ArrayList<CollaboratorRequest>()
    for(CollaboratorRequest r: rResults){
      if(institutionFilter.contains(r.creator.institution.id)){
        filteredRequests.add(r)
      }
      if(filteredRequests.size() >= maxFilteredResults){
        break;
      }
    }
    return filteredRequests
  }
}
