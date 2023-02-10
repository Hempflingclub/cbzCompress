package cbzCompress.Utils;

import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.opencv.opencv_core.Mat;

import java.io.File;
import java.io.IOException;


abstract class ImageUtil { //package-private
    protected static void convertAndSaveImage(File imageFile, int quality) {
        convertAndAdjustQuality(imageFile, quality);
    }

    private static void convertAndAdjustQuality(File imageFile, int quality) {
        // read the image file
        Mat image = getMat(imageFile);
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
        image = getMat(imageFile);
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
        if (compressJPG(tempImageFile, image, quality)) {
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
        if (convertToJPG(newImageFile, image)) {
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

    private static boolean compressJPG(File file, Mat imageMat, int quality) {
        return makeImage(file, imageMat, new int[]{opencv_imgcodecs.IMWRITE_JPEG_QUALITY, quality});
    }

    private static boolean convertToJPG(File file, Mat imageMat) {
        return makeImage(file, imageMat, new int[]{opencv_imgcodecs.IMWRITE_JPEG_OPTIMIZE, 1});
    }

    private static boolean makeImage(File file, Mat imageMat, int[] imageOptions) {
        return opencv_imgcodecs.imwrite(getPath(file), imageMat, imageOptions);
    }

    private static Mat getMat(File imageFile) {
        return opencv_imgcodecs.imread(getPath(imageFile), opencv_imgcodecs.IMREAD_UNCHANGED);
    }

    private static String getPath(File file) {
        try {
            String filename = file.getCanonicalPath();
            return filename;
        } catch (IOException e) {
            Logger.logException(e);
            throw new RuntimeException(e);
        }
    }

    /*private static void minimizeGifImage(File imageFile) {
        //org.bytedeco.ffmpeg
        //Figure out and complete Gif handling in ffmpeg package
        //https://github.com/bytedeco/javacpp-presets/tree/master/ffmpeg#the-readfewframejava-source-file
    }*/
}
