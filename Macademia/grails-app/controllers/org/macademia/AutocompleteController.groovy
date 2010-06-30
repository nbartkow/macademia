package org.macademia

class AutocompleteController {
    def autocompleteService

    def index = {
        def max = 10
        if (params.max) {
            max = params.max as int
        }
        List<AutocompleteEntity> results = null
        if (params.klass) {
            if (params.klass == 'person') {
                results = autocompleteService.getPersonAutocomplete(params.q, max)
            } else if (params.klass == 'interest') {
                results = autocompleteService.getInterestAutocomplete(params.q, max)
            } else if (params.klass == 'institution') {
                results = autocompleteService.getInstitutionAutocomplete(params.q, max)
            } else {
                throw new IllegalArgumentException("unknown klass to autocomplete: " + params.klass)
            }
        } else {
            def resultsByClass = autocompleteService.getOverallAutocomplete(params.q, max)
            results = resultsByClass.get(Person.class, []) + resultsByClass.get(Interest.class, [])
        }

        def responseStr = ''
        def z = { s -> s.replaceAll('\n', '') }
        results.each {
            responseStr += "${it.id}|${z(it.name)}\n"
        }
        render(responseStr)
    }
}
