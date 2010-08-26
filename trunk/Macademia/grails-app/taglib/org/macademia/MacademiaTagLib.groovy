package org.macademia

class MacademiaTagLib {
    static namespace = 'm'

    def ifLoggedIn = {
        attrs, body ->
        if (request.person) {
            out << body()
        }
    }

    def ifNotLoggedIn = {
        attrs, body ->
        if (!request.person) {
            out << body()
        }
    }

    def ifAdmin = {
        attrs, body ->
        if (request.person && request.person.role == Person.ADMIN_ROLE) {
            out << body()
        }
    }

    def personLink = {
        attrs, body ->
        def id = attrs.person.id
        attrs.uri = "/person/jit/#/?nodeId=p_${id}&navVisibility=true&navFunction=person&institutions=all&personId=${id}"
        String bodyStr = body()
        if (bodyStr) {
            out << g.link(attrs, bodyStr)
        } else {
            out << g.link(attrs, attrs.person.fullName.encodeAsHTML())
        }
    }

}
