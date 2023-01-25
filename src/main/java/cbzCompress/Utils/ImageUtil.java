package cbzCompress.Utils;

import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.opencv.opencv_core.Mat;

import java.io.File;

abstract class ImageUtil { //package-private
    protected static void convertAndSaveImage(File imageFile, int quality) {
        convertAndAdjustQuality(imageFile, quality);
    }

    private static void convertAndAdjustQuality(File imageFile, int quality) {
        try {
            // read the image file
            Mat image = opencv_imgcodecs.imread(imageFile.getAbsolutePath(), opencv_imgcodecs.IMREAD_UNCHANGED);

            // get the file name without the extension
            String pureFileName = imageFile.getName().substring(0, imageFile.getName().lastIndexOf("."));
            String orgExtension = SevenZUtil.getFileExtension(imageFile);
            // create a new file with the same name in the same directory
            File newFile = new File(imageFile.getParent(), pureFileName + ".jpg");
            // write the image data to the new file with the quality
            opencv_imgcodecs.imwrite(newFile.getAbsolutePath(), image, new int[]{opencv_imgcodecs.IMWRITE_JPEG_QUALITY, quality});
            // delete the original file
            if (!orgExtension.equals("jpg")) {
                if (!imageFile.delete()) {
                    System.out.println("Failed to delete original file");
                }
            }
        } catch (Exception e) {
            System.out.println("Error converting file: " + e.getMessage());
        }
    }
}
