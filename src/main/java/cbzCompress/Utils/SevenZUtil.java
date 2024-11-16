package cbzCompress.Utils;

import org.apache.commons.compress.archivers.sevenz.*;
import org.tukaani.xz.LZMA2Options;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static java.lang.Thread.sleep;

abstract class SevenZUtil { //package-private
    protected static final String fileExtension = ".cbz";

    protected static File compressFolder(File folderPath, File destinationDir) throws IOException {
        String fileName = folderPath.getName();
        File outputFile = new File(destinationDir,fileName+fileExtension);
        if (outputFile.exists()) while (!outputFile.delete()) ;

        SevenZOutputFile sevenZOutput = new SevenZOutputFile(outputFile);
        LZMA2Options lzma2Options = new LZMA2Options();
        lzma2Options.setPreset(LZMA2Options.PRESET_MAX);
        lzma2Options.setDictSize(16 << 20);
        sevenZOutput.setContentMethods(Collections.singleton(new SevenZMethodConfiguration(SevenZMethod.LZMA, lzma2Options)));
        compressFolderUtil(folderPath, sevenZOutput);
        sevenZOutput.close();
        return outputFile;
    }

    private static void compressFolderUtil(File folder, SevenZOutputFile sevenZOutput) throws IOException {
        File[] files = folder.listFiles();
        assert files != null;
        for (File file : files) {
            SevenZArchiveEntry entry = sevenZOutput.createArchiveEntry(file, file.getName());
            sevenZOutput.putArchiveEntry(entry);
            sevenZOutput.write(file.toPath());
            sevenZOutput.closeArchiveEntry();
        }
    }

    protected static File extract7zArchive(File filePath, File destPath) {
        //Building Structure for Extracted Files
        File outputFolder = new File(destPath, "tmp");
        if (outputFolder.exists()) constantDelete(outputFolder);

        while (!outputFolder.mkdir()) ;
        try {
            SevenZFile archive = new SevenZFile(filePath);
            for (SevenZArchiveEntry archivedFile : archive.getEntries()) {
                if (!archivedFile.isDirectory() && archivedFile.getSize() > 1) {
                    String currentFileName = archivedFile.getName();
                    //This will just get the 'filename' and not any folder structure of the archive, annoying to find out
                    currentFileName = currentFileName.substring(currentFileName.lastIndexOf("/") + 1);
                    File currentFile = new File(outputFolder, currentFileName);
                    InputStream archivedFileInputStream = archive.getInputStream(archivedFile);
                    FileOutputStream out = new FileOutputStream(currentFile);
                    byte[] content = archivedFileInputStream.readAllBytes();
                    archivedFileInputStream.close();
                    out.write(content);
                    out.close();
                }
            }
            archive.close();
            return outputFolder;
        } catch (IOException ioException) {
            if (ioException.fillInStackTrace().getMessage().equals("Bad 7z signature")) {
                //Probably not 7z but zip
                //Zip extraction
                return extractZipArchive(filePath, destPath);
            } else {
                System.out.println("IOException during archive opening");
                Logger.logException(ioException);
                ioException.printStackTrace();
            }
        }
        return null;
    }

    private static File extractZipArchive(File filePath, File destPath) {
        //Building Structure for Extracted Files
        File outputFolder = new File(destPath, "tmp");
        if (outputFolder.exists()) {
            constantDelete(outputFolder);
        }
        while (!outputFolder.mkdir()) ;
        try {
            ZipFile archive = new ZipFile(filePath);
            for (ZipEntry archivedFile : archive.stream().toList()) {
                if (!archivedFile.isDirectory() && archivedFile.getSize() > 1) {
                    String currentFileName = archivedFile.getName();
                    //This will just get the 'filename' and not any folder structure of the archive, annoying to find out
                    currentFileName = currentFileName.substring(currentFileName.lastIndexOf("/") + 1);
                    File currentFile = new File(outputFolder, currentFileName);
                    InputStream archivedFileInputStream = archive.getInputStream(archivedFile);
                    FileOutputStream out = new FileOutputStream(currentFile);
                    byte[] content = archivedFileInputStream.readAllBytes();
                    archivedFileInputStream.close();
                    out.write(content);
                    out.close();
                }
            }
            archive.close();
            return outputFolder;
        } catch (IOException ioException) {
            System.out.println("IOException during archive opening");
            Logger.logException(ioException);
            ioException.printStackTrace();
        }
        return null;
    }

    protected static String getFileExtension(File file) {
        String fileName = file.getName();
        return getFileExtension(fileName);
    }

    protected static String getFileExtension(String fileName) {
        String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1);
        return fileExtension;
    }

    protected static String getPureFileName(File file) {
        String fileName = file.getName();
        return getPureFileName(fileName);
    }

    protected static String getPureFileName(String fileName) {
        String pureFileName = fileName.substring(0, fileName.lastIndexOf("."));
        return pureFileName;
    }

    private static boolean deleteDirectory(File directoryToBeDeleted) {
        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteDirectory(file);
            }
        }
        return directoryToBeDeleted.delete();
    }

    public static void constantDelete(File folderToDelete) {
        while (!deleteDirectory(folderToDelete)) {
            System.out.println("Archive is in use");
            try {
                sleep(1000);
            } catch (InterruptedException interruptedException) {
                Logger.logException(interruptedException);
                interruptedException.printStackTrace();
            }
        }
    }
}
