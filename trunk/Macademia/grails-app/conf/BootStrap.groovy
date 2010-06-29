import grails.util.Environment



class BootStrap {

    def populateService
    def interestRelationFilterService
    def similarityService
    def autocompleteService

    def init = { servletContext ->
        def dbDir = null

        switch(Environment.current) {
        case Environment.TEST:
            break
        case Environment.DEVELOPMENT:
            autocompleteService.init()
            break
        default:
            assert(false)
        }
     }

     def destroy = {
     }
}
