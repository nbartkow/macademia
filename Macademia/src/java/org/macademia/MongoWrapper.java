package org.macademia;


import com.mongodb.*;
import org.bson.BSONObject;

import java.net.UnknownHostException;
import java.util.*;

/**
 * Authors: Nathaniel Miller and Alex Schneeman
 */
public class MongoWrapper {
    Mongo mongo ;
    
    //name of the users collection
    public static final String USERS = "users";

    //name of the interests collection
    public static final String INTERESTS = "interests";

    //name of the collaboratorRequests collection
    public static final String COLLABORATOR_REQUESTS = "collaboratorRequests" ;

    //name of the articlesToIds collection
    public static final String ARTICLES_TO_IDS = "articlesToIds";

    //name of the articlesToInterests collection
    public static final String ARTICLES_TO_INTERESTS = "articlesToInterests";

    //name of the articleSimilarities collection
    public static final String ARTICLE_SIMILARITIES = "articleSimilarities";

    public static final String INSTITUTION_INTERESTS ="institutionInterests";

    private String dbName = null;

    public MongoWrapper(Mongo mongo, String dbName){
        this.mongo=mongo;
        this.dbName = dbName;
    }

    public void changeDB(String dbName) {
        this.dbName = dbName;
    }
    
    public DB getDb(){
        return mongo.getDB(dbName);
    }

    public void copyDB(String fromDB, String dbName) {
        DB db = mongo.getDB( "admin" );
        //System.out.println("copying from '" + fromDB + "' to '" + dbName + "'");
        db.command(((String)("use " + fromDB)));
        BasicDBObjectBuilder b = BasicDBObjectBuilder.start();
        b.append("copydb", 1);
        b.append("fromhost", "localhost");
        b.append("fromdb", fromDB);
        b.append("todb", dbName);
        CommandResult cmd = db.command(b.get());
        //CommandResult cmd2 = db.command(((String)("use "+dbName)));
    }

    public void dropDB(String dbName) {
        mongo.dropDatabase(dbName);
    }

    public void dropCurrentDB(){
        mongo.dropDatabase(dbName);
    }

    public void switchToCopyDB(String toCopy) {
        Random rand = new Random();
        String dbName = "tmp" + rand.nextInt(10000);
        copyDB(toCopy, dbName);
        changeDB(dbName);
    }

    public DBObject findById(String collection, Long id) throws IllegalArgumentException{
        DBObject searchById= new BasicDBObject("_id", id);
        DBCollection coll = getDb().getCollection(collection);
        //System.out.println("DBCollection: " + coll.toString());
        //for (DBObject o : coll.find()) {
            //System.out.println("DBObject in collection: " + o.toString());
        //}
        DBObject res = coll.findOne(searchById);
        if(res==null){
            throw new IllegalArgumentException("No record found in "+collection+" with id "+id.toString() );
        }
        return res;
    }

    public DBObject safeFindById(String collection, Long id){
        try{
            return findById(collection, id);
        }   catch(IllegalArgumentException e){
            //System.out.println(e.getMessage());
            return null;
        }
    }

    public void addUser(Long userId, String userInterests, Long institutionId) throws RuntimeException {
        if(userId == null){
            throw new RuntimeException("User needs an ID");
        }
        if(institutionId == null){
            throw new RuntimeException("users institution has no ID");
        }
        for (long i : interestStringToSet(userInterests)) {
            addInterestToInstitution(institutionId, i);
        }
        DBObject newUser = new BasicDBObject("_id", userId);
        //log.info(interestIds+"addUser")
        newUser.put("interests", userInterests);
        newUser.put("institution", institutionId);
        DBObject searchById = safeFindById(USERS, userId);
        DBCollection users = getDb().getCollection(USERS);
        if(searchById != null){
            users.update(searchById, newUser);
        }
        else{
            users.insert(newUser);
        }
        //log.info("User ID: "+id)
        //log.info("Institution ID: "+institutionId)

    }

    public Long getUserInstitution(long id){
        DBObject user = safeFindById(USERS, id);
        return (Long) user.get("institution");
    }

    public List<Long> getUserInterests(long id){
        DBObject user = safeFindById(USERS, id);
        List<Long> interests = new ArrayList<Long>();
        String userInterests= (String) user.get(INTERESTS);
        for(String interest : userInterests.split(",") ){
            if (!interest.equals("")) {
                interests.add(new Long(interest));
            }
        }
        return interests;
    }

    public void addCollaboratorRequest(long rfcId, String interests, long creatorId, long institutionId){
        DBObject newRFC = new BasicDBObject("_id", rfcId);
        //log.info(interestIds+"addCollaboratorRequest")
        for (long i : interestStringToSet(interests)) {
            addInterestToInstitution(institutionId, i);
        }
        newRFC.put("keywords", interests);
        newRFC.put("creator", creatorId);
        newRFC.put("institution", institutionId);
        DBCollection collaboratorRequests = getDb().getCollection(COLLABORATOR_REQUESTS);
        DBObject searchById = safeFindById(COLLABORATOR_REQUESTS, rfcId);
        if(searchById !=null){
            collaboratorRequests.update(searchById, newRFC);
        }
        else{
            collaboratorRequests.insert(newRFC);
        }
    }

    public Long getCollaboratorRequestInstitution(long id){
        DBObject rfc = safeFindById(COLLABORATOR_REQUESTS, id);
        Long institutionId;
        if(rfc == null){
            institutionId=new Long(-1);
        } else{
            institutionId=(Long) rfc.get("institution");
        }
        return institutionId;
    }

    public Long getCollaboratorRequestCreator(long id){
        DBObject rfc = safeFindById(COLLABORATOR_REQUESTS, id);
        System.out.println("RFC: " + rfc.toString());
        return (Long) rfc.get("creator");
    }

    public Set<Long> getCollaboratorRequestKeywords(long id){
        DBObject rfc = safeFindById(COLLABORATOR_REQUESTS, id);
        System.out.println("RFC: " + rfc.toString());
        return interestStringToSet((String) rfc.get("keywords"));
    }

    public void removeCollaboratorRequest(long id) {
        DBCollection collaboratorRequests = getDb().getCollection(COLLABORATOR_REQUESTS);
        DBObject exists = safeFindById(COLLABORATOR_REQUESTS, id);
        collaboratorRequests.remove(exists);
    }

    /**
     *
     * @param firstId the interest to be added to
     * @param secondId the similar interest to be added
     * @param similarity the similarity between the interests
     */
    public void addToInterests(long firstId, long secondId, double similarity){
        //log.info("Similar Interest Id before added to DB: "+secondId)
        DBCollection interests = getDb().getCollection(INTERESTS);
        DBObject i = safeFindById(INTERESTS, firstId);
        if(i == null){
            i=new BasicDBObject("_id", firstId);
            interests.insert(i);
        }
        //log.info("Similar Interest String added to DB: "+interest)
        //log.info(similar+interest+" addToInterests put" )
        SimilarInterestList sim = new SimilarInterestList((String)i.get("similar"));
        sim.add(new SimilarInterest(secondId, similarity));
        i.put("similar",sim.toString());
        interests.update(safeFindById(INTERESTS, firstId),i);
    }


   /**
    *
    * @param firstInterest the interest to be removed from
    * @param secondInterest the similar interest to be removed
    */
    public void removeInterests(Long firstInterest, Long secondInterest){
        DBCollection interests = getDb().getCollection(INTERESTS);
        DBObject i = safeFindById(INTERESTS, firstInterest);
        if (i != null) {
            SimilarInterestList similarInterests = new SimilarInterestList((String)i.get("similar"));
            similarInterests.remove(new SimilarInterest(secondInterest, (double)0));
            i.put("similar", similarInterests.toString());
            interests.update(safeFindById(INTERESTS, firstInterest),i);
        }
    }

    public SimilarInterestList getSimilarInterests(long interest){
        //System.out.println(interest + " was the interest");
        DBObject i = safeFindById(INTERESTS, interest);
        //if (i == null) {
            //System.out.println("The interest " + i + " is null");
        //}
        //log.info(similar +" getSimilarInterests get")
        return new SimilarInterestList((String)i.get("similar"));
    }

    public SimilarInterestList getSimilarInterests(Long interest, Set<Long> institutionFilter) {
        DBObject i = safeFindById(INTERESTS, interest);
        //log.info(similar +" getSimilarInterests get")
        Set<Long> institutionInterests = new HashSet<Long>();
        for (long id : institutionFilter) {
            institutionInterests.addAll(getInstitutionInterests(id));
        }
        return new SimilarInterestList((String)i.get("similar"), institutionInterests);
    }

    public void removeLowestSimilarity(Long interest) {
        DBCollection interests = getDb().getCollection(INTERESTS);
        DBObject i = safeFindById(INTERESTS, interest);
        SimilarInterestList similarInterests = new SimilarInterestList((String)i.get("similar"));
        similarInterests.removeLowest();
        i.put("similar", similarInterests.toString());
        interests.update(safeFindById(INTERESTS, interest),i);
    }


   /**
    *
    * @param interest the interest to replace lowest similarity in
    * @param newInterest the new similar interest
    * @param similarity the new similarity
    */
    public void replaceLowestSimilarity(Long interest, Long newInterest, Double similarity){
        DBCollection interests = getDb().getCollection(INTERESTS);
        DBObject i = safeFindById(INTERESTS, interest);
        SimilarInterestList similarInterests = new SimilarInterestList((String)i.get("similar"));
        similarInterests.add(new SimilarInterest(newInterest, similarity));
        similarInterests.removeLowest();
        i.put("similar", similarInterests.toString());
        interests.update(safeFindById(INTERESTS, interest),i);
    }

    private void addInterestToInstitution(long institutionId ,long interestId){
        DBObject institution = safeFindById(INSTITUTION_INTERESTS, institutionId);
        DBCollection institutionInterests=getDb().getCollection(INSTITUTION_INTERESTS);
        if(institution==null){
            institution= new BasicDBObject("_id", institutionId);
            institution.put("interests","");
            institutionInterests.insert(institution);
        }
        String res = interestSetToString(interestStringToSet(institution.get("interests")+
                Long.toString(interestId)+","));
        institution.put("interests",res);
        institutionInterests.update(safeFindById(INSTITUTION_INTERESTS, institutionId),institution);
    }

    public Set<Long> getInstitutionInterests(long id) {
        DBObject institution = safeFindById(INSTITUTION_INTERESTS, id);
        if(institution == null) {
            return new HashSet<Long>();
        }
        return interestStringToSet((String)institution.get("interests"));
    }

    private String interestSetToString(Set<Long> interests) {
        String res = "";
        for (Long i : interests) {
            res = res + i.toString() + ",";
        }
        return res;
    }

    private Set<Long> interestStringToSet(String interestString) {
        Set<Long> res = new HashSet<Long>();
        String[] interests = interestString.split(",");
        for (String i : interests) {
            if (i.length() > 0) {
                res.add(Long.parseLong(i));
            }
        }
        return res;
    }

}
