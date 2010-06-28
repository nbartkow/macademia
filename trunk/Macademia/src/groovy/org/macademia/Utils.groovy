package org.macademia

import grails.util.Environment
import org.apache.log4j.Logger
import org.hibernate.SessionFactory

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
}
