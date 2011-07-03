package org.macademia

/**
 * To access actions in the administrator controller you must
 * 1) Execute the resetKey action.
 * 2) Go to the logs and read out the new access key
 * 3) Execute a new action with the newly generated key
 *
 * It is crucial that all actions call checkAuth() first.
 */
class AdministratorController {
    private static int AUTH_KEY = new Random().nextInt()
    def personService

    def resetKey = {
        AUTH_KEY = new Random().nextInt()
        println("AUTH_KEY is " + AUTH_KEY)
        render("key reset... new key should appear in log files")
    }

    def ping = {
        render("pong")
    }

    def invite = {
        checkAuth()
        String template = params.template
        String email = params.email
        if (!template || !email) {
            throw new IllegalArgumentException("missing template or email argument")
        }
        Person p = personService.findByEmail(params.email)
        if (p) {
            String password = p.resetPasswd()
            String body = g.render(
                        template: "${template}",
                        model : [person : p, password : password]
                )
            sendMail {
                to "${email}"
                subject "Come see your Macademia 2.0 profile!"
                html "${body}"
            }
            render(body)
        } else {
            render("unknown person: ${email}")
        }

    }

    private def checkAuth() {
        if ((params.key as int) != AUTH_KEY) {
            throw new IllegalArgumentException("invalid authorization key")
        }
    }
}
