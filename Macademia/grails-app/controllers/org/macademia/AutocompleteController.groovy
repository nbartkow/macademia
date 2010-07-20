package org.macademia

import grails.converters.JSON

class AutocompleteController {
    def autocompleteService

    def index = {
        def max = 10
        if (params.max) {
            max = params.max as int
        }
        List<AutocompleteEntity> results = null
        if (params.klass) {
            if (params.klass == 'interest') {
                results = autocompleteService.getInterestAutocomplete(params.term, max)
            } else if (params.klass == 'institution') {
                results = autocompleteService.getInstitutionAutocomplete(params.term, max)
            } else {
                throw new IllegalArgumentException("unknown klass to autocomplete: " + params.klass)
            }
        } else {
            def resultsByClass = autocompleteService.getOverallAutocomplete(params.term, max)
           
            results = resultsByClass.get(Person.class, []) + resultsByClass.get(Interest.class, []) + resultsByClass.get(CollaboratorRequest.class, [])
        }

        def responseStr = ''
        def z = { s -> s.replaceAll('\n', '') }
        def jsonResults = results.collect { ['' + it.id, z(it.name), '' + it.klass.getSimpleName().toLowerCase()]}
        render(jsonResults as JSON)
    }
}
