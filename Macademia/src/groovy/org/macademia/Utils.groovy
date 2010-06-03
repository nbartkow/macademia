package org.macademia

import grails.util.Environment

/**
 * Utility methods
 */
class Utils {

    /**
     * Determine if the current environment uses some variant of the test database
     * @return
     */

    public static boolean isTestDb() {
        System.err.println("RESULT IS " + Environment.getCurrent() + ", " + ['test', 'populateTest'].contains(Environment.getCurrent()))
        return ['test', 'populateTest'].contains(Environment.getCurrent())
    }
}
