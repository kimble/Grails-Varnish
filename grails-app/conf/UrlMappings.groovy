class UrlMappings {

	static mappings = {
		"/$controller/$action?/$id?"{
			constraints {
				// apply constraints here
			}
		}

        "/test"(view: "/test")
        
		"/"(view:"/index")
		"500"(view:'/error')
	}
}
