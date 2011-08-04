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
        wikipediaService.setCache(new File(fileDirectory + "wikipedia_cache.txt"))
    }

    /**
     * Finds the most relevant document(s) for the interest
     * @param interest : an interest in the interest list
     */
    public void buildDocuments(Interest interest) {
//        log.info("doing interest ${interest}")
        double weight = 1.0
        for (String url: wikipediaService.query(interest.text, 1)) {
            weight *= 0.5;
            String articleName = wikipediaService.decodeWikiUrl(url)
            interest.articleId = databaseService.articleToId(articleName)
            interest.articleName = articleName
            databaseService.addInterestToArticle(interest, interest.articleId)
            Utils.safeSave(interest)
        }
        interest.lastAnalyzed = new Date()
    }

    public void save(Interest interest) {
        if (interest.id == null) {
            //no interest with text in db, new interest
            //must save prior to finding similar interests
            Utils.safeSave(interest)
        }
        if (interest.articleId < 1 || interest.articleId == null) {
            buildDocuments(interest)
            if (similarityService.relationsBuilt) {
                similarityService.buildInterestRelations(interest)
            }
            Utils.safeSave(interest)
        }
    }

    public void delete(person, interestId) {
        autocompleteService.removeInterest(person, interestId)
        Interest.get(interestId).delete()
    }

    public void reapOrphans() {
        List<Long> theReaped = databaseService.reapOrphans()
        for (reaped in theReaped) {
            if (Interest.get(reaped) != null) {
                Interest.get(reaped).delete()
            }
        }

    }

    public void deleteOld(oldInterests, Person person) {
        for (interest in oldInterests) {
            if (!person.interests.contains(interest)) {
                if (interestRemove(interest, person))
                    delete(person, interest.id)
            }
        }
    }

    public void deleteOld(oldKeywords, CollaboratorRequest request) {
        for (interest in oldKeywords) {
            if (!request.keywords.contains(interest)) {
                if (keywordRemove(interest, request))
                    delete(request.creator, interest.id)
            }
        }
    }

    public interestRemove(Interest interest, Person person) {
        return databaseService.removeInterestFromUser(interest.id, person.id)
    }

    public keywordRemove(Interest keyword, CollaboratorRequest request) {
        return databaseService.removeKeywordFromRequest(keyword.id, request.id)
    }

    public List<Interest> parseInterests(String interests) {
        String[] tokens = tokenizer(interests)
        List<Interest> interestList = []
        for (i in tokens){
            if (i.trim().length() != 0) {
                Interest existingInterest = findByText(i)
                if (existingInterest != null){
                    interestList.add(existingInterest)
                } else {
                    Interest newInterest = new Interest(i)
                    interestList.add(newInterest)
                }
            }
        }
        return interestList
    }

    public String[] tokenizer(String interests){
        return interests.trim().split(",")
    }

}