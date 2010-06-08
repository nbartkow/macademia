package org.macademia

import grails.test.*

class WikipediaServiceTests extends GrailsUnitTestCase {
    protected void setUp() {
        super.setUp()
    }

    protected void tearDown() {
        super.tearDown()
    }
//
//    void testEncoding() {
//        Wikipedia w = new Wikipedia()
//        String encoded = w.encodeWikiUrl("My Favorite Things (album)")
//        assertEquals(encoded, "http://en.wikipedia.org/wiki/My_Favorite_Things_%28album%29")
//    }
//
//    void testDecoding() {
//        Wikipedia w = new Wikipedia()
//        String decoded = w.decodeWikiUrl("http://en.wikipedia.org/wiki/My_Favorite_Things_%28album%29")
//        assertEquals(decoded, "My Favorite Things (album)")
//    }
//
//    void testRetrieve() {
//        Wikipedia w = new Wikipedia()
//        Document d = w.getDocumentByName("My Favorite Things (album)")
//        assertEquals(d.url, "http://en.wikipedia.org/wiki/My_Favorite_Things_%28album%29")
//        assertEquals(d.name, "My Favorite Things (album)")
//        println("cntext is ${d.text}")
//        assertTrue(d.text.contains("My Favorite Things is a 1961 jazz album by John Coltrane"))
//    }

    void testWorkLife() {
        WikipediaService w = new WikipediaService()
//        Document d = w.getDocumentByUrl(URLDecoder.decode("Work%E2%80%93life_balance"))
        Document d = w.getDocumentByUrl("http://en.wikipedia.org/wiki/Work%E2%80%93life_balance")
        println("d is " + d)
    }
}
