package org.macademia

import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware

/**
 * Authors: Nathaniel Miller and Alex Schneeman
 */

class InterestService implements ApplicationContextAware {

    ApplicationContext applicationContext
    def xSimilarityService
    def googleServiceProxy
    def wikipediaServiceProxy

    boolean transactional = true

    public Interest get(long id) {
        return Interest.get(id)
    }

    public Interest findByText(String text) {
//        return Interest.find("from Interest as i where i.normalizedText = ?", [Interest.normalize(text)])
        return Interest.findByNormalizedText(Interest.normalize(text))
    }

    public List<InterestRelation> findSimilarities(String interest) {
        Interest i = findByText(interest)
        return (i == null) ? [] : findSimilarities(i)
    }

    public List<InterestRelation> findSimilarities(Interest interest) {
        return InterestRelation.findAllByFirst(interest)
    }

    public Map<String, Double> findSimilaritiesAsMap(String interest) {
        Interest i = findByText(interest)
        return (i == null) ? [:] : findSimilaritiesAsMap(i)
    }

    public Map<String, Double> findSimilaritiesAsMap(Interest interest) {
        def res = [:]
        findSimilarities(interest).each { res[it.second.normalizedText] = it.similarity }
        return res
    }

    public void analyzeInterest(Interest interest) {
        analyzeInterest(interest, new Wikipedia(), new Google())

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
        for (String url : googleServiceProxy.query(interest.text, 5)) {
            weight *= 0.5;
            String url2 = wikipediaServiceProxy.getCanonicalUrl(url)
            if (!url2) {
                log.error("canonicalizing of $url failed")
                continue
            }
            Document d = Document.findByUrl(url2)
            if (d == null) {
                d = wikipediaServiceProxy.getDocumentByUrl(url2)
                if (!d) {
                    log.error("retrieval of $url (canonical form is $url2) failed")
                    continue
                } else if (!d.save(flush : true)) {
                    log.error("saving failed!")
                    continue
                }
            }
            InterestDocument id = new InterestDocument(document : d, weight : weight)
            interest.addToDocuments(id)
            id.save()
        }
        interest.lastAnalyzed = new Date()
        interest.save()
    }


  public void save(Interest interest) {
      if (interest.id == null) {
            //no interest with text in db, new interest
            if (xSimilarityService == null) {
              xSimilarityService = applicationContext.getBean("similarityService")
            }
            //must save prior to finding similar interests
            interest.save()
            buildDocuments(interest)
            xSimilarityService.buildInterestRelations(interest)
      } /*else {
           //id, already in db
           interest.save()
      }   */ //not necessary?
  }

  
}
