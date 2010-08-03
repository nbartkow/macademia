package org.macademia

import grails.plugins.nimble.InstanceGenerator
import org.apache.commons.validator.EmailValidator

class AccountController extends grails.plugins.nimble.core.AccountController{
    def personService
    def interestService
  

    def saveuser = {
        if (!grailsApplication.config.nimble.localusers.registration.enabled) {
            log.warn("Account registration is not enabled for local users, skipping request")
            response.sendError(404)
            return
        }

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

        if (!userService.passCheck(user)) {
            render("You need numbers in your password");
            return
        } else {
          log.info("?!?!?!")
        }

        if (user.hasErrors()) {
            render("Submitted values for new user are invalid")
            return
            user.errors.each {
                log.debug it
            }

            resetNewUser(user)
            //render(view: 'modalcreateuser', model: [user: user])     // make this call javascript function to stop redirection
            //return
        }

        def savedUser
        def human = recaptchaService.verifyAnswer(session, request.getRemoteAddr(), params)

        if (human) {

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
        }
        else {
            log.debug("Captcha entry was invalid for user account creation")
            resetNewUser(user)
            user.errors.reject('nimble.invalid.captcha')
            //render(view: 'createuser', model: [user: user])      // similiarly
            //return
        }

		if(userService.events['afterregister']) {
			userService.events['afterregister'](user)
		}

        log.info("Sending account registration confirmation email to $user.profile.email with subject $grailsApplication.config.nimble.messaging.registration.subject")
        if(grailsApplication.config.nimble.messaging.enabled) {
			sendMail {
	            to user.profile.email
				from grailsApplication.config.nimble.messaging.mail.from
	            subject grailsApplication.config.nimble.messaging.registration.subject
	            html g.render(template: "/templates/nimble/mail/accountregistration_email", model: [user: savedUser]).toString()
	        }
		}
		else {
			log.debug "Messaging disabled would have sent: \n${user.profile.email} \n Message: \n ${g.render(template: "/templates/nimble/mail/accountregistration_email", model: [user: user]).toString()}"
		}

        log.info("Created new account identified as $user.username with internal id $savedUser.id")

        render('okay ' + savedUser.id)
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
            if (user.id != authenticatedUser.id && !userService.isAdmin(authenticatedUser, user)){
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
            log.warn("User identified by id '$params.id' was not located")
            flash.type = "error"
            flash.message = message(code: 'nimble.user.nonexistant', args: [params.id])
            redirect action: editprofile, id: params.id
        } else {
            def fields = grailsApplication.config.nimble.fields.enduserEdit.user
            println('fields are ' + fields);
            println('params are ' + params)
            user.profile.properties[fields] = params
            println('email is ' + user.profile.email)
            //password check
            if (!user.validate()) {
                log.debug("Updated details for user [$user.id]$user.username are invalid")
                render view: 'edit', model: [user: user]
            } else {
                if (params.interests){
                    interestParse(user)
                }
	            personService.save(user.profile, Utils.getIpAddress(request))
	            log.info("Successfully updated details for user [$user.id]$user.username")
                render('okay')
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
