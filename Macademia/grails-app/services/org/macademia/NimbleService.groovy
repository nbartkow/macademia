package org.macademia

import grails.plugins.nimble.core.Role

class NimbleService extends grails.plugins.nimble.core.NimbleService {

    def grailsApplication

    boolean transactional = true

    public void init() {

        // Perform all base Nimble setup
        def userRole = Role.findByName(UserService.USER_ROLE)
        if (!userRole) {
            userRole = new Role()
            userRole.description = 'Issued to all users'
            userRole.name = UserService.USER_ROLE
            userRole.protect = true
            userRole.save()

            if (userRole.hasErrors()) {
                userRole.errors.each {
                    log.error(it)
                }
                throw new RuntimeException("Unable to create valid users role")
            }
        }

        def adminRole = Role.findByName(AdminsService.ADMIN_ROLE)
        if (!adminRole) {
            adminRole = new Role()
            adminRole.description = 'Assigned to users who are considered to be system wide administrators'
            adminRole.name = AdminsService.ADMIN_ROLE
            adminRole.protect = true
            adminRole.save()

            if (adminRole.hasErrors()) {
                adminRole.errors.each {
                    log.error(it)
                }
                throw new RuntimeException("Unable to create valid administrative role")
            }
        }

        def institutionalAdminRole = Role.findByName(AdminsService.INSTITUTIONAL_ADMIN_ROLE)
        if (!institutionalAdminRole) {
            institutionalAdminRole = new Role()
            institutionalAdminRole.description = 'institutional administrators administrators can edit/delete all profiles of their school'
            institutionalAdminRole.name = AdminsService.INSTITUTIONAL_ADMIN_ROLE
            institutionalAdminRole.protect = false
            institutionalAdminRole.save()

            if (institutionalAdminRole.hasErrors()) {
                institutionalAdminRole.errors.each {
                    log.error(it)
                }
                throw new RuntimeException("Unable to create valid institutional administrative role")
            }
        }
        log.info(institutionalAdminRole.protect)

        // Execute all service init that relies on base Nimble environment
        def services = grailsApplication.getArtefacts("Service")
        for (service in services) {
            if(service.clazz.methods.find{it.name == 'nimbleInit'} != null) {
                def serviceBean = grailsApplication.mainContext.getBean(service.propertyName)
                serviceBean.nimbleInit()
            }
        }

        /**
         * This is some terribly hacky shit to fix a major problem once Nimble
         * was upgraded to use 1.1.1 (possibly groovy not grails specific)
         *
         * TODO: remove this ugly ugly piece of crud when 1.1.2 or 1.2 comes out
         * BUG: http://jira.codehaus.org/browse/GRAILS-4580
         */
        def domains = grailsApplication.getArtefacts("Domain")
        for (domain in domains) {
            domain.clazz.count()
        }
    }
}
