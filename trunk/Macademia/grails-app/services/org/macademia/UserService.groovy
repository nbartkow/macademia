package org.macademia

import grails.plugins.nimble.core.UserBase
import grails.plugins.nimble.core.Role
import grails.plugins.nimble.core.Permission
import grails.plugins.nimble.auth.CorePermissions
import org.apache.shiro.crypto.hash.Sha256Hash
import grails.plugins.nimble.core.Group

class UserService extends grails.plugins.nimble.core.UserService{

    def roleService
    def groupService

    def isAdmin(User authenticatedUser){
        boolean admin = false
        authenticatedUser.roles.each { role ->
            if(role.name == AdminsService.ADMIN_ROLE){
                admin = true
            }
        }
        return admin
    }

    def isAdmin(User authenticatedUser, User user){
        boolean admin = false
        authenticatedUser.roles.each { role ->
            if(role.name == AdminsService.ADMIN_ROLE){
                admin = true
            } else if(role.name == AdminsService.INSTITUTIONAL_ADMIN_ROLE){
                if(authenticatedUser.profile.institution == user.profile.institution){
                    admin = true
                }
            }
        }
        return admin
    }

    def deleteUser(UserBase user) {

        // Remove roles from user
        def roles = []
        roles.addAll(user.roles)
        roles.each{
            roleService.deleteMember(user, it)
        }

        // Remove groups from user
        def groups = []
        groups.addAll(user.groups)
        groups.each{
            groupService.deleteMember(user, it)
        }

        //remove individual interest
//        for (interest in user.profile.interests){
//            if (interest.people.size() == 1){
//                if (InterestRelation.findByFirst(interest)){
//                    log.info("FOUNDS IT")
//                }
//                InterestRelation.findByFirst(interest).delete()
//                InterestRelation.findBySecond(interest).delete()
//                interest.delete()
//                log.info("Deleted interest " + interest.text)
//            }
//        }

        user.delete()
        log.info("Deleted user [$user.id]$user.username")
    }

    static transactional = true

    def serviceMethod() {

    }

    /**
     * Applies password processing rules to determine if the user
     * pass and passConfirm values are valid. If not error objects are added to the user
     * object. If the pass is valid it is encrypted and set as the value of user.passwd and
     * added to the password history
     */
    public def validatePass(UserBase user) {
		return validatePass(user, false)
	}
    public def validatePass(UserBase user, boolean checkOnly) {
        log.debug("Validating user entered password")

        if (user.pass == null || user.pass.length() < grailsApplication.config.nimble.passwords.minlength) {
            log.debug("Password to short")
            user.errors.rejectValue('pass', 'nimble.user.password.required')
            return false
        }

        if (user.passConfirm == null || user.passConfirm.length() < grailsApplication.config.nimble.passwords.minlength) {
            log.debug("Confirmation password to short")

            user.errors.rejectValue('passConfirm', 'nimble.user.passconfirm.required')
            return false
        }

        if (!user.pass.equals(user.passConfirm)) {
            log.debug("Password does not match confirmation")
            user.errors.rejectValue('pass', 'nimble.user.password.nomatch')
            return false
        }

        if (grailsApplication.config.nimble.passwords.mustcontain.symbols && !(user.pass =~ /^.*\W.*$/)) {
            if (grailsApplication.config.nimble.passwords.mustcontain.numbers && !(user.pass =~ /^.*[0-9].*$/)) {
                log.debug("Password does not contain numbers or symbols")
                user.errors.rejectValue('pass', 'nimble.user.password.no.numbersOrSymbols')
                return false
            }
        }

        def pwEnc = new Sha256Hash(user.pass)
        def crypt = pwEnc.toHex()

        if (user.passwdHistory != null && user.passwdHistory.contains(crypt)) {
            log.debug("Password was previously utilized")
            user.errors.rejectValue('pass', 'nimble.user.password.duplicate')
            return false
        }

        if (!user.hasErrors() && !checkOnly) {
            user.passwordHash = crypt
            user.addToPasswdHistory(crypt)
        }

        return true
    }


    def createUser(User user) {
        user.username = user.profile.email
        if (user==null){
            log.error("USER IS NULL! WARNING!")
        }
        user.validate()
        log.info "user Password: '${user.pass}' with hash '${user.passwordHash}', and external is ${user.external}"

        if (!user.external) {
            if (validatePass(user)){
                generateValidationHash(user)
            }
            else
            return user
        }

        if (!user.hasErrors()) {

            // Add user to group (named after his institution)
            def existingGroup = Group.findByName(user.profile.institution.name)
            if (!existingGroup){
                Group newGroup = groupService.createGroup(user.profile.institution.name, user.profile.institution.name, false)
                groupService.addMember(user, newGroup)
            } else {
                groupService.addMember(user, existingGroup)
            }

            // Add default role for users
            def defaultRole = Role.findByName(UserService.USER_ROLE)

            if(!defaultRole) {
                log.error("Unable to locate default user role, aborting user creation")
                throw new RuntimeException("Unable to locate default user role, aborting user creation")
            }


            user.addToRoles(defaultRole)

            def savedUser = user.save()
            if (savedUser) {
                defaultRole.addToUsers(savedUser)
                defaultRole.save()

                if (defaultRole.hasErrors()) {
                    log.error("Unable to assign default role to new user [$savedUser.id]$savedUser.username")
                    defaultRole.errors.each {
                        log.error(it)
                    }

                    throw new RuntimeException("Unable to assign default role to new user [$savedUser.id]$savedUser.username")
                }

                // Add default permission set
                // Allow personal profile edit
                Permission profileEdit = new Permission(managed:true, type: Permission.defaultPerm, target:"${CorePermissions.editPermission}:$savedUser.id")
                permissionService.createPermission(profileEdit, savedUser)

                savedUser.save()
                if (savedUser.hasErrors()) {
                    log.error("Unable to assign default permissions to new user [$savedUser.id]$savedUser.username")
                    savedUser.errors.each {
                        log.error(it)
                    }

                    throw new RuntimeException("Unable to assign default permissions to new user [$savedUser.id]$savedUser.username")
                }

                log.info("Successfully created user [$user.id]$user.username")
                return savedUser

            }
        }

        // Validation or save errors ocured
        log.debug("Submitted details for new user account are invalid")
        user.errors.each {
            log.debug it
        }
        return user
    }
}
