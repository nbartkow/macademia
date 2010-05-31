import grails.util.Environment
import org.macademia.MacademiaConstants
import org.macademia.BlacklistRelations



class BootStrap {

    def populateService
    def interestRelationFilterService
    def similarityService

    def init = { servletContext ->
        def dbDir = null

        switch(Environment.current) {
        case Environment.TEST:
            dbDir = "db/test"
            populateService.populate(new File(dbDir))
            break
        case Environment.DEVELOPMENT:
            break
        default:
            assert(false)
        }

        BlacklistRelations bl = new BlacklistRelations(MacademiaConstants.PATH_SIM_ADJUSTEMENTS)
        similarityService.analyze(bl)
     }

     def destroy = {
     }
} 
