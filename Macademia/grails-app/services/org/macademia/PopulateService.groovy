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
        log.error("reading institutions from $file...")
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
                Institution institution = new Institution(name : name, emailDomain : emailDomain, webUrl : "www.${emailDomain}")
                institutionService.save(institution)
            }
        }

        def ig1 = new InstitutionGroup(name : 'Associated Colleges of the Midwest', abbrev : 'acm')
        Institution.list().each({
            ig1.addToInstitutions(it)
            it.addToInstitutionGroups(ig1)
        })
        Utils.safeSave(ig1)
        def ig2 = new InstitutionGroup(name : 'All institutions', abbrev : 'all')
        Institution.list().each({
            ig2.addToInstitutions(it)
            it.addToInstitutionGroups(ig2)
        })
        Utils.safeSave(ig2)
        def ig3 = new InstitutionGroup(name : 'Test group', abbrev : 'test')
        def mac = Institution.findByEmailDomain('macalester.edu')
        ig3.addToInstitutions(mac)
        mac.addToInstitutionGroups(ig3)
        Utils.safeSave(ig3)

    }

    def readPeople(File file) {
        interestService.initBuildDocuments((file.getParent()).toString()+File.separator)   
        log.error("reading people from $file...")
        file.eachLine {
            String line ->
            importPersonFromLine(file, line)
        }
        log.error("Read ${Person.count()} people objects")
        log.error("Read ${Interest.count()} interest objects")

        Person admin = personService.findByEmail("ssen@macalester.edu")
        admin.role = Person.ADMIN_ROLE
        personService.save(admin)
    }

    def importPersonFromLine(File file, String line) {
        String[] tokens = line.trim().split("\t")
        if (tokens.length != 4 && tokens.length != 5) {
            log.error("illegal line in ${file.absolutePath}: ${line.trim()}")
            log.error("$tokens.length")
            return
        }
        String name = tokens[0]
        String dept = tokens[1]
        String email = tokens[2]
        String emailDomain = email.split("@")[1]
        String interestStr = tokens[3]
        String title = (tokens.length == 5) ? tokens[4] : null

        Institution institution = institutionService.findByEmailDomain(emailDomain)
        if (institution == null) {
            log.error("unknown institution for ${emailDomain} in ${file.absolutePath}: ${line.trim()}")
            return
        }

        Person person = Person.findByEmail(email)
        if (person == null) {
            person = new Person()
            person.email = email
            person.enabled = true
            person.fullName = name
            person.department = dept
            person.title = title
        }
        Interest interest = interestService.findByText(interestStr)
        if (interest == null) {
            interest = new Interest(interestStr)
        }
        person.addToInterests(interest)
        if (person.id) {
            personService.save(person, [institution])
        } else {
            personService.create(person, "useR123!", [institution])
        }

    }


    def importPersonFromLine2(File file, String line) {
        String[] tokens = line.trim().split("\t")
        if (tokens.length != 4 && tokens.length != 5) {
            log.error("illegal line in ${file.absolutePath}: ${line.trim()}")
            log.error("$tokens.length")
            return
        }
        String name = tokens[0]
        String dept = tokens[1]
        String email = tokens[2]

        if (email == null || email == "") {
            log.error("missing email address for ${name}")
            return
        }

        String emailDomain = email.split("@")[1]
        String interestStr = tokens[3]
        String title = (tokens.length == 5) ? tokens[4] : null

        Institution institution = institutionService.findByEmailDomain(emailDomain)
        if (institution == null) {
            log.error("unknown institution for ${emailDomain} in ${file.absolutePath}: ${line.trim()}")
            return
        }

        Person person = Person.findByEmail(email)
        if (person == null) {
            person = new Person()
            person.email = email
            person.enabled = true
            person.fullName = name
            person.department = dept
            person.title = title
        }

        for (String i : interestStr.split(",")) {
            i = i.trim()
            if (i != "") {
                Interest interest = interestService.findByText(i)
                if (interest == null) {
                    interest = new Interest(i)
                }
                person.addToInterests(interest)
            }
        }
        if (person.id) {
            personService.save(person, [institution])
        } else {
            personService.create(person, "useR123!", [institution])
        }
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

}
