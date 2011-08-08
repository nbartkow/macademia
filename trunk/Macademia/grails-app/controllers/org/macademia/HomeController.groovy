package org.macademia

import grails.plugin.springcache.annotations.Cacheable

class HomeController {
    public static int NUM_PEOPLE = 28
    public static int NUM_RANDOM_LISTS = 20

    def personService
    def springcacheService
    def random = new Random()

    def index = {
        TimingAnalysis ta = new TimingAnalysis()
        ta.startTime()
        def igCounts = getInstitutionGroupCounts()
        ta.recordTime("count ig memberships")
        def igs = igCounts.keySet() as ArrayList
        igs.sort({igCounts[it]})
        igs = igs.reverse()
        def r = random.nextInt(NUM_RANDOM_LISTS)
        def people = getRandomPeopleWithImages(NUM_PEOPLE, r)
        ta.recordTime("find random images")
//        ta.analyze()
        [people : people, igs : igs]
    }

    def getInstitutionGroupCounts() {
        return springcacheService.doWithCache(
                'homeCache',
                'institutionGroupCounts',
                {personService.getInstitutionGroupCounts()}
        )
    }

    def getRandomPeopleWithImages(int numPeople, int randomNum) {
        return springcacheService.doWithCache(
                'homeCache',
                'getRandomPeople' + randomNum,
                {personService.findRandomPeopleWithImage(numPeople)}
        )
    }
}
