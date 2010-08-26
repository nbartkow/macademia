package org.macademia

class PopulateService {
    def interestService
    def similarityService
    def institutionService
    def userService
    def nimbleService
    def personService
    def adminsService

    def sessionFactory

    boolean transactional = true

    def populate(File directory) {
        readInstitutions(new File(directory.toString() + "/institutions.txt"))
        readPeople(new File(directory.toString() + "/people.txt"))
        //downloadInterestDocuments()
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
        String[] directory =  file.getAbsolutePath().split("/")
        interestService.initBuildDocuments("db/"+directory[directory.length-2]+"/")
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
                person = new Person()
                person.email = email
                person.enabled = true
                person.fullName = name
                person.department = dept
                person.institution = institution
            }
            Interest interest = interestService.findByText(interestStr)
            if (interest == null) {
                interest = new Interest(interestStr)
            }
            person.addToInterests(interest)
            if (person.id) {
                personService.save(person)
            } else {
                personService.create(person, "useR123!", null)
            }
        }
        log.error("Read ${Person.count()} people objects")
        log.error("Read ${Interest.count()} interest objects")

        Person admin = personService.findByEmail("ssen@macalester.edu")
        admin.role = Person.ADMIN_ROLE
        personService.save(admin)
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
        similarityService.buildInterestRelations()
    }

    def displaySimilarities(File blacklistFile) {
        similarityService.displaySimilarities(new BlacklistRelations(blacklistFile))
    }

}
