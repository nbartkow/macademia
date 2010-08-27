import org.macademia.PersonService
import org.macademia.Person
import org.macademia.MacademiaConstants

class MacademiaFilters {
    PersonService personService

   def filters = {
       loginCheck(controller:'*', action:'*') {
           before = {
               def cookie = request.cookies.find({it.name == MacademiaConstants.COOKIE_NAME})
               if (cookie && cookie.value) {
                   Person person = personService.findByToken(cookie.value)
                   if (person) {
                       request.authenticated = person
                   }
               }
               return true
           }
       }
   }
}