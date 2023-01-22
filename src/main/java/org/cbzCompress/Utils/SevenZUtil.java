package org.cbzCompress.Utils;

import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry;
import org.apache.commons.compress.archivers.sevenz.SevenZMethod;
import org.apache.commons.compress.archivers.sevenz.SevenZOutputFile;

import java.io.*;

abstract class SevenZUtil { //package-private
    protected static final String fileExtension = ".cbz";

    protected static String compressFolder(String folderPath, String destinationDir) {
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
        return outputFilePath;
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

    protected static String extractArchive(String filePath, String destPath) {
        /*
        Extract the Archive at filePath
        The File can be a 7z Archive, or a Zip file, so both should be supported
        Save the Contents of the Archive in the destPath, in a folder with the name set to the original file's name
        TBD
         */
        return "";
    }

    protected static String getFileExtension(File file) {
        String fileName = file.getName();
        return getFileExtension(fileName);
    }

    protected static String getFileExtension(String fileName) {
        String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1);
        return fileExtension;
    }
}
