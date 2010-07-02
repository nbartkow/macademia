package org.macademia

import static org.junit.Assert.assertTrue
import grails.plugins.nimble.core.AdminsService
import grails.plugins.nimble.core.Role

class BenchmarkService {

    static transactional = true

    static int NUM_ITERATIONS = 1000 //number of iterations
    static int NUM_PEOPLE = 1000  //number of people in the benchmark database
    static int NUM_INTERESTS = 6034 //number of interests (must be actual number of unique interests)
    static int MAX_PEOPLE = 25 //represents maximum number of people on the outside of a graph
    static int GORM_CLEAN_LINES = 225 //number of lines read before Gorm clean up is called
    Random rand = new Random()

    def similarityService
    def adminsService
    def institutionService
    def personService
    def interestService
    def nimbleService
    def sessionFactory
    def propertyInstanceMap = org.codehaus.groovy.grails.plugins.DomainClassGrailsPlugin.PROPERTY_INSTANCE_MAP

   /* def BenchmarkService() {
        populate(new File("db/benchmark_backup/people.txt"))
        benchmark()
    }  */

    def populate(File directory, boolean necessary) {
        Calendar build = Calendar.getInstance()
        long start = build.getTimeInMillis()
        interestService.initBuildDocuments("db/benchmark_backup/")
        readInstitutions(new File("db/prod/institutions.txt"))
        readPeople(new File(directory.toString()))
        int i = 0
        List<Long> interests= Interest.findAll().collect {it.id}
        //similarityService.roughThreshold=0.03
        //similarityService.refinedThreshold=0.01
        if (necessary) {
            similarityService.buildInterestRelations()
        }
        long end = build.getTimeInMillis()
        long time = (end - start) / 1000
        log.info("It took $time seconds to build the graph.")
    }

    def readInstitutions(File file) {
        log.error("reading intstitutions from $file...")
        file.eachLine {
            String line ->
            String[] tokens = line.trim().split("\t")
            if (tokens.length != 2) {
                log.error("illegal line in ${file.absolutePath}: ${line.trim()}")
                log.error("$tokens.length")
                return
            }
            String name = tokens[0]
            String emailDomain = tokens[1]
            if (institutionService.findByEmailDomain(emailDomain) == null) {
                Institution institution = new Institution(name : name, emailDomain : emailDomain)
                Utils.safeSave(institution)
            }
        }
    }

    def readPeople(File file) {
        nimbleService.init()
        log.error("reading people from $file...")
        int i = 0
        file.eachLine {
            String line ->
            String[] tokens = line.trim().split("\t")
            if (tokens.length != 4) {
                log.error("illegal line in ${file.absolutePath}: ${line.trim()}")
                return
            }
            String name = tokens[0]
            String email = tokens[1]
            String dept = tokens[2]
            String interestStr = tokens[3]
            //String institutionName = tokens[4]

            Institution institution = institutionService.findByEmailDomain(email.split("@")[1])
            if (institution == null) {
                log.error("unknown institution in ${file.absolutePath}: ${line.trim()}")
                return
            }
            Person person = Person.findByEmail(email)
            if (person == null) {
                // Create example User account
                def user = new User()
                user.username = email
                user.pass = 'useR123!'
                user.passConfirm = 'useR123!'
                user.enabled = true

                person = new Person(fullName: name, department: dept, email: email, institution: institution)
                person.owner = user
                user.profile = person
                //Utils.safeSave(user)

                // NOTE: THIS IS AN OLD METHOD THAT USES PERSON.SAVE
                // HENCE, BOTH personService.save() and userService.save() don't work.
                // (we have to rearrange the test)
                //personService.save(person)
            }
            Interest interest = interestService.findByText(interestStr)
            if (interest == null) {
                interest = new Interest(interestStr)
                //Utils.safeSave(interest)
            }
            person.addToInterests(interest)
            personService.save(person)
            
            i++
            if (i % 50 == 0) {
                cleanUpGorm()
                log.info("Cleaning GORM")
            }
        }
        log.error("Read ${Person.count()} people objects")
        log.error("Read ${Interest.count()} interest objects")

        def admins = Role.findByName(AdminsService.ADMIN_ROLE)
        adminsService.add(Person.findById(1).owner)
    }

    def benchmark() {
//        benchRand()
        benchmarkPersonGraph()
        benchmarkInterestGraph()
        similarityService.analyzeTimes()
    }

    def benchmarkPersonGraph() {
        log.info("Beginning person graph benchmark")
        Calendar cal = Calendar.getInstance()
        long begin = cal.getTimeInMillis()
        long randTime = 0
        long interests = 0

        for(int i = 0; i < NUM_ITERATIONS; i++) {
            long randStart = Calendar.getInstance().getTimeInMillis()
            Person person = Person.findById(rand.nextInt(NUM_PEOPLE))
            long randEnd = Calendar.getInstance().getTimeInMillis()
            log.info("RandTime: " + (randEnd - randStart))
            randTime = randTime + randEnd - randStart
            if(person != null){
                long interestSize = person.interests.size()
                log.info("Building graph $i for person $person with " + interestSize + " interests.")
                interests = interests + interestSize
                similarityService.calculatePersonNeighbors(person, MAX_PEOPLE)
            } else{
                i--
            }


        }
        cal = cal.getInstance()
        long end = cal.getTimeInMillis()

        int avg = ((int)(end - begin - randTime))
        avg=avg/NUM_ITERATIONS

        int intAvg = (end - begin - randTime)/interests

        log.info("Began: $begin")
        log.info("Ended: $end")

        log.info("Over $NUM_ITERATIONS iterations, it took an average of $avg milliseconds to create" +
             " a graph with a random person at the center")
        log.info("Over $interests interests, it took an average of $intAvg milliseconds to create a graph" +
            " for that interest")
    }

    def benchmarkInterestGraph() {
        log.info("Beginning interest graph benchmark")
        Calendar cal = Calendar.getInstance()
        long begin = cal.getTimeInMillis()
        long randTime = 0

        for(int i = 0; i < NUM_ITERATIONS; i++) {
            long randStart = Calendar.getInstance().getTimeInMillis()
            Interest interest = Interest.findById(rand.nextInt(NUM_INTERESTS))
            long randEnd = Calendar.getInstance().getTimeInMillis()
            randTime = randTime + randEnd - randStart
            log.info("Building graph $i for interest $interest")
            similarityService.calculateInterestNeighbors(interest, MAX_PEOPLE, 100)
        }
        cal = cal.getInstance()
        long end = cal.getTimeInMillis()

        long avg = (end - begin - randTime)/NUM_ITERATIONS


        log.info("Began: $begin")
        log.info("Ended: $end")

        log.info("Over $NUM_ITERATIONS iterations, it took an average of $avg milliseconds to create" +
             " a graph with a random interest at the center")
    }

    def benchmarkAddInterest() {
        Calendar cal = Calendar.getInstance()
        long total = 0

        for (int i = 0; i < NUM_ITERATIONS; i++) {
            long begin = cal.getTimeInMillis()
            String interest;

            long end = cal.getTimeInMillis()
            total = total + end - begin
        }

        long avg = total/NUM_ITERATIONS

        log.info("Total: $total")

        log.info("Over $NUM_ITERATIONS , it took an average of $avg milliseconds to create" +
             " a graph with a random interest at the center")
    }

    def cleanUpGorm() {
        def session = sessionFactory.currentSession
        session.flush()
        session.clear()
        propertyInstanceMap.get().clear()
    }
}
