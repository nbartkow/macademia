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
    MongoWrapper wrapper = new MongoWrapper(
                                    mongo,
                                    (String)ConfigurationHolder.config.dataSource.mongoDbName,
                                    (String)ConfigurationHolder.config.dataSource.wpMongoDbName
                                )

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
        return wrapper.findById(collection, id, false)
    }

    public DBObject safeFindById(String collection, Long id){
        return wrapper.safeFindById(collection, id, false)
    }

    public void addUser(Person user) throws RuntimeException {
        long id = user.id
        if(id == null){
            throw new RuntimeException("User needs an ID")
        }
        Set<Interest> interests=user.interests
        List<Long> interestIds = new ArrayList<Long>()
        for(Interest interest : interests){
            if(interest.id ==null){
                throw new RuntimeException("User has an interest with out an ID")
            }
            interestIds.add(+interest.id)
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

    public Set<Long> getInterestUsers(long id) {
        return wrapper.getInterestUsers(id)
    }

    public Set<Long> getInterestRequests(long id) {
        return wrapper.getInterestRequests(id)
    }

    public void addCollaboratorRequest(CollaboratorRequest rfc){
        List<Long> interestIds = new ArrayList<Long>()
        for(Interest interest : rfc.keywords){
            if(interest.id ==null){
                throw new RuntimeException("User has an interest with out an ID")
            }
            interestIds.add(interest.id)
        }
        wrapper.addCollaboratorRequest(rfc.id,interestIds,rfc.creator.id,rfc.creator.institution.id)
    }

    public long getCollaboratorRequestInstitution(long id){
        return wrapper.getCollaboratorRequestInstitution(id)
    }

    public long getCollaboratorRequestCreator(long id){
        return wrapper.getCollaboratorRequestCreator(id)
    }

    public Set<Long> getRequestKeywords(long id){
        return wrapper.getRequestKeywords(id)
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


    public void addToInterests(long firstId, long secondId, double sim) {
        wrapper.addToInterests(firstId, secondId, sim)
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

    public SimilarInterestList getSimilarInterests(Long id) {
        return wrapper.getSimilarInterests(id)
    }

    public SimilarInterestList getSimilarInterests(Long id, Set<Long> institutionFilter) {
        return wrapper.getSimilarInterests(id, institutionFilter)
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

    public void addInterestToArticle(Interest interest, long article){
        wrapper.addInterestToArticle(interest.id, article);
    }

    public long articleToId(String title){
        return wrapper.articleToId(title);
    }

    public void buildInterestRelations(long interest, long article, boolean relationsBuilt) {
        wrapper.buildInterestRelations(interest, article, relationsBuilt)
    }

}
