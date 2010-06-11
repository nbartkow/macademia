package org.macademia

/**
 * Authors: Nathaniel Miller and Alex Schneeman
 */
class CollaboratorRequestService {

    def interestService

    static transactional = true
    
    def findByInterest(Interest i) {
        return i.requests
    }

    public void save(CollaboratorRequest cr) {
        //Maps wrong interest to right interest
        Map<Interest,Interest> remove = new HashMap<Interest,Interest>()
        for(Interest interest in cr.keywords){
            def res = interestService.findByText(interest.text)
            if (res == null) {
                interestService.save(interest)
            } else if (res != null && interest.id == null) {
                remove.put(interest,res)

            } 
        }
        for (Interest interest in remove.keySet()) {
            cr.removeFromKeywords(interest)
            cr.addToKeywords(remove.get(interest))
        }
        Utils.safeSave(cr)
    }
}