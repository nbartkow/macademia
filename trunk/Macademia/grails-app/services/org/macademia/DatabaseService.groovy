package org.macademia

import com.mongodb.*
import org.codehaus.groovy.grails.commons.ConfigurationHolder
//import org.bson.types.ObjectId

/**
 * Authors: Nathaniel Miller and Alex Schneeman
 *
 *
 */

class DatabaseService {

    Mongo mongo = new Mongo((String)ConfigurationHolder.config.dataSource.mongoDbUrl)
    MongoWrapper wrapper = new MongoWrapper(mongo, (String)ConfigurationHolder.config.dataSource.monogDbName)

    public void changeDB(String dbName){
        wrapper.changeDB(dbName)
    }

    public void copyDB(String toCopy, String dbName) {
        wrapper.copyDB(toCopy, dbName)
    }

    public void dropDB(String dbName) {
        wrapper.dropDB(dbName)
    }

    public void dropCurrentDB(){
        wrapper.dropCurrentDB()
    }

    public void switchToCopyDB(String toCopy) {
        wrapper.switchToCopyDB(toCopy)
        
    }

    public DBObject findById(String collection, Long id) throws IllegalArgumentException{
        return wrapper.findById(collection, id)
    }

    public DBObject safeFindById(String collection, Long id){
        return wrapper.safeFindById(collection, id)
    }

    public void addUser(Person user) throws RuntimeException {
        long id = user.id
        if(id == null){
            throw new RuntimeException("User needs an ID")
        }
        Set<Interest> interests=user.interests
        String interestIds =""
        for(Interest interest : interests){
            if(interest.id ==null){
                throw new RuntimeException("User has an interest with out an ID")
            }
            interestIds=interestIds+","+interest.id.toString()
            //log.info("Interest ID: "+ interest + "for User ID: " + id)
        }
        long institutionId = user.institution.id
        if(institutionId == null){
            throw new RuntimeException("users institution has no ID")
        }
        wrapper.addUser(id, interestIds, institutionId)
    }

    public long getUserInstitution(long id){
        return wrapper.getUserInstitution(id)
    }

    public List<Long> getUserInterests(long id){
        return wrapper.getUserInterests(id)
    }

    public void addCollaboratorRequest(CollaboratorRequest rfc){
        String interestIds = ""
        for(Interest interest : rfc.keywords){
            interestIds=interestIds+","+interest.id.toString()
        }
        wrapper.addCollaboratorRequest(rfc.id,interestIds,rfc.creator.id,rfc.creator.institution.id)
    }

    public long getCollaboratorRequestInstitution(long id){
        return wrapper.getCollaboratorRequestInstitution(id)
    }

    public long getCollaboratorRequestCreator(long id){
        return wrapper.getCollaboratorRequestCreator(id)
    }

    public Set<Long> getCollaboratorRequestKeywords(long id){
        return wrapper.getCollaboratorRequestKeywords(id)
    }

    public void removeCollaboratorRequest(CollaboratorRequest rfc) {
        wrapper.removeCollaboratorRequest(rfc.id)
    }

    /**
     *
     * @param firstInterest the interest to be added to
     * @param secondInterest the similar interest to be added
     * @param similarity the similarity between the interests
     */
    public void addToInterests(Interest firstInterest, Interest secondInterest, double similarity){
        wrapper.addToInterests((long)firstInterest.id, (long)secondInterest.id, similarity)
    }


   /**
    *
    * @param firstInterest the interest to be removed from
    * @param secondInterest the similar interest to be removed
    */
    public void removeInterests(Interest firstInterest, Interest secondInterest){
        wrapper.removeInterests((long)firstInterest.id, (long)secondInterest.id)
    }

    public SimilarInterestList getSimilarInterests(Interest interest){
        return wrapper.getSimilarInterests((long)interest.id)
    }

    public SimilarInterestList getSimilarInterests(Interest interest, Set<Long> institutionFilter) {
        return wrapper.getSimilarInterests(interest.id, institutionFilter)
    }

    public void removeLowestSimilarity(Interest interest) {
        wrapper.removeLowestSimilarity(interest.id)
    }


   /**
    *
    * @param interest the interest to replace lowest similarity in
    * @param second the new similar interest
    * @param similarity the new similarity
    */
    public void replaceLowestSimilarity(Interest interest, Interest newInterest, double similarity){
        wrapper.replaceLowestSimilarity(interest.id, newInterest.id, similarity)
    }

    public Set<Long> getInstitutionInterests(long institutionId) {
        return wrapper.getInstitutionInterests(institutionId)
    }

}
