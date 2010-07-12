package org.macademia

class SearchController {
  def searchService

  def index = { }

  def search = {
    def query
    if (params.searchBox) {
      query = params.searchBox
    }
    def institutions = params.institutions
    def index = params.pageNumber.toInteger()
    def type = params.type

    // Prefix match!
    def cleanedQuery = query.toLowerCase()
    if (cleanedQuery[-1] != '*') {
      cleanedQuery += "*"
    }
    if (type == 'all'){
      def pResults
      def iResults
      def rResults
      pResults = searchService.searchPeople(cleanedQuery)
      iResults = searchService.searchInterests(cleanedQuery)
      rResults = searchService.searchCollaboratorRequests(cleanedQuery)
      render(template: "/search/searchResults", model: [people: pResults, interests: iResults, requests: rResults, query : query])
    }else if(type == 'interest'){
       def iResults
       def total = 10
       def offSet = (index + 1) * 20 + 1
       def max = 20
       iResults = searchService.searchInterests(cleanedQuery)
       render(template: "/search/deepSearchResults", model: [results: iResults, query : query, type : type, index : index, total : total])
    }else if(type == 'person'){
       def pResults
       def total = 10
       def offSet = (index + 1) * 10 + 1
       def max = 10
       pResults = searchService.searchPeople(cleanedQuery)
       render(template: "/search/deepSearchResults", model: [results: pResults, query : query, type : type, index : index, total : total])
    }else if(type == 'request'){
       def rResults
       def total = 10
       def offSet = (index + 1) * 5 + 1
       def max = 5
       rResults = searchService.searchCollaboratorRequests(cleanedQuery)
       render(template: "/search/deepSearchResults", model: [results: rResults, query : query, type : type, index : index, total : total])
    }
  }
}
