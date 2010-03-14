package org.macademia

class SearchController {
    def searchService

    def index = { }

    def search = {
        def query
        if (params.searchBox) {
            query = params.searchBox
        }

        // Prefix match!
        def cleanedQuery = query.toLowerCase()
        if (cleanedQuery[-1] != '*') {
            cleanedQuery += "*"
        }
        
        def pResults
        def iResults
        pResults = searchService.searchPeople(cleanedQuery)
        iResults = searchService.searchInterests(cleanedQuery)

        render(template: "/search/searchResults", model: [people: pResults, interests: iResults, query : query])

    }
}
