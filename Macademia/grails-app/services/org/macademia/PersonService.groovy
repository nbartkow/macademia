package org.macademia

/**
 * Authors: Nathaniel Miller and Alex Schneeman
 */
class PersonService {

    boolean transactional = true
    def MAX_DEPTH = 5
    def interestService
    def userService


    def get(long id) {
        return Person.get(id)
    }

    def findByEmail(String email) {
        return Person.findByEmail(email)
    }

    def findByInterest(Interest i) {
        return i.people
    }

    public void save(Person person){
        //Maps wrong interest to right interest
        Map<Interest,Interest> remove = new HashMap<Interest,Interest>()
        //log.info("$person.interests[0]")

        for(Interest interest in person.interests){
            log.info(interestService.findByText(interest.text))
            if (interestService.findByText(interest.text) == null) {
                interestService.save(interest)
            } else if (interest.id == null && interestService.findByText(interest.text) != null) {
                remove.put(interest,interestService.findByText(interest.text))
            } else if (interest.lastAnalyzed == null && interestService.findByText(interest.text) != null) {
                interestService.save(interest)
            }
        }
        for (Interest interest in remove.keySet()) {
            person.removeFromInterests(interest)
            person.addToInterests(remove.get(interest))
        }
        if (!person.id){
            userService.createUser(person.owner)
        } else {
            userService.updateUser(person.owner)
        }
        Utils.safeSave(person)
    }


}
