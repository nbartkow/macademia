package org.macademia

class PersonService {

    boolean transactional = true
    def MAX_DEPTH = 5


    def get(long id) {
        return Person.get(id)
    }

    def findByEmail(String email) {
        return Person.findByEmail(email)
    }

    def findByInterest(Interest i) {
        return i.people
    }


}
