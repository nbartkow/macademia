package org.macademia

import grails.plugins.nimble.InstanceGenerator
import grails.util.Environment
import grails.plugins.nimble.core.AdminsService
import grails.plugins.nimble.core.Role

class PopulateService {
    def interestService
    def similarityService
    def institutionService
    def userService
    def nimbleService
    def personService
    def adminsService
    def databaseService

    def sessionFactory
    def propertyInstanceMap = org.codehaus.groovy.grails.plugins.DomainClassGrailsPlugin.PROPERTY_INSTANCE_MAP


    boolean transactional = true

    def populate(File directory) {
        readInstitutions(new File(directory.toString() + "/institutions.txt"))
        readPeople(new File(directory.toString() + "/people.txt"))
        downloadInterestDocuments()
        buildInterestRelations()
    }

    def readInstitutions(File file) {
        log.error("reading people from $file...")
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
        file.eachLine {
            String line ->
            String[] tokens = line.trim().split("\t")
            if (tokens.length != 4) {
                log.error("illegal line in ${file.absolutePath}: ${line.trim()}")
                log.error("$tokens.length")
                return
            }
            String name = tokens[0]
            String dept = tokens[1]
            String email = tokens[2]
            String emailDomain = email.split("@")[1]
            String interestStr = tokens[3]

            Institution institution = institutionService.findByEmailDomain(emailDomain)
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
        }
        log.error("Read ${Person.count()} people objects")
        log.error("Read ${Interest.count()} interest objects")

        def admins = Role.findByName(AdminsService.ADMIN_ROLE)
        adminsService.add(personService.findByEmail("ssen@macalester.edu").owner)

    }

    /** TODO: move this to some other service */
  /**
   * second method in graphing algorithm
   * for each interest, calls buildDocuments, which uses wikipedia and
   * google assigns documents to the interests (for the purposes of calculating
   * interest similarity)
   * @return
   */
   def downloadInterestDocuments(String directory) {
       interestService.initBuildDocuments(directory)
        Interest.findAll().each({
            interestService.buildDocuments(it) //wrap in try/catch each of the people individually
        })
    }

    def buildInterestRelations() {
        similarityService.buildAllInterestRelations()
    }

    def displaySimilarities(File blacklistFile) {
        similarityService.displaySimilarities(new BlacklistRelations(blacklistFile))
    }

    def readSimilarities(File file) {
        log.error("reading similarities from $file...")
        int i = 0
        def added = new HashSet()
        file.eachLine {
            String line ->
//            if (i++ > 500) {
//                return
//            }
            if (i++ % 1000 == 0) {
                log.error("processing similarity " + i);
                cleanUpGorm()

            }
            String[] tokens = line.trim().split("\t")
            if (tokens.length != 3) {
                log.error("illegal line in ${file.absolutePath}: ${line.trim()}")
                return
            }
            String interestStr1 = tokens[0]
            String interestStr2 = tokens[1]
            double sim = (tokens[2] as double)
            Interest i1 = interestService.findByText(interestStr1)
            Interest i2 = interestService.findByText(interestStr2)
            if (i1 == null) {
                log.error("unknown interest $interestStr1 in $line")
            }
            if (i2 == null) {
                log.error("unknown interest $interestStr2 in $line")
            }
            InterestRelation r1 = new InterestRelation(first: i1, second: i2, similarity: sim)
            InterestRelation r2 = new InterestRelation(first: i2, second: i1, similarity: sim)
            [r1, r2].each {
                if (!added.contains(it)) {
                    it.save()
                    added.add(it)
                }
            }
        }
        log.error("Read ${InterestRelation.count()} interest relationships (including mirror images)")
    }

    def cleanUpGorm() {
        def session = sessionFactory.currentSession
        session.flush()
        session.clear()
        propertyInstanceMap.get().clear()
    }

}
