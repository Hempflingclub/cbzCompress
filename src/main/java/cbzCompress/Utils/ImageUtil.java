package cbzCompress.Utils;

import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.opencv.opencv_core.Mat;

import java.io.File;


abstract class ImageUtil { //package-private
    protected static void convertAndSaveImage(File imageFile, int quality) {
        convertAndAdjustQuality(imageFile, quality);
    }

    private static void convertAndAdjustQuality(File imageFile, int quality) {
        // read the image file
        Mat image = opencv_imgcodecs.imread(imageFile.getAbsolutePath(), opencv_imgcodecs.IMREAD_UNCHANGED);
        // get the file name without the extension
        String orgExtension = SevenZUtil.getFileExtension(imageFile);
        // create a new file with the same name in the same directory
        if (orgExtension.equals("gif")) {
            //Don't handle gif, just leave it as is
            return;
        }
        if (orgExtension.equals("png")) {
            convertToJPGImage(image, imageFile);
        }
        //Overwriting Image data, so ensuring after png -> jpg conversion no confusions and reconversions to png happen
        String pureImageFileName = SevenZUtil.getPureFileName(imageFile);
        imageFile = new File(imageFile.getParent(), pureImageFileName + ".jpg");
        image = opencv_imgcodecs.imread(imageFile.getAbsolutePath(), opencv_imgcodecs.IMREAD_UNCHANGED);
        minimizeJPGImage(image, imageFile, quality);
    }

    private static void minimizeJPGImage(Mat image, File imageFile, int quality) {
        // write the image data to the new file with the quality
        // Ensuring no data loss on unexpected quit
        String fileName = imageFile.getName();
        File tempImageFile = new File(imageFile.getParent(), fileName + ".tmp.jpg");
        if (tempImageFile.exists()) {
            while (!tempImageFile.delete()) ;
        }
        if (opencv_imgcodecs.imwrite(tempImageFile.getAbsolutePath(), image, new int[]{opencv_imgcodecs.IMWRITE_JPEG_QUALITY, quality})) {
            //Successfully applied quality to JPG
            //Overwrite Orginal with tmp
            while (!imageFile.delete()) ;
            tempImageFile.renameTo(imageFile);
        } else {
            //Failed to apply quality
            //Delete fragment
            if (tempImageFile.exists()) {
                while (!tempImageFile.delete()) ;
            }
        }
    }

    private static void convertToJPGImage(Mat image, File imageFile) {
        String pureFileName = imageFile.getName().substring(0, imageFile.getName().lastIndexOf("."));
        File newImageFile = new File(imageFile.getParent(), pureFileName + ".jpg");
        // write the image data to the new file with the quality
        if (opencv_imgcodecs.imwrite(newImageFile.getAbsolutePath(), image, new int[]{opencv_imgcodecs.IMWRITE_JPEG_OPTIMIZE, 1})) {
            //Successfully created JPG
            //Delete the original file
            if (imageFile.exists()) {
                while (!imageFile.delete()) ;
            }
        } else {
            //Failed to create JPG
            //Delete JPG fragment
            if (newImageFile.exists()) {
                while (!newImageFile.delete()) ;
            }
        }

    }

    /*private static void minimizeGifImage(File imageFile) {
        //org.bytedeco.ffmpeg
        //Figure out and complete Gif handling in ffmpeg package
        //https://github.com/bytedeco/javacpp-presets/tree/master/ffmpeg#the-readfewframejava-source-file
    }*/
}
