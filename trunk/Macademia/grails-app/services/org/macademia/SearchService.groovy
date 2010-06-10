package org.macademia

class SearchService {
  boolean transactional = true

  Collection<Person> searchPeople(String query) {
    return Person.search(query, [reload: true]).results
  }

  Collection<Interest> searchInterests(String query) {
    return Interest.search(query, [reload: true]).results
  }

  Collection<Institution> searchInstitutions(String query) {
    return Institution.search(query, [reload: true]).results
  }

  Collection<CollaboratorRequest> searchCollaboratorRequests(String query) {
      return CollaboratorRequest.search(query, [reload: true]).results
  }
}
