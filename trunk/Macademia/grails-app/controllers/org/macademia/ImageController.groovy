package org.macademia

import grails.converters.JSON

class ImageController {

    def ImageService

    def retrieve = {
        // should be key-value pairs that checks to see if
        // images passed as param names exist.
        render [:] as JSON
    }

    def upload = {
        def f = request.getFile('Filedata')
        def images = imageService.saveImages(f.inputStream, [Person.LARGE, Person.MEDIUM, Person.SMALL])
        render ([
                'large' : images[Person.LARGE].id,
                'medium' : images[Person.MEDIUM].id,
                'small' : images[Person.SMALL].id,
                'path' : images[Person.LARGE].retrieveWebPath()
        ] as JSON)
    }
}
