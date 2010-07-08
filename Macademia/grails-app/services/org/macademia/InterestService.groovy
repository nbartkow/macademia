package org.macademia

import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware

/**
 * Authors: Nathaniel Miller and Alex Schneeman
 */

class InterestService implements ApplicationContextAware {

    ApplicationContext applicationContext
    def xSimilarityService
    def googleService
    def wikipediaService
    def databaseService

    boolean transactional = true

    public Interest get(long id) {
        return Interest.get(id)
    }

    public Interest findByText(String text) {
//        return Interest.find("from Interest as i where i.normalizedText = ?", [Interest.normalize(text)])
        return Interest.findByNormalizedText(Interest.normalize(text))
    }

    public SimilarInterestList findSimilarities(String interest) {
        Interest i = findByText(interest)
        return (i == null) ? new SimilarInterestList() : findSimilarities(i)
    }

    public SimilarInterestList findSimilarities(Interest interest) {
        if (xSimilarityService == null) {
              xSimilarityService = applicationContext.getBean("similarityService")
        }
        return xSimilarityService.getSimilarInterests(interest)
    }

    public void initBuildDocuments(String fileDirectory){
        googleService.setCache (new File(fileDirectory+"google_cache.txt"))
        wikipediaService.setCache(new File(fileDirectory+"wikipedia_cache.txt"))
    }

   /**
    * Finds the most relevant document(s) for the interest
    * @param interest : an interest in the interest list
    * @param wikipedia : wikipedia
    * @param google : google
    */
    public void buildDocuments(Interest interest) {

        log.info("doing interest ${interest}")
        double weight = 1.0
        for (String url : googleService.query(interest.text, 1)) {
            weight *= 0.5;
            String articleName = wikipediaService.decodeWikiUrl(url)
            interest.articleId = databaseService.articleToId(articleName)
            interest.articleName = articleName
            databaseService.addInterestToArticle(interest, interest.articleId)
            //InterestDocument id = new InterestDocument(document : d, weight : weight)
           // id.interest = interest
            //Utils.safeSave(id)
            Utils.safeSave(interest)
        }
        interest.lastAnalyzed = new Date()
        //Utils.safeSave(interest)
    }

    public void save(Interest interest) {
        if (interest.id == null) {
            //no interest with text in db, new interest
            if (xSimilarityService == null) {
                xSimilarityService = applicationContext.getBean("similarityService")
            }
            //must save prior to finding similar interests
            Utils.safeSave(interest)
        }
        if (interest.articleId < 1 || interest.articleId == null) {
            buildDocuments(interest)
            if (xSimilarityService == null) {
                  xSimilarityService = applicationContext.getBean("similarityService")
            }
            if (xSimilarityService.relationsBuilt) {
                xSimilarityService.buildInterestRelations(interest)
            }
            Utils.safeSave(interest)
        }
    }


}
