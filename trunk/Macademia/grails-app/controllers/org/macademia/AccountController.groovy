package org.macademia

import javax.servlet.http.Cookie
import org.codehaus.groovy.grails.commons.ConfigurationHolder

class AccountController {
    def personService
    def interestService


    def forgottenpassword = {
        render(view : 'forgottenpassword', model : [])
    }

    def forgottenpasswordcomplete = {
        String email = params.email
        Person person = personService.findByEmail(email)
        if (!person) {
            render(view : 'forgottenpassword', model : [error : "No account is associated with email address ${email}"])
            return
        }
        String newPassword = person.resetPasswd()
        personService.save(person)

        sendMail {
            to email
            subject "Your Macademia password has been reset"                        
            body """Dear ${params.email},

We have reset your Macademia password to \"${newPassword}\".

You can login to Macademia with this new password at ${ConfigurationHolder.config.grails.serverURL as String}.  Please change your password as soon as possible.

Thank you! 

The Macademia Team

(Do not reply to this e-mail.)
"""
        }

        render(view : 'message',
               model : [
                   title : "Password recovered.",
                   message : "We have emailed a new password to you at ${email}."
               ])
    }


    def changepassword = {
        if (!request.person) {
            render("not logged in.")
            return
        }
        render(view : 'changepassword', model : [:])
    }
    
    def changepasswordcomplete = {
        String error = ''
        if (!params.currentPassword) {
            error = 'Please enter your current password.'
        } else if (!params.password) {
            error = 'Please enter a new password.'
        } else if (!params.password) {
            error = 'Please confirm your new password.'
        } else if (!request.person.checkPasswd(params.currentPassword)) {
            error = 'Your current password is incorrect.'
        } else if (params.password.length() < 6) {
            error = 'Your password must be at least 6 characters.'
        } else if (params.password != params.passwordConfirm) {
            error = 'Your new passwords do not match.'
        }
        if (error) {
            render(view : 'changepassword', model : [error : error])
            return
        }
        request.person.updatePasswd(params.password)
        personService.save(request.person)
        render(view : 'message',
               model : [
                   title : "Password changed.",
                   message : "Your password has been successfully changed."
               ])
    }
    
    def signin = {
        Person person = personService.findByEmail(params.email)
        if (person == null) {
            render('unknown email address')
            return
        } else if (!person.checkPasswd(params.password)) {
            render('invalid passwd')
            return
        } else if (!person.enabled) {
            render('your account has been disabled')
            return
        }
        setAuthCookie(person)
        render('okay ' + person.id)
    }

    private void setAuthCookie(Person person) {
        def cookie = new Cookie(MacademiaConstants.COOKIE_NAME, person.token)
        cookie.path = "/"
        cookie.setMaxAge(MacademiaConstants.MAX_COOKIE_AGE)
        response.addCookie(cookie)
        request.person = person
    }

    def logout = {
        session.invalidate()
        def cookie = new Cookie(MacademiaConstants.COOKIE_NAME, "")
        cookie.path = "/"
        response.addCookie(cookie)
        redirect(url: request.getHeader("referer"))
    }

    /**
     * Creates a brand new user.
     */
    def saveuser = {
        Person person = new Person()
        person.properties[grailsApplication.config.macademia.creatableFields] = params
        // Handle interest splitting
        if (params.interests){
            interestParse(person)
        }
        // create institution  - replace this eventually
        String institutionDomain = params.email.split("@")[1]
        Institution institution = Institution.findByEmailDomain(institutionDomain)
        if (institution == null){
            institution= new Institution(name:institutionDomain, emailDomain:institutionDomain)
            Utils.safeSave(institution)
        }
        person.institution = institution
        person.enabled = true

        log.debug("Attempting to create new user account identified as $person.email")

        // Enforce email address for account registrations
        if (person.email == null || person.email.length() == 0)  {
          person.email = 'invalid'
          render('No email provided')
          return
        }
        if (params.pass != params.passConfirm) {
          render("Passwords do not match")
          return
        }
        if (personService.findByEmail(person.email) != null) {
          render("Email already in use")
          return
        }
        try {
            personService.create(person, params.pass, Utils.getIpAddress(request))
        } catch(Exception e) {
            log.error("creation of " + person + " failed")
            log.error(e)
            render("Internal error: " + e.getMessage())
            return
        }

        person.save(flush : true)    // flush to get the id
        log.info("Created new account identified as $person.email with internal id $person.id")

        // Set the login cookie.
        setAuthCookie(person)

        render('okay ' + person.id)
    }

    def createuser2 = {
      return render(view: 'modalCreateUser', model: [user : new Person(), interests : ""])
    }

    def login = {
      return render(view: 'login')
    }


    private def resetNewUser = {user ->
        log.debug("New user creation failed, resetting user input to accepted state")

        if (user.profile?.email.equals('invalid'))
        	user.profile.email = ''

        user.pass = ""
        user.passConfirm = ""
    }

    def modaledituser = {
        Person person = null;
        if (!request.person) {
            throw new IllegalStateException("no user present!")
        } else if (!params.id){
            person = request.person
        } else {
            person = Person.get(params.id)
            //admin check
            if (!request.person.canEdit(person)) {
                throw new IllegalArgumentException("not authorized")
            }
        }
        if (!person) {
            log.warn("User identified by id '$params.id' was not located")
            flash.type = "error"
            flash.message = message(code: 'nimble.user.nonexistant', args: [params.id])
            redirect(uri: '/')
        }
	    else {
            log.info("Editing user [$person.id] $person.email")
            person.properties[grailsApplication.config.macademia.editableFields] = params
            String allInterests = person.interests.collect({it.text}).join(', ')
            return render(view: 'modalCreateUser', model: [user: person, interests : allInterests])
	    }
    }

    def updateuser = {
        def person
        if (params.id){
            person = Person.get(params.id)
            if (!request.person.canEdit(person)) {
                throw new IllegalArgumentException("not authorized")
            }
        } else{
            person = request.person
        }
        if (!person) {
            render("User identified by id '$params.id' was not located")
        } else {
            person.properties[grailsApplication.config.macademia.editableFields] = params
            //password check
            if (!person.validate()) {
                render("Updated details for user [$person.id] $person.email are invalid")
            } else {
                if (params.interests){
                    interestParse(person)
                }
	            personService.save(person, Utils.getIpAddress(request))
	            log.info("Successfully updated details for user [$person.id] $person.email")
                render('okay ' + person.id)
            }
	    }

    }

    private void interestParse(Person person) {
    String allInterests = params.interests
        String[] tokens = allInterests.trim().split(",")
        person.interests = []
        for (i in tokens){
            Interest existingInterest = interestService.findByText(i);
            if (existingInterest != null){
                person.addToInterests(existingInterest)
            } else {
                Interest newInterest = new Interest(i);
                person.addToInterests(newInterest)
            }
        }
    }
}
