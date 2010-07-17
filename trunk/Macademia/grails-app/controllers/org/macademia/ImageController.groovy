package org.macademia

import grails.converters.JSON

class ImageController {

    def imageService

    def retrieve = {
        // should be key-value pairs that checks to see if
        // images passed as param names exist.
        render [:] as JSON
    }

    def upload = {
        def imageId = imageService.createNewImages(request.getFile('Filedata'), -1)
        def path = imageService.constructPath("", imageId, false)
        render(path)
    }
}
