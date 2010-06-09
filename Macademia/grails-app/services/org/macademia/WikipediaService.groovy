package org.macademia

class WikipediaService {

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
        if (holder.get() == null) {
            holder.set(new Wikipedia())
        }
        return holder.get()
    }
}

