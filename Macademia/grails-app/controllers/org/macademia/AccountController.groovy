package org.macademia

import grails.plugins.nimble.InstanceGenerator
import org.apache.commons.validator.EmailValidator
import org.apache.shiro.authc.UsernamePasswordToken
import org.apache.shiro.SecurityUtils
import grails.plugins.nimble.core.AuthController
import org.apache.shiro.authc.IncorrectCredentialsException
import org.apache.shiro.authc.DisabledAccountException
import org.apache.shiro.authc.AuthenticationException

class AccountController extends grails.plugins.nimble.core.AccountController{
    def personService
    def interestService

    def forgottenpasswordcomplete = {
            redirect(uri: '/')
    }


    def changedpasswd = {
            redirect(uri: '/')
    }
    
    def signin = {
        def authToken = new UsernamePasswordToken((String)params.username, (String)params.password)
        if (params.rememberme)
            authToken.rememberMe = true

        log.info("Attempting to authenticate user, $params.username. RememberMe is $authToken.rememberMe")

        try {
            SecurityUtils.subject.login(authToken)
            this.userService.createLoginRecord(request)

            log.info("Authenticated user, $params.username.")
            if (userService.events["login"]) {
                log.info("Executing login callback")
                userService.events["login"](authenticatedUser, targetUri, request)
            }
            render('okay ' + authenticatedUser.profile.id)
        }
        catch (IncorrectCredentialsException e) {
            render('invalid email or password')
        }
        catch (DisabledAccountException e) {
            render('your account has been disabled')
        }
        catch (AuthenticationException e) {
            render('invalid email or password')
        }
    }

    def saveuser = {
        def user = InstanceGenerator.user()
        user.profile = InstanceGenerator.profile()
        def userFields = grailsApplication.config.nimble.fields.enduser.user
        def profileFields = grailsApplication.config.nimble.fields.enduser.profile
        user.properties[userFields] = params
        user.profile.properties[profileFields] = params
        // Handle interest splitting
        if (params.interests){
            interestParse(user)
        }
        // create institution  - replace this eventually
        String institutionDomain = params.email.split("@")[1]
        Institution institution = Institution.findByEmailDomain(institutionDomain)
        if (institution == null){
            institution= new Institution(name:institutionDomain, emailDomain:institutionDomain)
            Utils.safeSave(institution)
        }
        user.profile.institution = institution

        user.username = user.profile.email
        user.profile.owner = user
        user.enabled = grailsApplication.config.nimble.localusers.provision.active
        user.external = false

        user.validate()

        log.debug("Attempting to create new user account identified as $user.username")

        log.info("$user.username is $user.id")

        // Enforce email address for account registrations
        if (user.profile.email == null || user.profile.email.length() == 0)  {
          user.profile.email = 'invalid'
          render('No email provided')
          return
        }
		// Allow host application to do some validation, etc.
		if(userService.events['beforeregister']) {
			userService.events['beforeregister'](user)
		}

        def emailCheckVar = User.findAllByUsername(user.username)
        if (emailCheckVar != null && emailCheckVar.size() > 0) {
          render("Email already in use")
          return
        }
        if (user.hasErrors()) {
            render("Submitted values for new user are invalid")
            return
            user.errors.each {
                log.debug it
            }
            resetNewUser(user)
        }

        def savedUser
        savedUser = userService.createUser(user)
        log.info("saved user ID is $savedUser.id")
        if (savedUser.hasErrors()) {
            log.debug("UserService returned invalid account details when attempting account creation")
            resetNewUser(user)
            //render(view: 'createuser', model: [user: user])
            //return
        } else {
            personService.save(user.profile, Utils.getIpAddress(request))
        }
        savedUser.save(flush : true)    // flush to get the id and foo

		if(userService.events['afterregister']) {
			userService.events['afterregister'](user)
		}

        log.info("Sending account registration confirmation email to $user.profile.email with subject $grailsApplication.config.nimble.messaging.registration.subject")
        if(grailsApplication.config.nimble.messaging.enabled) {
//			sendMail {
//	            to user.profile.email
//				from grailsApplication.config.nimble.messaging.mail.from
//	            subject grailsApplication.config.nimble.messaging.registration.subject
//	            html g.render(template: "/templates/nimble/mail/accountregistration_email", model: [user: savedUser]).toString()
//	        }
		} else {
			log.debug "Messaging disabled would have sent: \n${user.profile.email} \n Message: \n ${g.render(template: "/templates/nimble/mail/accountregistration_email", model: [user: user]).toString()}"
		}

        log.info("Created new account identified as $user.username with internal id $savedUser.id")

        // Login user
        def authToken = new UsernamePasswordToken(user.username, params.pass)
        authToken.rememberMe = true
        log.info("Attempting to authenticate user, ${user.username}.")
        SecurityUtils.subject.login(authToken)
        this.userService.createLoginRecord(request)

        render('okay ' + savedUser.profile.id)
    }

    def createuser2 = {
      def model = super.createuser()
      return render(view: 'modalCreateUser', model: model)
    }

    def login = {
      return render(view: 'login')
    }

    def validemail = {
        EmailValidator emailValidator = EmailValidator.getInstance()
        if (params.val == null || !emailValidator.isValid(params.val)) {
            render message(code: 'nimble.user.email.invalid')
            response.status = 500
        }
		else {
        	def profile = User.findAllByUsername(params?.val)
	        if (profile != null && profile.size() > 0) {
	            render message(code: 'nimble.user.email.invalid')
	            response.status = 500
	        }
			else
	        	render message(code: 'nimble.user.email.valid')
		}
    }


    private def resetNewUser = {user ->
        log.debug("New user creation failed, resetting user input to accepted state")

        if (user.profile?.email.equals('invalid'))
        	user.profile.email = ''

        user.pass = ""
        user.passConfirm = ""
    }

    def modaledituser = {
        User user = null;
        if (!params.id){
            user = User.get(authenticatedUser.id)
        } else {
            user = User.get(params.id)
            //admin check
            if (user.id != authenticatedUser.id && !userService.isAdmin(authenticatedUser, user)) {
                redirect(controller: 'auth', action:'unauthorized')
            }
        }
        if (!user) {
            log.warn("User identified by id '$authenticatedUser.id' was not located")
            flash.type = "error"
            flash.message = message(code: 'nimble.user.nonexistant', args: [params.id])
            redirect(uri: '/')
        }
	    else {
    	    log.debug("Editing user [$user.id]$user.username")
            log.info("Editing user [$user.id]$user.username")
            def fields = grailsApplication.config.nimble.fields.enduserEdit.user
            user.properties[fields] = params
            String allInterests = user.profile.interests.collect({it.text}).join(', ')
            log.info(allInterests)
            return render(view: 'modalCreateUser', model: [user: user, interests : allInterests])
	    }
    }

    def updateuser = {
        def user
        if(params.id){
            user = User.get(params.id)
            if (user.id != authenticatedUser.id && !userService.isAdmin(authenticatedUser, user)){
                redirect(controller: 'auth', action:'unauthorized')
            }
        } else{
            user = User.get(authenticatedUser.id)
        }
        if (!user) {
            render("User identified by id '$params.id' was not located")
        } else {
            def fields = grailsApplication.config.nimble.fields.enduserEdit.user
            user.profile.properties[fields] = params
            //password check
            if (!user.validate()) {
                render("Updated details for user [$user.id]$user.username are invalid")
            } else {
                if (params.interests){
                    interestParse(user)
                }
	            personService.save(user.profile, Utils.getIpAddress(request))
	            log.info("Successfully updated details for user [$user.id]$user.username")
                render('okay ' + user.profile.id)
            }
	    }

    }

    def interestParse = {
        user->
        String allInterests = params.interests
        String[] tokens = allInterests.trim().split(",")
        user.profile.interests = []
        for (i in tokens){
            Interest existingInterest = interestService.findByText(i);
            if (existingInterest != null){
                user.profile.addToInterests(existingInterest)
            } else {
                Interest newInterest = new Interest(i);
                user.profile.addToInterests(newInterest)
            }
        }
    }
}