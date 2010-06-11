package org.macademia

import grails.test.*

class InterestRelationTests extends GrailsUnitTestCase {
    protected void setUp() {
        super.setUp()
    }

    protected void tearDown() {
        super.tearDown()
    }

    void testCompare() {
        Interest i1 = new Interest("foo")
        Interest i2 = new Interest("bar")
        Interest i3 = new Interest("baz")
        InterestRelation ir1 = new InterestRelation(first : i1, second : i2, similarity : 0.45)
        InterestRelation ir2 = new InterestRelation(first : i1, second : i3, similarity : 0.9)
        assertTrue(ir1.compareTo(ir2) > 0)
        assertTrue(ir2.compareTo(ir1) < 0)
        def l1 = [ir1, ir2]
        def l2 = [ir2, ir1]
        l1.sort()
        l2.sort()
        assertEquals(l1[0], ir2)
        assertEquals(l2[0], ir2)
    }
}
