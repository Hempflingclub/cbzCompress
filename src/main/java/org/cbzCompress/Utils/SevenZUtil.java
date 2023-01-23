package org.cbzCompress.Utils;

import org.apache.commons.compress.archivers.sevenz.*;

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
        File targetArchive = new File(filePath);
        String fileName = targetArchive.getName();
        String pureFileName = SevenZUtil.getPureFileName(fileName);
        String outputFolderPath = destPath + File.separator + pureFileName;
        File outputFolder = new File(outputFolderPath);
        outputFolder.mkdir();
        try {
            SevenZFile archive = new SevenZFile(targetArchive);
            for (SevenZArchiveEntry archivedFile : archive.getEntries()) {
                if (!archivedFile.isDirectory()) {
                    String currentFileName = archivedFile.getName();
                    //This will just get the 'filename' and not any folder structure of the archive, annoying to find out
                    currentFileName = currentFileName.substring(currentFileName.lastIndexOf("/") + 1);
                    String currentFilePath = outputFolderPath + File.separator + currentFileName;
                    FileOutputStream out = new FileOutputStream(currentFilePath);
                    byte[] content = new byte[(int) archivedFile.getSize()];
                    // Need to actually get content
                    archive.read(content, 0, content.length);
                    out.write(content);
                    out.close();
                }
            }
            archive.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return outputFolderPath;
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
}
