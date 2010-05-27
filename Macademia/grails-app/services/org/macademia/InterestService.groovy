package org.macademia

class InterestService {

    boolean transactional = true

    public Interest get(long id) {
        return Interest.get(id)
    }

    public Interest findByText(String text) {
        return Interest.find("from Interest as i where i.normalizedText = ?", [Interest.normalize(text)])
//        return Interest.findByNormalizedText(Interest.normalize(text))
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
    public void analyzeInterest(Interest interest, Wikipedia wikipedia, Google google) {
        log.info("doing interest ${interest}")
        double weight = 1.0
        for (String url : google.query(interest.text, 5)) {
            weight *= 0.5;
            String url2 = Wikipedia.getCanonicalUrl(url)
            if (!url2) {
                log.error("canonicalizing of $url failed")
                continue
            }
            Document d = Document.findByUrl(url2)
            if (d == null) {
                d = wikipedia.getDocumentByUrl(url2)
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
            id.save(flush : true)
        }
        interest.lastAnalyzed = new Date()
        interest.save(flush : true)
    }
}
