package cbzCompress;

import cbzCompress.Utils.CbzUtil;

import java.nio.file.Path;

public class Main {
    public static void main(String[] args) {
        //Run CbzUtil for all files in x Folder, and rerun every 30mins with rescanning the folder

        //Tested:
        /*
        Image Compression (Conversion Png-JPG) works flawlessly now
        Compression of Folder into .cbz works
        Extraction function
        Image Compression, in function followed with Compression
         */
        //To be done:
        /*
        Completed System
        .jar arguments, to define the baseFolders, without recompiling
         */
        String inputPath = "F:\\Projekte\\IntelliJ\\cbzCompress\\compressTest\\Input";
        String tempPath = "F:\\Projekte\\IntelliJ\\cbzCompress\\compressTest\\Tmp";
        String outputPath = "F:\\Projekte\\IntelliJ\\cbzCompress\\compressTest\\Out";
        Path resultArchive = CbzUtil.fullRecompressionOfFirstFile(inputPath, tempPath, outputPath);
        System.out.println(resultArchive);
    }
}