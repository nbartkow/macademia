import org.macademia.MacademiaConstants

class UrlMappings {
    static mappings = {
      "/$group/$controller/$action?/$id?"{
	      constraints {
			 // apply constraints here
		  }
	  }
      "/" {
          controller = 'home'
          group = MacademiaConstants.DEFAULT_GROUP
          action = 'index'
      }

      "/$group" {
          controller = 'person'
//          group = params.group
          action = 'index'
      }
	  "500"(view:'/error')

//    "/viewprofile/$id?" {
//            controller = "account"
//            action = "show"
//        }
//
//    "/register" {
//            controller = "account"
//            action = "createuser"
//        }
	}
}
