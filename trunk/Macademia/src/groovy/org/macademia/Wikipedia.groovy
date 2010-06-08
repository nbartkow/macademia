package org.macademia

import info.bliki.api.User
import info.bliki.api.Connector
import info.bliki.api.Page
import info.bliki.wiki.model.WikiModel
import info.bliki.wiki.filter.PlainTextConverter
import grails.util.Environment

/**
 * @author Shilad
 */
public class Wikipedia {

    private static String WIKIPEDIA_URL = "http://en.wikipedia.org"
    private static String ARTICLE_PREFIX = "${WIKIPEDIA_URL}/wiki/"
    private static String WIKIPEDIA_API_URL = "${WIKIPEDIA_URL}/w/api.php"
    private static File TEST_CACHE_FILE = new File("db/test/wikipedia.cache.txt")
    
    private String userName = "macademiabot"
    private String password = "goscots"
    private String apiUrl = WIKIPEDIA_API_URL
    private User user;
    private DiskMap cache = null

    public Wikipedia() {
        if (Utils.isTestDb()) {
            cache = new DiskMap(TEST_CACHE_FILE)
        }
    }

    public Document getDocumentByUrl(String url) {
        String url2 = getCanonicalUrl(url)
        String name = decodeWikiUrl(url2)
        if (name) {
            Document d = getDocumentByName(name)
            if (d) {
                d.url = url2
                return d
            } else {
                return null
            }
        } else {
            return null
        }
    }

    /**
     * Retrieves the Wikipedia page with the given title as a Document object.
     */
    public Document getDocumentByName(String title) {
        if (!title) {
            return null
        }
        if (cache != null && cache.contains(title)) {
            def info = cache.get(title)
            return new Document(info)
        }
        login()
        Connector connector = user.getConnector()
        List<String> titles = new ArrayList<String>([title])
        List<Page> pages = connector.queryContent(user, titles)
        if (!pages.isEmpty()) {
            Page first = pages[0]
            if (first.title) {
                WikiModel wikiModel = new WikiModel("", "")
                String text = wikiModel.render(new PlainTextConverter(), first.currentContent)
                String url = encodeWikiUrl(first.title)
                if (cache) {
                    cache.put(title, [url : url, name : first.title, text : text])
                }
                return new Document(url : url, name : first.title, text : text)
            }
        }
        if (cache) {
            cache.put(title, null)
        }
    }

    public void setUserName(String userName) {
        user = null
        this.userName = userName
    }

    public void setPassword(String password) {
        user = null
        this.password = password
    }

    public void setApiUrl(String apiUrl) {
        user = null
        this.apiUrl = apiUrl
    }

    public void login() {
        if (user == null) {
            user = new User(userName, password, apiUrl)
            if (!user.login()) {
              throw new RuntimeException("login failed");
            }
        }
    }

    /**
     * Returns the title of a Wikipedia page located at a particular URL. 
     */
    private static URLDecoder decoder = new URLDecoder();
    public static String decodeWikiUrl(String url) {
        if (url.indexOf("/") < 0) {
            return null
        }
        String ending = url.substring(url.lastIndexOf("/")+1);
        String decoded = decoder.decode(ending, "UTF-8");
        return decoded.replace('_', ' ');
    }


    private static URLEncoder encoder = new URLEncoder();

    /**
     * Returns the url location of a Wikipedia page with a particular title.
     */
    public static String encodeWikiUrl(String name) {
        String encoded = name.replace(' ', '_')
        return ARTICLE_PREFIX + encoder.encode(encoded, "UTF-8")
    }

    /**
     * Returns the canonical, normalized version of the passed in Wikipedia url
     */
    public static String getCanonicalUrl(String url) {
        String name = decodeWikiUrl(url)
        if (!name) {
            return null
        } else {
            return encodeWikiUrl(name)
        }
    }

    /**
     * Ensures that a page is in the default namespace (not a talk page, for instance)
     * @param url
     * @return
     */
    public static boolean isUrlForNormalPage(String url) {
        if (url.startsWith("http://")) {
            String suffix = url.substring(7).toLowerCase();
            if (!suffix.contains("%3a") && !suffix.contains(":")) {
                return true
            }
        }
        return false
    }
}