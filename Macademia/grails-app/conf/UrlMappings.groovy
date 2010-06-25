class UrlMappings {
    static mappings = {
      "/$controller/$action?/$id?"{
	      constraints {
			 // apply constraints here
		  }
	  }
      "/" (controller: 'person', action: 'index')
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
