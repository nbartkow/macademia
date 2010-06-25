package org.macademia
import grails.plugins.nimble.core.UserBase
import grails.plugins.nimble.InstanceGenerator
import grails.plugins.nimble.core.Role

class UserController extends grails.plugins.nimble.core.UserController{

  def delete = {
    def user = UserBase.get(params.id)
    if (!user) {
      log.warn("User identified by id '$params.user_base_id' was not located")
      flash.type = "error"
      flash.message = message(code: 'nimble.user.nonexistant', args: [params.id])
      redirect action: list
    }
    else {
            userService.deleteUser(user)
            log.info("Deleted user $user.username")
            flash.type = "success"
            flash.message = message(code: 'nimble.user.delete.success', args: [user.username])
            redirect action: list
    }
  }

  def create = {
    def user = InstanceGenerator.user()
    user.profile = InstanceGenerator.profile()
    log.debug("Starting user creation process")
    [user: user]
  }


  def save = {
    def user = InstanceGenerator.user()
    user.profile = InstanceGenerator.profile()
    def userFields = grailsApplication.config.nimble.fields.enduser.user
    def profileFields = grailsApplication.config.nimble.fields.enduser.profile
    user.properties[userFields] = params
    user.profile.properties[profileFields] = params
    user.username = user.profile.email
    user.enabled = false
    user.external = false

    def savedUser = userService.createUser(user)
    if (savedUser.hasErrors()) {
      log.info("Failed to save new user")
      render view: 'create', model: [roleList: Role.list(), user: user]
    }
	else {
    	log.info("Successfully created new user [$savedUser.id]$savedUser.username")
	    redirect action: show, id: user.id
    }
  }
}