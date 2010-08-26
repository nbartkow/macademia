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
    private String wpDbName = "fromWikipedia";

    public MongoWrapper(Mongo mongo, String dbName, String wpDbName){
        this.mongo=mongo;
        this.dbName = dbName;
        this.wpDbName = wpDbName;
    }

    public void changeDB(String dbName) {
        this.dbName = dbName;
    }
    
    public DB getDb(){
        return mongo.getDB(dbName);
    }

    public DB getDb(boolean articleDb) {
        if (articleDb) {
            return mongo.getDB(wpDbName);
        } else {
            return mongo.getDB(dbName);
        }
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

    public DBObject findById(String collection, Object id, boolean articleDb) throws IllegalArgumentException{
        DBObject searchById= new BasicDBObject("_id", id);
        DBCollection coll = getDb(articleDb).getCollection(collection);
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

    public DBObject safeFindById(String collection, Object id, boolean articleDb){
        try{
            return findById(collection, id, articleDb);
        }   catch(IllegalArgumentException e){
            //System.out.println(e.getMessage());
            return null;
        }
    }

    public void addUser(Long userId, List<Long> userInterests, Long institutionId) throws RuntimeException {
        if(userId == null){
            throw new RuntimeException("User needs an ID");
        }
        if(institutionId == null){
            throw new RuntimeException("users institution has no ID");
        }
        for (long i : userInterests) {
            addInterestToInstitution(institutionId, i);
        }
        DBObject newUser = new BasicDBObject("_id", userId);
        //log.info(interestIds+"addUser")
        newUser.put("interests", userInterests);
        newUser.put("institution", institutionId);
        DBObject searchById = safeFindById(USERS, userId, false);
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
        DBObject user = safeFindById(USERS, id, false);
        return (Long) user.get("institution");
    }

    public List<Long> getUserInterests(long id){
        DBObject user = safeFindById(USERS, id, false);
        List<Long> interests = new ArrayList<Long>();
        BasicDBList userInterests=(BasicDBList) user.get(INTERESTS);
        //interests.addAll((ArrayList<Long>)(ArrayList<Object>)userInterests);
        //interests.addAll(Arrays.asList((Long[]) userInterests.toArray()));
        for (Object l : userInterests) {
            interests.add((Long)l);
        }
        return interests;
    }

    public Set<Long> getInterestUsers(long id) {
        DBObject query = new BasicDBObject("interests", id);
        DBCollection users = getDb(false).getCollection(USERS);
        DBCursor cursor = users.find(query);
        Set<Long> res = new HashSet<Long>();
        for (DBObject user : cursor) {
            res.add((Long)user.get("_id"));
        }
        return res;
    }

    public Set<Long> getInterestRequests(long id) {
        DBObject query = new BasicDBObject("keywords", id);
        DBCollection requests = getDb(false).getCollection(COLLABORATOR_REQUESTS);
        DBCursor cursor = requests.find(query);
        Set<Long> res = new HashSet<Long>();
        for (DBObject request : cursor) {
            res.add((Long)request.get("_id"));
        }
        return res;
    }

    public void addCollaboratorRequest(long rfcId, List<Long> interests, long creatorId, long institutionId){
        DBObject newRFC = new BasicDBObject("_id", rfcId);
        //log.info(interestIds+"addCollaboratorRequest")
        for (long i : interests) {
            addInterestToInstitution(institutionId, i);
        }
        newRFC.put("keywords", interests);
        newRFC.put("creator", creatorId);
        newRFC.put("institution", institutionId);
        DBCollection collaboratorRequests = getDb().getCollection(COLLABORATOR_REQUESTS);
        DBObject searchById = safeFindById(COLLABORATOR_REQUESTS, rfcId, false);
        if(searchById !=null){
            collaboratorRequests.update(searchById, newRFC);
        }
        else{
            collaboratorRequests.insert(newRFC);
        }
    }

    public Long getCollaboratorRequestInstitution(long id){
        DBObject rfc = safeFindById(COLLABORATOR_REQUESTS, id, false);
        Long institutionId;
        if(rfc == null){
            institutionId=new Long(-1);
        } else{
            institutionId=(Long) rfc.get("institution");
        }
        return institutionId;
    }

    public Long getCollaboratorRequestCreator(long id){
        DBObject rfc = safeFindById(COLLABORATOR_REQUESTS, id, false);
        System.out.println("RFC: " + rfc.toString());
        return (Long) rfc.get("creator");
    }

    public Set<Long> getRequestKeywords(long id){
        DBObject rfc = safeFindById(COLLABORATOR_REQUESTS, id, false);
        System.out.println("RFC: " + rfc.toString());
        Set<Long> keywords = new HashSet<Long>();
        for(Object l :(BasicDBList)rfc.get("keywords")){
            keywords.add((Long) l);
        }
        return keywords;
    }

    public void removeCollaboratorRequest(long id) {
        DBCollection collaboratorRequests = getDb().getCollection(COLLABORATOR_REQUESTS);
        DBObject exists = safeFindById(COLLABORATOR_REQUESTS, id, false);
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
        DBObject i = safeFindById(INTERESTS, firstId, false);
        if(i == null){
            i=new BasicDBObject("_id", firstId);
            interests.insert(i);
        }
        //log.info("Similar Interest String added to DB: "+interest)
        //log.info(similar+interest+" addToInterests put" )
        SimilarInterestList sim = new SimilarInterestList((String)i.get("similar"));
        sim.add(new SimilarInterest(secondId, similarity));
        i.put("similar",sim.toString());
        interests.update(safeFindById(INTERESTS, firstId, false),i);
    }


   /**
    *
    * @param firstInterest the interest to be removed from
    * @param secondInterest the similar interest to be removed
    */
    public void removeInterests(Long firstInterest, Long secondInterest){
        DBCollection interests = getDb().getCollection(INTERESTS);
        DBObject i = safeFindById(INTERESTS, firstInterest, false);
        if (i != null) {
            SimilarInterestList similarInterests = new SimilarInterestList((String)i.get("similar"));
            similarInterests.remove(new SimilarInterest(secondInterest, (double)0));
            i.put("similar", similarInterests.toString());
            interests.update(safeFindById(INTERESTS, firstInterest, false),i);
        }
    }

    public long articleToId(String title){
        DBObject res = safeFindById(ARTICLES_TO_IDS, title, true);
        if(res == null){
            System.out.println("Invalid article title no ID found");
            return (long) -1;
        }
        Object wpId = res.get("wpId");
        if (wpId instanceof Integer) {
            return ((Integer)wpId).longValue();
        } else if (wpId instanceof String) {
            return Long.valueOf((String)wpId);
        } else if (wpId instanceof Long) {
            return (Long) wpId;
        } else {
            throw new IllegalStateException("invalid article id: '" + wpId + "'");
        }
    }

    public void buildInterestRelations (long interest, long article, boolean relationsBuilt) {
        System.out.println(article);
        SimilarInterestList articles = getArticleSimilarities(article);
        SimilarInterestList list = new SimilarInterestList();
        int i = 0;
        Map<Long, Double> ids = new HashMap<Long, Double>();
        while (list.size() < 200 && i < articles.size()) {
            SimilarInterest check = articles.get(i);
            DBObject articleToInterests = safeFindById(ARTICLES_TO_INTERESTS, check.interestId, false);
            if (articleToInterests != null) {
                Set<Long> similarInterests = interestStringToSet((String)articleToInterests.get("interests"));
                for (long id : similarInterests) {
                    if (relationsBuilt && id!=interest) {
                        ids.put(id, check.similarity);
                    }
                    if(id!=interest){
                        list.add(new SimilarInterest(id, check.similarity));
                    }
                }
            }
            i++;
        }
        addInterestRelations(interest, list);
        if (relationsBuilt) {
            for (long id : ids.keySet()) {
                SimilarInterestList sims = new SimilarInterestList();
                sims.add(new SimilarInterest(id, ids.get(id)));
                addInterestRelations(id, sims);
            }
        }
    }

    public void addInterestRelations(long interestId, SimilarInterestList sims){
        DBCollection interests = getDb().getCollection(INTERESTS);
        DBObject interest= safeFindById(INTERESTS, interestId, false);
        if(interest == null){
            interest = new BasicDBObject("_id", interestId);
            interest.put("similar", "");
            interests.insert(interest);
        }
        sims.add((String)interest.get("similar"));
        interest.put("similar", sims.toString());
        interests.update(safeFindById(INTERESTS, interestId, false), interest);
    }

    public void addInterestRelations(long interestId, SimilarInterest sim){
        SimilarInterestList sims = new SimilarInterestList();
        sims.add(sim);
        addInterestRelations(interestId, sims);
    }

    public SimilarInterestList getSimilarInterests(long interest){
        //System.out.println(interest + " was the interest");
        DBObject i = safeFindById(INTERESTS, interest, false);
        if (i == null) {
            System.out.println("The interest " + i + " is null");
            return new SimilarInterestList();
        }
        //System.out.println(i +" getSimilarInterests get");
        String res = (String)i.get("similar");
        if (res == null) {
            return new SimilarInterestList();
        }
        return new SimilarInterestList(res);
    }

    public SimilarInterestList getSimilarInterests(Long interest, Set<Long> institutionFilter) {
        DBObject i = safeFindById(INTERESTS, interest, false);
        if (i == null) {
            return new SimilarInterestList();
        }
        //log.info(similar +" getSimilarInterests get")
        Set<Long> institutionInterests = new HashSet<Long>();
        for (long id : institutionFilter) {
            institutionInterests.addAll(getInstitutionInterests(id));
        }
        return new SimilarInterestList((String)i.get("similar"), institutionInterests);
    }

    public void removeLowestSimilarity(Long interest) {
        DBCollection interests = getDb().getCollection(INTERESTS);
        DBObject i = safeFindById(INTERESTS, interest, false);
        SimilarInterestList similarInterests = new SimilarInterestList((String)i.get("similar"));
        similarInterests.removeLowest();
        i.put("similar", similarInterests.toString());
        interests.update(safeFindById(INTERESTS, interest, false),i);
    }


   /**
    *
    * @param interest the interest to replace lowest similarity in
    * @param newInterest the new similar interest
    * @param similarity the new similarity
    */
    public void replaceLowestSimilarity(Long interest, Long newInterest, Double similarity){
        DBCollection interests = getDb().getCollection(INTERESTS);
        DBObject i = safeFindById(INTERESTS, interest, false);
        SimilarInterestList similarInterests = new SimilarInterestList((String)i.get("similar"));
        similarInterests.add(new SimilarInterest(newInterest, similarity));
        similarInterests.removeLowest();
        i.put("similar", similarInterests.toString());
        interests.update(safeFindById(INTERESTS, interest, false),i);
    }

    private void addInterestToInstitution(long institutionId ,long interestId){
        DBObject institution = safeFindById(INSTITUTION_INTERESTS, institutionId, false);
        DBCollection institutionInterests=getDb().getCollection(INSTITUTION_INTERESTS);
        if(institution==null){
            institution= new BasicDBObject("_id", institutionId);
            institution.put("interests","");
            institutionInterests.insert(institution);
        }
        String res = interestSetToString(interestStringToSet(institution.get("interests")+
                Long.toString(interestId)+","));
        institution.put("interests",res);
        institutionInterests.update(safeFindById(INSTITUTION_INTERESTS, institutionId, false),institution);
    }

    public Set<Long> getInstitutionInterests(long id) {
        DBObject institution = safeFindById(INSTITUTION_INTERESTS, id, false);
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

    public SimilarInterestList getArticleSimilarities(long article) {
        DBObject similarities = safeFindById(ARTICLE_SIMILARITIES, article, true);
        if (similarities == null) {
            System.out.println(article + " does not have an articleSimilarities entry");
            return new SimilarInterestList();
        }
        //System.out.println(article);
        //really long print ln statement below
        //System.out.println(similarities.toString());
        return new SimilarInterestList((String)similarities.get("similarities"));
    }

    public void addInterestToArticle(long interest, long article){
        DBObject articleInterests = safeFindById(ARTICLES_TO_INTERESTS , article, false);
        DBCollection articlesToInterests = getDb().getCollection(ARTICLES_TO_INTERESTS);
        if(articleInterests==null){
            articleInterests=new BasicDBObject("_id", article);
            articleInterests.put("interests","");
            articlesToInterests.insert(articleInterests);
        }
        Set<Long> interests = interestStringToSet((String)articleInterests.get("interests"));
        interests.add(interest);
        articleInterests.put("interests", interestSetToString(interests));
        articlesToInterests.update(safeFindById(ARTICLES_TO_INTERESTS, article, false), articleInterests);

    }

}
