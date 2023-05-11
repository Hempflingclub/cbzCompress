package cbzCompress.Utils;

import com.sun.jna.Native;
import com.sun.jna.Platform;
import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.opencv.opencv_core.Mat;

import java.io.File;


abstract class ImageUtil { //package-private
    protected static void convertAndSaveImage(File imageFile, int quality) {
        convertAndAdjustQuality(imageFile, quality);
    }

    private static void convertAndAdjustQuality(File imageFile, int quality) {
        // read the image file
        Mat imageMat = getMat(imageFile);
        // get the file name without the extension
        String orgExtension = SevenZUtil.getFileExtension(imageFile);
        // create a new file with the same name in the same directory
        if (orgExtension.equals("gif")) {
            //Don't handle gif, just leave it as is
            return;
        }
        if (orgExtension.equals("png")) {
            convertToJPGImage(imageMat, imageFile);
            imageMat.release();
        }
        //Overwriting Image data, so ensuring after png -> jpg conversion no confusions and reconversions to png happen
        String pureImageFileName = SevenZUtil.getPureFileName(imageFile);
        imageFile = new File(imageFile.getParent(), pureImageFileName + ".jpg");
        imageMat = getMat(imageFile);
        while (!imageFile.exists()) ; //Busy waiting until FileSystem finished writing file
        minimizeJPGImage(imageMat, imageFile, quality);
        imageMat.release();
    }

    private static void minimizeJPGImage(Mat imageMat, File imageFile, int quality) {
        // write the image data to the new file with the quality
        // Ensuring no data loss on unexpected quit
        String fileName = SevenZUtil.getPureFileName(imageFile);
        String newExtension = "tmp.jpg";
        File tempImageFile = new File(imageFile.getParent(), fileName + "." + newExtension);
        if (tempImageFile.exists()) {
            while (!tempImageFile.delete()) ;
        }
        if (compressJPG(newExtension, imageFile, imageMat, quality)) {
            //Successfully applied quality to JPG
            //Overwrite Orginal with tmp
            while (!imageFile.delete()) ;
            while (!tempImageFile.renameTo(imageFile)) ;
        } else {
            //Failed to apply quality
            //Delete fragment
            if (tempImageFile.exists()) {
                while (!tempImageFile.delete()) ;
            }
        }
    }

    private static void convertToJPGImage(Mat imageMat, File imageFile) {
        String pureFileName = SevenZUtil.getPureFileName(imageFile);
        String newExtension = "jpg";
        File newImageFile = new File(imageFile.getParent(), pureFileName + "." + newExtension);
        // write the image data to the new file with the quality
        if (convertToJPG(newExtension, imageFile, imageMat)) {
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

    private static boolean compressJPG(String newExtension, File originalImageFile, Mat imageMat, int quality) {
        return makeImage(newExtension, originalImageFile, imageMat, new int[]{opencv_imgcodecs.IMWRITE_JPEG_QUALITY, quality});
    }

    private static boolean convertToJPG(String newExtension, File orignalImageFile, Mat imageMat) {
        return makeImage(newExtension, orignalImageFile, imageMat, new int[]{opencv_imgcodecs.IMWRITE_JPEG_OPTIMIZE, 1});
    }

    private static boolean makeImage(String newExtension, File originalImageFile, Mat imageMat, int[] imageOptions) {
        String originalImageFilePath = getPath(originalImageFile);
        //Replacing Extension, to ensure Short Paths of Windows work
        String orgExtension = SevenZUtil.getFileExtension(originalImageFile);
        int extensionIndex = originalImageFilePath.lastIndexOf(orgExtension);
        String finalImagePath = originalImageFilePath.substring(0, extensionIndex) + newExtension;
        return opencv_imgcodecs.imwrite(finalImagePath, imageMat, imageOptions);
    }

    private static Mat getMat(File imageFile) {
        String filePath = getPath(imageFile);
        return opencv_imgcodecs.imread(filePath, opencv_imgcodecs.IMREAD_UNCHANGED);
    }

    private static String getPath(File file) {
        String filePath;
        if (Platform.isWindows()) {
            String longPath = "\\\\?\\" + file.getAbsolutePath();
            int bufferSize = com.sun.jna.platform.win32.Kernel32.INSTANCE.GetShortPathName(longPath, null, 0);
            char[] result = new char[bufferSize];
            com.sun.jna.platform.win32.Kernel32.INSTANCE.GetShortPathName(longPath, result, result.length);
            filePath = Native.toString(result); // making shortPath's using ~1 (like dir /X) does
        } else {
            filePath = file.getAbsolutePath();
        }
        return filePath;
    }

    /*private static void minimizeGifImage(File imageFile) {
        //org.bytedeco.ffmpeg
        //Figure out and complete Gif handling in ffmpeg package
        //https://github.com/bytedeco/javacpp-presets/tree/master/ffmpeg#the-readfewframejava-source-file
    }*/
}
