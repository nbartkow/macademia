package org.macademia

class SearchController {
  def searchService
  def institutionService

  def index = { }

  def search = {
    def query
    def offset
    def max
    def pResults
    def iResults
    def rResults
    if (params.searchBox) {
      query = params.searchBox
    }
    def institutionString = params.institutions
    def pageNumber = params.pageNumber.toInteger()
    def type = params.type

    // Prefix match!
    def cleanedQuery = query.toLowerCase()
    if (cleanedQuery[-1] != '*') {
      cleanedQuery += "*"
    }
    if (type == 'all'){
      if(institutionString.equals("all")){
        pResults = searchService.searchPeople(cleanedQuery, 0, 10)
        iResults = searchService.searchInterests(cleanedQuery, 0, 10)
        rResults = searchService.searchCollaboratorRequests(cleanedQuery, 0, 10)
      }
      else{
        pResults = searchService.searchPeople(cleanedQuery, 0, 100)
        iResults = searchService.searchInterests(cleanedQuery, 0, 100)
        rResults = searchService.searchCollaboratorRequests(cleanedQuery, 0, 100)
        def splitInstitutions = ("+" + institutionString).tokenize("//+c_")
        Set<Long> institutions = []
        for(String id: splitInstitutions) {
          def institutionId = id.toLong()
          institutions.add(institutionId)
        }
        pResults = searchService.filterPeopleByInstitution(pResults, institutions)
        iResults = searchService.filterInterestsByInstitution(iResults, institutions)
        rResults = searchService.filterRequestsByInstitution(rResults, institutions)
      }
      render(template: "/search/searchResults", model: [people: pResults, interests: iResults, requests: rResults, query : query])
    }else if(type == 'interest'){
      def total = searchService.numInterestResults(cleanedQuery)/20
      offset = pageNumber * 20
      max = 20
      iResults = searchService.searchInterests(cleanedQuery, offset, max)
      render(template: "/search/deepSearchResults", model: [results: iResults, query : query, type : type, index : pageNumber, total : total])
    }else if(type == 'person'){
      def total = searchService.numPersonResults(cleanedQuery)/10
      offset = pageNumber * 10
      max = 10
      pResults = searchService.searchPeople(cleanedQuery, offset, max)
      render(template: "/search/deepSearchResults", model: [results: pResults, query : query, type : type, index : pageNumber, total : total])
    }else if(type == 'request'){
      def total = searchService.numRequestResults(cleanedQuery)/3
      offset = pageNumber * 3
      max = 3
      rResults = searchService.searchCollaboratorRequests(cleanedQuery, offset, max)
      render(template: "/search/deepSearchResults", model: [results: rResults, query : query, type : type, index : pageNumber, total : total])
    }
  }
}
