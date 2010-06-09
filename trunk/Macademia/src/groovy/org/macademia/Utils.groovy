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
        return ['test', 'populateTest'].contains(Environment.getCurrent())
    }
}
