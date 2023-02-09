package cbzCompress.Utils;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import static cbzCompress.Utils.SevenZUtil.constantDelete;
import static java.lang.Thread.sleep;

abstract public class CbzUtil {
    //Use Utils to solve Minimize CBZ Size, by recompressing after lowering Image Quality to 70
    protected static final int quality = 70;

    private static void compressImagesInFolder(File targetFolderFile) {
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
        }
    }

    public static boolean constantCompressionAction(String targetArchiveFolder, String tempFolder, String destFolder, String finishedFolder, int waitingMinutes) {
        wait(waitingMinutes);
        Path resultArchive = fullRecompressionOfFirstFile(targetArchiveFolder, tempFolder, destFolder);
        if (resultArchive == null) {
            return false;
        }
        File resultArchiveFile = resultArchive.toFile();
        String fileName = resultArchiveFile.getName();
        System.out.println(fileName);
        Path finishedFolderPath = Path.of(finishedFolder + File.separator + fileName);
        //Just in case create provided Path of finishedFolder
        File finishedFolderFile = new File(finishedFolder);
        finishedFolderFile.mkdirs();
        //Moving of File
        try {
            Files.copy(resultArchive, finishedFolderPath);
            if (resultArchiveFile.exists()) {
                while (!resultArchiveFile.delete()) ;
            }
        } catch (IOException e) {
            System.out.println("IOException during moving to finished folder");
            Logger.logException(e);
            return false;
        }
        return true;
    }

    private static Path fullRecompressionOfFirstFile(String targetArchiveFolder, String tempFolder, String destFolder) {
        //Just in case create provided Paths
        File targetArchiveFolderFile = new File(targetArchiveFolder);
        File tempFolderFile = new File(tempFolder);
        File destFolderFile = new File(destFolder);
        targetArchiveFolderFile.mkdirs();
        tempFolderFile.mkdirs();
        destFolderFile.mkdirs();

        File[] extractionResult = extractFirstArchiveInTarget(targetArchiveFolder, tempFolder);
        if (extractionResult != null) {
            File orginalArchive = extractionResult[0];
            File extractedFolder = extractionResult[1]; // This cannot be done in single Line, because Java doesn't support it apparently
            compressImagesInFolder(extractedFolder);
            try {
                Path resultArchive = SevenZUtil.compressFolder(extractedFolder.toString(), destFolder);
                //Delete temp folder, and original Archive
                constantDelete(extractedFolder);
                constantDelete(orginalArchive);
                return resultArchive;
            } catch (IOException e) {
                constantDelete(extractedFolder);
                Logger.logException(e);
            }
        }
        return null;
    }

    private static File[] extractFirstArchiveInTarget(String targetArchiveFolder, String destFolder) {
        Path targetArchiveFolderPath = Path.of(targetArchiveFolder);
        File targetArchiveFolderFile = targetArchiveFolderPath.toFile();
        if (targetArchiveFolderFile.listFiles() != null) {
            for (File targetFile : Objects.requireNonNull(targetArchiveFolderFile.listFiles())) {
                String archiveExtension = SevenZUtil.getFileExtension(targetArchiveFolderFile);
                if (targetFile.isDirectory()) {
                    //Cannot be an archive
                    //No further differentiation based on file extension, to avoid custom solutions failing
                    continue;
                }
                if(archiveExtension.equals("tmp")){
                    //Ignoring .tmp Archives
                    continue;
                }
                File orginalArchive = targetFile;
                File extractedFolder = SevenZUtil.extract7zArchive(targetFile.getPath(), destFolder);
                return new File[]{orginalArchive, extractedFolder};
            }
        }
        return null;
    }
}
