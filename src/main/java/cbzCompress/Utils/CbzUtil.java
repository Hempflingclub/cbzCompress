package cbzCompress.Utils;


import cbzCompress.DataTypes.Result;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import static cbzCompress.Utils.SevenZUtil.constantDelete;
import static java.lang.Thread.sleep;

abstract public class CbzUtil {
    //Use Utils to solve Minimize CBZ Size, by recompressing after lowering Image Quality to 70

    private static void compressImagesInFolder(File targetFolderFile, int quality) {
        File[] imageFiles = targetFolderFile.listFiles();
        assert imageFiles != null;
        for (File imageFile : imageFiles) {
            ImageUtil.convertAndSaveImage(imageFile, quality);
        }
    }

    private static void wait(int minutes) {
        int minimumSleepMs = 5 * 1000;
        int seconds = minutes * 60;
        int ms = seconds * 1000;
        try {
            sleep(Math.max(ms, minimumSleepMs));
        } catch (InterruptedException e) {
            Logger.logException(e);
            throw new RuntimeException(e);
        }
    }

    public static boolean constantCompressionAction(File targetArchiveFolder, File tempFolder, File destFolder, File finishedFolder, int waitingMinutes, int quality) {
        wait(waitingMinutes);
        Result result = fullRecompressionOfFirstFile(targetArchiveFolder, tempFolder, destFolder,quality);
        if (result == null) return false;
        File resultArchive = result.recompressedArchive();
        File originalArchive = result.originalArchive();


        String fileName = originalArchive.getName();
        File finishedFile = new File(finishedFolder,fileName);
        System.out.println(Logger.getStringWithTimestamp("Finished: " + fileName)); // Finished CBZ
        finishedFolder.mkdirs();
        //Moving of File
        try {
            while(!resultArchive.renameTo(finishedFile));
            constantDelete(originalArchive);
        } catch (Exception e) {
            System.out.println("IOException during moving to finished folder");
            Logger.logException(e);
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private static Result fullRecompressionOfFirstFile(File targetArchiveFolder, File tempFolder, File destFolder, int quality) {
        //Just in case create provided Paths
        targetArchiveFolder.mkdirs();
        tempFolder.mkdirs();
        destFolder.mkdirs();

        File[] extractionResult = extractFirstArchiveInTarget(targetArchiveFolder, tempFolder);
        if (extractionResult != null) {
            File orginalArchive = extractionResult[0], extractedFolder = extractionResult[1];
            compressImagesInFolder(extractedFolder,quality);
            try {
                System.out.println(Logger.getStringWithTimestamp("Minimized Images: " + orginalArchive.getName())); // Minimized Images
                File resultArchive = SevenZUtil.compressFolder(extractedFolder, destFolder);
                System.out.println(Logger.getStringWithTimestamp("Recompressed: " + orginalArchive.getName())); // Recompressed
                //Delete temp folder
                constantDelete(extractedFolder);
                return new Result(resultArchive,orginalArchive);
            } catch (IOException e) {
                constantDelete(extractedFolder);
                Logger.logException(e);
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    private static File[] extractFirstArchiveInTarget(File targetArchiveFolder, File destFolder) {
        if (targetArchiveFolder.listFiles() != null) {
            File[] files = Objects.requireNonNull(targetArchiveFolder.listFiles());
            shuffleFiles(files);
            for (File targetFile : files) {
                String archiveExtension = SevenZUtil.getFileExtension(targetArchiveFolder);
                if (targetFile.isDirectory()) {
                    //Cannot be an archive
                    //No further differentiation based on file extension, to avoid custom solutions failing
                    continue;
                }
                if (archiveExtension.equals("tmp")) {
                    //Ignoring .tmp Archives
                    continue;
                }
                File extractedFolder = SevenZUtil.extract7zArchive(targetFile, destFolder);
                System.out.println(Logger.getStringWithTimestamp("Extracted: " + targetFile.getName())); // Extracted
                return new File[]{targetFile, extractedFolder};
            }
        }
        return null;
    }

    private static void shuffleFiles(File[] files) {
        Random rnd = ThreadLocalRandom.current();
        for (int i = files.length - 1; i > 0; i--) {
            int index = rnd.nextInt(i + 1);
            File a = files[index];
            files[index] = files[i];
            files[i] = a;
        }
    }
}
