package org.macademia

import grails.util.Environment
import org.apache.log4j.Logger

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
}
