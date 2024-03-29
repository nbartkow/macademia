package org.macademia

import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware

/**
 * Authors: Nathaniel Miller and Alex Schneeman
 */
class InterestService implements ApplicationContextAware {

    ApplicationContext applicationContext
    def similarityService
    def wikipediaService
    def databaseService
    def autocompleteService

    boolean transactional = true

    public Interest get(long id) {
        return Interest.get(id)
    }

    public Interest findByText(String text) {
        return Interest.findByNormalizedText(Interest.normalize(text))
    }

    public SimilarInterestList findSimilarities(String interest) {
        Interest i = findByText(interest)
        return (i == null) ? new SimilarInterestList() : findSimilarities(i)
    }

    public SimilarInterestList findSimilarities(Interest interest) {
        return similarityService.getSimilarInterests(interest)
    }

    public void initBuildDocuments(String fileDirectory) {
        wikipediaService.setCache(new File(fileDirectory+"wikipedia_cache.txt"))
    }

    /**
     * Finds the most relevant document for the interest
     * @param interest The Interest whose relevant document
     * is to be built.
     */
    public void buildDocuments(Interest interest) {
        interest.articleId = -1
        for (String url : wikipediaService.query(interest.text, 1)) {
            String articleName = wikipediaService.decodeWikiUrl(url)
            interest.articleId = databaseService.articleToId(articleName)
            interest.articleName = articleName
        }
        databaseService.addInterestToArticle(interest, interest.articleId)
        Utils.safeSave(interest)
        interest.lastAnalyzed = new Date()
    }

    public void save(Interest interest) {
        if (interest.id == null) {
            //no interest with text in db, new interest
            //must save prior to finding similar interests
            Utils.safeSave(interest)
        }
        if (interest.articleId == null || interest.articleId < 1) {
            buildDocuments(interest)
            if (similarityService.relationsBuilt) {
                similarityService.buildInterestRelations(interest)
            }
            Utils.safeSave(interest)
        }
    }

    /**
     * Should the parameter String interest text not be
     * an existing interest, creates and saves the new
     * interest, building its relations.
     * @param text The String interest text to be analyzed.
     * @return An Interest of the parameter text, guaranteed
     * to have been saved and to have relational data.
     */
    public Interest analyze(String text) {
        Interest interest = findByText(text)
        if (!interest) {
            interest = new Interest(text)
            save(interest)
        }
        return interest
    }

    public void delete(person,  interestId){
        autocompleteService.removeInterest(person, interestId)
        Interest.get(interestId).delete()
    }

    public void reapOrphans(){
        List<Long> theReaped = databaseService.reapOrphans()
        for (reaped in theReaped){
            if (Interest.get(reaped) != null){
                Interest.get(reaped).delete()
            }
        }
    }

    public void deleteOld(oldInterests, Person person){
        for (interest in oldInterests){
            if (!person.interests.contains(interest)){
                if (interestRemove(interest, person))
                    delete(person, interest.id)
            }
        }
    }

    public void deleteOld(oldKeywords, CollaboratorRequest request){
        for (interest in oldKeywords){
            if (!request.keywords.contains(interest)){
                if (keywordRemove(interest, request))
                    delete(request.creator, interest.id)
            }
        }
    }

    public interestRemove(Interest interest, Person person){
        return databaseService.removeInterestFromUser(interest.id, person.id)
    }

    public keywordRemove(Interest keyword, CollaboratorRequest request){
        return databaseService.removeKeywordFromRequest(keyword.id, request.id)
    }

}
