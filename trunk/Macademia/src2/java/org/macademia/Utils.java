package org.macademia;


import java.util.Collection;
import java.util.Iterator;

public class Utils {
	public static final String WIKIPEDIA = "./dat/en-wikipedia-20080101";
	public static final String BNC = "./dat/en-BNC-20080721";
	public static final String PUBMED = "./dat/en-PubMedOA-20070501";
	
    public static String join(Collection s, String delimiter) {
        StringBuffer buffer = new StringBuffer();
        Iterator iter = s.iterator();
        while (iter.hasNext()) {
            buffer.append(iter.next());
            if (iter.hasNext()) {
                buffer.append(delimiter);
            }
        }
        return buffer.toString();
    }

}
