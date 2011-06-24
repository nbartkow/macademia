package org.macademia

import grails.test.*

class InstitutionServiceTests extends GrailsUnitTestCase {
    protected void setUp() {
        super.setUp()
    }

    protected void tearDown() {
        super.tearDown()
    }

    void testNormalizeWebUrl(){
        def institutionService = new InstitutionService()
        assertEquals("www.macalester.edu",institutionService.normalizeWebUrl("macalester.edu"))
        assertEquals("www.macalester.edu",institutionService.normalizeWebUrl("www.macalester.edu"))
        assertEquals("www.macalester.edu",institutionService.normalizeWebUrl("http://macalester.edu"))
        assertEquals("macalester.edu.mn",institutionService.normalizeWebUrl("macalester.edu.mn"))
        assertEquals("url not valid",institutionService.normalizeWebUrl("wwwwwwwwww"))
        assertEquals("url not valid",institutionService.normalizeWebUrl("www.macalester"))
        assertEquals("macalester.edu.mn",institutionService.normalizeWebUrl("macalester.edu.mn/hello"))
        assertEquals("www.macalester.edu",institutionService.normalizeWebUrl("macalester.edu/asd"))
        assertEquals("foo.macalester.edu",institutionService.normalizeWebUrl("foo.macalester.edu/asd"))
        assertEquals("bar.foo.macalester.edu",institutionService.normalizeWebUrl("bar.foo.macalester.edu/asd"))
        assertEquals("acm.macalester.edu",institutionService.normalizeWebUrl("acm.macalester.edu"))
    }
}
