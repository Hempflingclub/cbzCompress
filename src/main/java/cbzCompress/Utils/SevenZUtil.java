package cbzCompress.Utils;

import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry;
import org.apache.commons.compress.archivers.sevenz.SevenZFile;
import org.apache.commons.compress.archivers.sevenz.SevenZMethod;
import org.apache.commons.compress.archivers.sevenz.SevenZOutputFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static java.lang.Thread.sleep;

abstract class SevenZUtil { //package-private
    protected static final String fileExtension = ".cbz";

    protected static Path compressFolder(String folderPath, String destinationDir) {
        File folder = new File(folderPath);
        String fileName = folder.getName();
        String outputFilePath = destinationDir + File.separator + fileName + fileExtension;
        try {
            SevenZOutputFile sevenZOutput = new SevenZOutputFile(new File(outputFilePath));
            sevenZOutput.setContentCompression(SevenZMethod.LZMA2);
            compressFolderUtil(folder, sevenZOutput);
            sevenZOutput.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Path.of(outputFilePath);
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

    protected static Path extract7zArchive(String filePath, String destPath) {
        File targetArchive = new File(filePath);
        //Building Structure for Extracted Files
        String fileName = targetArchive.getName();
        String pureFileName = SevenZUtil.getPureFileName(fileName);
        Path outputFolderPath = Path.of(destPath, File.separator, pureFileName);
        File outputFolder = outputFolderPath.toFile();
        if (outputFolder.exists()) {
            constantDelete(outputFolder);
        }
        outputFolder.mkdir();
        try {
            SevenZFile archive = new SevenZFile(targetArchive);
            for (SevenZArchiveEntry archivedFile : archive.getEntries()) {
                if (!archivedFile.isDirectory() && archivedFile.getSize() > 1) {
                    String currentFileName = archivedFile.getName();
                    //This will just get the 'filename' and not any folder structure of the archive, annoying to find out
                    currentFileName = currentFileName.substring(currentFileName.lastIndexOf("/") + 1);
                    String currentFilePath = outputFolderPath + File.separator + currentFileName;
                    InputStream archivedFileInputStream = archive.getInputStream(archivedFile);
                    FileOutputStream out = new FileOutputStream(currentFilePath);
                    byte[] content = archivedFileInputStream.readAllBytes();
                    out.write(content);
                    out.close();
                }
            }
            archive.close();
            return outputFolderPath;
        } catch (IOException ioException) {
            if (ioException.fillInStackTrace().getMessage().equals("Bad 7z signature")) {
                //Probably not 7z but zip
                //Zip extraction
                return extractZipArchive(filePath, destPath);
            } else {
                System.out.println("IOException during archive opening");
                ioException.printStackTrace();
            }
        }
        return null;
    }

    private static Path extractZipArchive(String filePath, String destPath) {
        File targetArchive = new File(filePath);
        //Building Structure for Extracted Files
        String fileName = targetArchive.getName();
        String pureFileName = SevenZUtil.getPureFileName(fileName);
        Path outputFolderPath = Path.of(destPath, File.separator, pureFileName);
        File outputFolder = outputFolderPath.toFile();
        if (outputFolder.exists()) {
            constantDelete(outputFolder);
        }
        outputFolder.mkdir();
        try {
            ZipFile archive = new ZipFile(targetArchive);
            for (ZipEntry archivedFile : archive.stream().toList()) {
                if (!archivedFile.isDirectory() && archivedFile.getSize() > 1) {
                    String currentFileName = archivedFile.getName();
                    //This will just get the 'filename' and not any folder structure of the archive, annoying to find out
                    currentFileName = currentFileName.substring(currentFileName.lastIndexOf("/") + 1);
                    String currentFilePath = outputFolderPath + File.separator + currentFileName;
                    InputStream archivedFileInputStream = archive.getInputStream(archivedFile);
                    FileOutputStream out = new FileOutputStream(currentFilePath);
                    byte[] content = archivedFileInputStream.readAllBytes();
                    out.write(content);
                    out.close();
                }
            }
            archive.close();
            return outputFolderPath;
        } catch (IOException ioException) {
            System.out.println("IOException during archive opening");
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
        return getFileExtension(fileName);
    }

    protected static String getPureFileName(String fileName) {
        String fileExtension = fileName.substring(0, fileName.lastIndexOf("."));
        return fileExtension;
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
                interruptedException.printStackTrace();
            }
        }
    }
}
