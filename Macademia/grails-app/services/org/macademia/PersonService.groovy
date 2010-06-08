package org.macademia

class PersonService {

    boolean transactional = true
    def MAX_DEPTH = 5
    def interestService


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
        for(Interest interest in person.interests){            
            if (Interest.findByText(interest.text) == null) {
                interestService.save(interest)
            } else if (interestService.findByText(interest.text) != null && interest.id == null) {
                remove.put(interest,interestService.findByText(interest.text))

            } /*else {
              interestService.save(interest)
            }  */ //not necessary?
        }
        for (Interest interest in remove.keySet()) {
            person.removeFromInterests(interest)
            person.addToInterests(remove.get(interest))
        }
        person.save()
    }


}
