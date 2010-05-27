import org.springframework.orm.hibernate3.SessionFactoryUtils
import org.springframework.orm.hibernate3.SessionHolder
import org.springframework.transaction.support.TransactionSynchronizationManager
import org.apache.commons.io.FileUtils


/**
 * Deletes existing database.
 */
["devDb.log", "devDb.properties", "devDb.script"].each({
    File f = new File("db/dev/full/${it}")
    if (f.exists()) {
        f.delete()
    }
})

grailsEnv = 'populate'

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

    def populateService = appCtx.getBean('populateService')
    populateService.populate(new File("db/prod"))

    session.connection().commit()
    def statement = session.connection().createStatement();
    statement.execute("SHUTDOWN;")
    session.close();

}

setDefaultTarget(main)