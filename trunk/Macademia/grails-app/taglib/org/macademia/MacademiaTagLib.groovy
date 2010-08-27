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
        attrs.uri = Utils.makeUrl('person', attrs.person.id, true)
        String bodyStr = body()
        if (bodyStr) {
            out << g.link(attrs, bodyStr)
        } else {
            out << g.link(attrs, attrs.person.fullName.encodeAsHTML())
        }
    }

    def requestLink = {
        attrs, body ->
        attrs.uri = Utils.makeUrl('request', attrs.request.id)
        String bodyStr = body()
        if (bodyStr) {
            out << g.link(attrs, bodyStr)
        } else {
            out << g.link(attrs, attrs.request.title.encodeAsHTML())
        }
    }

    def interestLink = {
        attrs, body ->
        attrs.uri = Utils.makeUrl('interest', attrs.interest.id)
        String bodyStr = body()
        if (bodyStr) {
            out << g.link(attrs, bodyStr)
        } else {
            out << g.link(attrs, attrs.interest.text.encodeAsHTML())
        }
    }

}
