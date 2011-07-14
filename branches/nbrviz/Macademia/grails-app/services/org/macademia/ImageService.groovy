package org.macademia

import javax.media.jai.RenderedOp
import javax.media.jai.JAI
import java.awt.RenderingHints
import org.codehaus.groovy.grails.commons.ConfigurationHolder

class ImageService {

    static transactional = true

    // The number of times an ID is split into subdirectories.
    public static final int NUM_ID_DIRS = 2

    // The number of digits in each subdirectory generated by splitting the ID.
    public static final int ID_INTERVAL = 2

    // Sizes for small and large thumbnails
    public static final int SMALL_SIZE = 30;
    public static final int LARGE_SIZE = 60;

    // Internal image file format
    public static final String IMAGE_TYPE = "png"

    // local file paths
    public static final String DB_IMAGES_PATH =  ConfigurationHolder.config.macademia.profileImagePath
    public static final String LARGE_IMAGES_PATH = DB_IMAGES_PATH + "/large"
    public static final String SMALL_IMAGES_PATH = DB_IMAGES_PATH + "/small"
    public static final String TEMP_IMAGES_PATH = DB_IMAGES_PATH + "/tmp"
    public static final String ORIG_IMAGES_PATH = DB_IMAGES_PATH + "/orig"
    private static final String TEMP_IMAGES_PREFIX = "img"

    // web url paths
    // these aren't used any more.
    public static final String BASE_URL = "/Macademia/images/db"
    public static final String SMALL_URL = BASE_URL + "/small"
    public static final String LARGE_URL = BASE_URL + "/large"
    public static final Random random = new Random()

    /**
     * Creates small and large images whose paths can be obtained by calling smallUrl(), or largeUrl().
     * @param file MultipartFile as retrieved by calling request.getFile("foo")
     * @param id Desired id of the image, or null.
     * @return
     */
    def createNewImages(def upload, long id) {
        if (id < 0) {
            id = getUnusedImageId(id)
        }
        if (!new File(TEMP_IMAGES_PATH).exists()) {
            new File(TEMP_IMAGES_PATH).mkdirs()
        }

        File tempImage = File.createTempFile(TEMP_IMAGES_PREFIX, IMAGE_TYPE, new File(TEMP_IMAGES_PATH))
        tempImage.deleteOnExit()
        upload.transferTo(tempImage)

        RenderedOp image = JAI.create("fileload", tempImage.toString());
        int maxDim = Math.max(image.getWidth(), image.getHeight());
        resize(tempImage, 1.0 * SMALL_SIZE / maxDim, constructPath(SMALL_IMAGES_PATH, id, true))
        resize(tempImage, 1.0 * LARGE_SIZE / maxDim, constructPath(LARGE_IMAGES_PATH, id, true))
        resize(tempImage, 1.0, constructPath(ORIG_IMAGES_PATH, id, true))

        tempImage.delete();
        
        return id
    }

    private long getUnusedImageId(long id) {
        synchronized (random) {
            while (true) {
                id = Math.abs(random.nextLong())
                if (!constructPath(SMALL_IMAGES_PATH, id, false).isFile()) {
                    return id
                }
                log.info("random collision for image with id ${id}")
            }
        }
        throw new IllegalStateException();
    }

    public void resize(File inputFile, double scale, File outputFile) {
        // read in the original image from an input stream
        JAI.disableDefaultTileCache()
        RenderedOp image = JAI.create("fileload", inputFile.toString());

        // now resize the image
        RenderingHints qualityHints = new RenderingHints(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        RenderedOp resizedImage = JAI.create("SubsampleAverage", image, scale, scale, qualityHints);

        // lastly, write the newly-resized image to an output stream, in a specific encoding
        JAI.create("encode", resizedImage, outputFile.newOutputStream(), IMAGE_TYPE, null);
    }


    public File constructPath(String basePath, long id, boolean mkDirs){
        List<String> path = new ArrayList<String>()
        path.add(basePath)

        // Split the id into chunks based on ID_INTERVAL and NUM_ID_DIRS
        int divisor = Math.pow(10, ID_INTERVAL)
        for(i in 0..<NUM_ID_DIRS){
            int num = (int)((id/Math.pow(divisor, i)) % divisor)
            path.add(("" + num).padLeft(ID_INTERVAL, "0"))
        }

        // Store the image.
        path.add("${id}.${IMAGE_TYPE}");

        // Create all the directories in the path so we can run mkdirs().
        String fileString = ""
        for(i in 0..<path.size() - 1){
            fileString += "${path.get(i)}/"
        }
        if (mkDirs) {
            new File(fileString).mkdirs()
        }

        // Now add the file name.
        fileString += path.get(path.size() - 1)

        return new File(fileString)
    }
}
