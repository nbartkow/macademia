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
}
