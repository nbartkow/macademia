package org.macademia


import grails.util.Environment
import org.apache.log4j.Logger
import org.hibernate.SessionFactory
import java.security.MessageDigest


/**
 * Utility methods
 */
class Utils {
    private final static Logger log = Logger.getLogger(Utils.class)
    
    public static void safeSave(Object o) {
        safeSave(o, false)
    }
    public static void safeSave(Object o, boolean flush) {
        if( !o.save(flush : flush) ) {
            log.error("save of " + o + " failed")
            o.errors.each {
                log.error(it)
            }
            throw new RuntimeException("save of " + o + " failed")
        }
    }
    
    static def propertyInstanceMap = org.codehaus.groovy.grails.plugins.DomainClassGrailsPlugin.PROPERTY_INSTANCE_MAP
    public static void cleanUpGorm(Object sessionFactory) {
        def session = sessionFactory.currentSession
        session.flush()
        session.clear()
        propertyInstanceMap.get().clear()
    }

    public static String generateMD5(InputStream is) {
       MessageDigest digest = MessageDigest.getInstance("MD5")
       byte[] buffer = new byte[8192]
       int read = 0
       while( (read = is.read(buffer)) > 0) {
           digest.update(buffer, 0, read);
       }
       byte[] md5sum = digest.digest()
       BigInteger bigInt = new BigInteger(1, md5sum)
       return bigInt.toString(16).padLeft(32, "0")
    }
}
