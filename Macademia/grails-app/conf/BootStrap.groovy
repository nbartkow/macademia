import grails.util.Environment
import org.macademia.MacademiaConstants
import org.macademia.BlacklistRelations



class BootStrap {

    def populateService
    def interestRelationFilterService
    def similarityService
    def searchableService

    def init = { servletContext ->
        def dbDir = null

        switch(Environment.current) {
        case Environment.TEST:
            break
        case Environment.DEVELOPMENT:
            BlacklistRelations bl = new BlacklistRelations(MacademiaConstants.PATH_SIM_ADJUSTEMENTS)
            similarityService.analyze(bl)
            break
        default:
            assert(false)
        }
     }

     def destroy = {
     }
} 
