package org.macademia

class WikipediaService {

    static File wikiCache = null
    static transactional = false
    ThreadLocal<Wikipedia> holder = new ThreadLocal<Wikipedia>()

    public Document getDocumentByUrl(String url){
        return getWikipedia().getDocumentByUrl(url)
    }

    public Document getDocumentByName(String title){
        return getWikipedia().getDocumentByName(title)
    }

    public String getCanonicalUrl(String url){
        return getWikipedia().getCanonicalUrl(url)
    }

    public Wikipedia getWikipedia() {
        Wikipedia wiki
        wiki = holder.get()
        if (wiki == null) {
            if (wikiCache != null) {
                wiki = new Wikipedia(wikiCache)
            } else {
                wiki = new Wikipedia()
        }
        holder.set(wiki)    
        }
        return wiki
    }

    public void setCache(File cache){
        wikiCache=cache
        Wikipedia wiki = new Wikipedia(cache)
        holder.set(wiki)
    }
}

