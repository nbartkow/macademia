package org.macademia

class WikipediaService {

    static scope = "request"
    static proxy = true
    static transactional = false
    Wikipedia wikipedia=new Wikipedia()

    public Document getDocumentByUrl(String url){
        return wikipedia.getDocumentByUrl(url)
    }

    public Document getDocumentByName(String title){
        return wikipedia.getDocumentByName(title)
    }

    public String getCanonicalUrl(String url){
        return wikipedia.getCanonicalUrl(url)
    }
}
