import org.macademia.*

Institution.withTransaction {

def inst = InstitutionGroup.findByAbbrev('glca')
if (inst == null) {
    inst = new InstitutionGroup(name : 'Great Lakes College Association', abbrev : 'glca')
    inst.save(failOnError : true)
}
def institutions = [
    'albion.edu' : 'Albion College',
    'allegheny.edu' : 'Allegheny College',
    'antioch.edu' : 'Antioch College',
    'denison.edu' : 'Denison University',
    'depauw.edu' : 'DePauw University',
    'earlham.edu' : 'Earlham College',
    'hope.edu' : 'Hope College',
    'kzoo.edu' : 'Kalamazoo College',
    'kenyon.edu' : 'Kenyon College',
    'oberlin.edu' : 'Oberlin College',
    'owu.edu' : 'Ohio Weslyyan University',
    'wabash.edu' : 'Wabash College',
    'wooster.edu' : 'The College of Wooster',
    'glca.org' : 'The Great Lakes College Association'
]

for (String domain : institutions.keySet()) {
    def college = Institution.findByEmailDomain(domain)
    if (college == null) {
        college = new Institution(name : institutions[domain], emailDomain: domain)
        inst.addToInstitutions(college)
        college.addToInstitutionGroups(inst)
        college.save(failOnError : true)
    }
}

inst.save(failOnError : true)

}
