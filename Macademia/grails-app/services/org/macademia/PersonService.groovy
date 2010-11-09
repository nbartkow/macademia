package org.macademia

/**
 * Authors: Nathaniel Miller and Alex Schneeman
 */
class PersonService {

    boolean transactional = true
    def MAX_DEPTH = 5
    def interestService
    def userService
    def databaseService
    def autocompleteService
    def collaboratorRequestService


    def get(long id) {
        return Person.get(id)
    }

    def findByEmail(String email) {
        return Person.findByEmail(email)
    }

    def findByInterest(Interest i) {
        return i.people
    }

    public void create(Person person, String passwd, String ipAddr) {
        person.passwdHash = Person.calculatePasswdHash(passwd)
        person.token = Person.randomString(20)
        save(person, ipAddr)
    }

    public void save(Person person){
        this.save(person, null)
    }
    public void save(Person person, String ipAddr){
        //Maps wrong interest to right interest
        Map<Interest,Interest> remove = new HashMap<Interest,Interest>()
        //log.info("$person.interests[0]")

        for(Interest interest in person.interests){
            // brand new interest
            if (interestService.findByText(interest.text) == null) {
                interestService.save(interest, ipAddr)
            } // new interest, but .text of interest exists in database
              else if (interest.id == null) {
                remove.put(interest,interestService.findByText(interest.text))
            } // existing interest
              else if (interest.lastAnalyzed == null) {
                interestService.save(interest, ipAddr)
            }
        }
        for (Interest interest in remove.keySet()) {
            person.removeFromInterests(interest)
            person.addToInterests(remove.get(interest))
        }
        Utils.safeSave(person, true)
        databaseService.addUser(person)
        autocompleteService.addPerson(person)
    }

    public Person findByToken(String token) {
        return Person.findByToken(token)
    }

    public void delete(Person person){
        autocompleteService.removePerson(person.id)
        List deleteInterestsAndRequests = databaseService.removeUser(person.id)
        for (interest in deleteInterestsAndRequests[0]){
          interestService.delete(interest)
        }
        for (request in deleteInterestsAndRequests[1]){
          collaboratorRequestService.delete(CollaboratorRequest.get(request))
        }
        person.delete()
    }

}
