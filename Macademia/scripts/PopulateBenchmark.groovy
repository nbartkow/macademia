import org.springframework.orm.hibernate3.SessionFactoryUtils
import org.springframework.orm.hibernate3.SessionHolder
import org.springframework.transaction.support.TransactionSynchronizationManager
import org.apache.commons.io.FileUtils


/**
 * Deletes existing database.
 */

["db.log", "db.properties", "db.script"].each({
    File f = new File("db/benchmark_backup/" + it)
    if (f.exists()) {
        f.delete()
    }
})

grailsEnv = 'populateBenchmark'

includeTargets << grailsScript("_GrailsBootstrap")

target ('main': "Load the Grails interactive shell") {
    depends(parseArguments, compile, configureProxy, packageApp, classpath)

    loadApp()
    configureApp()
    bootstrap()

    // prepare hibernate
    def sessionFactory = appCtx.getBean("sessionFactory")
    def session = SessionFactoryUtils.getSession(sessionFactory, true)
    TransactionSynchronizationManager.bindResource(sessionFactory, new SessionHolder(session))
    def tx = session.beginTransaction();

    boolean necessary = false
    def databaseService = appCtx.getBean('databaseService')
    if (necessary) {
        def similarityService = appCtx.getBean('similarityService')
        similarityService.relationsBuilt = false
        //databaseService.dropCurrentDB()
    }

   // def interestService = appCtx.getBean('interestService')
   // def similarityService = appCtx.getBean('similarityService')
    def benchmarkService = appCtx.getBean('benchmarkService')
    benchmarkService.populate(new File("db/benchmark_backup/testPeople.txt"), false)

    session.connection().commit()
    session.close();

}

setDefaultTarget(main)
