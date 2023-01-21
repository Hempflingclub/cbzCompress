package org.cbzCompress.Utils;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry;
import org.apache.commons.compress.archivers.sevenz.SevenZMethod;
import org.apache.commons.compress.archivers.sevenz.SevenZOutputFile;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorInputStream;
import org.apache.commons.compress.compressors.CompressorStreamFactory;

import java.io.*;
import java.util.zip.Deflater;

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
            BufferedInputStream input = new BufferedInputStream(new FileInputStream(file));
            byte[] buffer = new byte[1024];
            int len;
            while ((len = input.read(buffer)) > 0) {
                sevenZOutput.write(buffer, 0, len);
            }
            input.close();
            sevenZOutput.closeArchiveEntry();
        }
    }

    protected static String extractArchive(String filePath, String destPath) {
        //Extract the Archive at filePath
        //The File can be a 7z Archive, or a Zip file, so both should be supported
        //Save the Contents of the Archive in the destPath, in a folder with the name set to the original file's name
        //TBD
        return "";
    }
}
