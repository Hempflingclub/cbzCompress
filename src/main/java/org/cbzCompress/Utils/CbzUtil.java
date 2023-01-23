package org.cbzCompress.Utils;


abstract public class CbzUtil {
    //Use Utils to solve Minimize CBZ Size, by recompressing after lowering Image Quality to 70
    private static final int quality = 70;

    public static void testImageCompression(String imagePath) {
        ImageUtil.convertAndSaveImage(imagePath, quality);
    }

    public static String testFolderCompression(String targetFolderPath, String destPath) {
        return SevenZUtil.compressFolder(targetFolderPath, destPath);
    }

    public static String testArchiveExtraction(String targetArchive, String destFolder) {
        return SevenZUtil.extractArchive(targetArchive, destFolder);
    }

}
