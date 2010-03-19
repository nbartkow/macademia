package org.macademia

class PopulateService {
    def interestService
    def sessionFactory
    def propertyInstanceMap = org.codehaus.groovy.grails.plugins.DomainClassGrailsPlugin.PROPERTY_INSTANCE_MAP


    boolean transactional = true

    def populate(File directory) {
        readPeople(new File(directory.toString() + "/people.txt"))
        analyzeInterests();
//        readSimilarities(new File(directory.toString() + "/sims.txt"))
    }

    def readPeople(File file) {
        log.error("reading people from $file...")
        file.eachLine {
            String line ->
            String[] tokens = line.trim().split("\t")
            if (tokens.length != 4) {
                log.error("illegal line in ${file.absolutePath}: ${line.trim()}")
                return
            }
            String name = tokens[0]
            String dept = tokens[1]
            String email = tokens[2]
            String interestStr = tokens[3]

            Person person = Person.findByEmail(email)
            if (person == null) {
                person = new Person(name: name, department: dept, email: email)
                person.save()
            }
            Interest interest = interestService.findByText(interestStr)
            if (interest == null) {
                interest = new Interest(interestStr)
                interest.save()
            }
            person.addToInterests(interest)
        }
        log.error("Read ${Person.count()} people objects")
        log.error("Read ${Interest.count()} interest objects")
    }

    /** TODO: move this to some other service */
    def analyzeInterests() {
        Wikipedia wikipedia = new Wikipedia()
        Google google = new Google()
        Interest.findAll().each({
            println("doing interest ${it}")
            double weight = 1.0
            for (String url : google.query(it.text, 5)) {
                weight *= 0.5;
                String url2 = Wikipedia.getCanonicalUrl(url)
                if (!url2) {
                    println("canonicalizing of $url failed")
                    continue
                }
                Document d = Document.findByUrl(url2)
                if (d == null) {
                    d = wikipedia.getDocumentByUrl(url2)
                    if (!d) {
                        println("retrieval of $url (canonical form is $url2) failed")
                        continue
                    } else if (!d.save(flush : true)) {
                        println("saving failed!")
                        continue
                    }
                }
                InterestDocument id = new InterestDocument(document : d, weight : weight)
                it.addToDocuments(id)
                id.save(flush : true)
            }
            it.save(flush : true)
        })
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
