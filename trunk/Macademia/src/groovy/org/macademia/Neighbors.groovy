package org.macademia
/**
 * Created by IntelliJ IDEA.
 * User: shilad
 * Date: Aug 2, 2009
 * Time: 12:31:41 AM
 * To change this template use File | Settings | File Templates.
 */

public class Neighbors {
    Person first
    Person second
    List<InterestRelation> sharedInterests = new ArrayList<InterestRelation>()

    def sortInterests = {
        this.sharedInterests = sharedInterests.sort({it.similarity}).reverse()
    }


    public String toString() {
        StringBuffer b = new StringBuffer()
        b.append("<$first, $second")
        for (InterestRelation ir: sharedInterests) {
            b.append(", $ir")
        }
        b.append(">")
        return b.toString()
    }
}