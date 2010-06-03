package org.macademia

/**
 * Created by IntelliJ IDEA.
 * User: aschneem
 * Date: Jun 3, 2010
 * Time: 11:00:18 AM
 * To change this template use File | Settings | File Templates.
 */
import grails.test.*

class EdgeTests extends GrailsUnitTestCase {
    protected void setUp() {
        super.setUp()
    }

    protected void tearDown() {
        super.tearDown()
    }

    void testEquals(){
      Person p1=new Person(name:"foo" , email:"bar", department:"goo")
      Person p2=new Person(name:"bar" , email:"foo", department:"goo")
      Interest i1=new Interest("foo")
      Interest i2=new Interest("bar")
      Interest i3=new Interest("goo")
      Interest i4=new Interest("foo")
      Edge e1=new Edge(person:p1, interest:i1)
      Edge e2=new Edge(person:p1, interest:i2)
      Edge e3=new Edge(person:p1, interest:i4)
      Edge e4=new Edge(person:p2, interest:i1)
      Edge e5=new Edge(person:p1, interest:i1, relatedInterest:i2)
      Edge e6=new Edge(person:p1, interest:i1, relatedInterest:i3)
      Edge e7=new Edge(person:p1, interest:i1, relatedInterest:i4)
      Edge e8=new Edge(person:p1, interest:i4, relatedInterest:i1)
      Edge e9=new Edge(person:p1, interest:i3, relatedInterest:i1)
      Edge e10=new Edge(person:p1, interest:i4, relatedInterest:i3)
      Edge e11=new Edge(person:p2, interest:i1, relatedInterest:i2)
      Edge e12=new Edge(person:p2, interest:i4, relatedInterest:i2)
      Edge e13=new Edge(interest:i1, relatedInterest:i1)
      Edge e14=new Edge(interest:i1, relatedInterest:i2)
      Edge e15=new Edge(interest:i3, relatedInterest:i2)
      Edge e16=new Edge(interest:i1, relatedInterest:i4)
      Edge e17 = new Edge (interest: i2, relatedInterest: i1)
      assertEquals(e1,e3)
      assertFalse(e1==e4)
      assertFalse(e2==e4)
      assertFalse(e1==e5)
      assertFalse(e1==e11)
      assertFalse(e9==e5)
      assertEquals(e10,e6)
      assertEquals(e13,e16)
      assertEquals(e14, e17)
      assertFalse(e14== e16)
      assertTrue(e11== e12)
      assertFalse(e14== e15)
      assertFalse(e13== e15)
      assertFalse(e5== e11)
      assertEquals(e7, e8)

    }

    void testHashCode(){
      Person p1=new Person(name:"foo" , email:"bar", department:"goo")
      Person p2=new Person(name:"bar" , email:"foo", department:"goo")
      Interest i1=new Interest("foo")
      Interest i2=new Interest("bar")
      Interest i3=new Interest("goo")
      Interest i4=new Interest("foo")
      Edge e1=new Edge(person:p1, interest:i1)
      Edge e2=new Edge(person:p1, interest:i2)
      Edge e3=new Edge(person:p1, interest:i4)
      Edge e4=new Edge(person:p2, interest:i1)
      Edge e5=new Edge(person:p1, interest:i1, relatedInterest:i2)
      Edge e6=new Edge(person:p1, interest:i1, relatedInterest:i3)
      Edge e7=new Edge(person:p1, interest:i1, relatedInterest:i4)
      Edge e8=new Edge(person:p1, interest:i4, relatedInterest:i1)
      Edge e9=new Edge(person:p1, interest:i3, relatedInterest:i1)
      Edge e10=new Edge(person:p1, interest:i4, relatedInterest:i3)
      Edge e11=new Edge(person:p2, interest:i1, relatedInterest:i2)
      Edge e12=new Edge(person:p2, interest:i4, relatedInterest:i2)
      Edge e13=new Edge(interest:i1, relatedInterest:i1)
      Edge e14=new Edge(interest:i1, relatedInterest:i2)
      Edge e15=new Edge(interest:i3, relatedInterest:i2)
      Edge e16=new Edge(interest:i1, relatedInterest:i4)
      Edge e17 = new Edge (interest: i2, relatedInterest: i1)
      assertEquals(e1.hashCode(),e3.hashCode())
      assertFalse(e1.hashCode()==e4.hashCode())
      assertFalse(e2.hashCode()==e4.hashCode())
      assertFalse(e1.hashCode()==e5.hashCode())
      assertFalse(e1.hashCode()==e11.hashCode())
      assertFalse(e9.hashCode()==e5.hashCode())
      assertEquals(e10.hashCode(),e6.hashCode())
      assertEquals(e13.hashCode(),e16.hashCode())
      assertEquals(e14.hashCode(), e17.hashCode())
      assertFalse(e14.hashCode()== e16.hashCode())
      assertTrue(e11.hashCode()== e12.hashCode())
      assertFalse(e14.hashCode()== e15.hashCode())
      assertFalse(e13.hashCode()== e15.hashCode())
      assertFalse(e5.hashCode()== e11.hashCode())
      assertEquals(e7.hashCode(), e8.hashCode())

    }
}
