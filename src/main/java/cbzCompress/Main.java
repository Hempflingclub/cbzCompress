package cbzCompress;

import cbzCompress.Utils.CbzUtil;

public class Main {
    public static void main(String[] args) {
        //Run CbzUtil for all files in x Folder, and rerun every 30mins with rescanning the folder

        //Tested:
        /*
        Image Compression (Conversion Png-JPG) works flawlessly now
        Compression of Folder into .cbz works
        Extraction function
        Image Compression, in function followed with Compression
        Completed System
        .jar arguments, to define the baseFolders, without recompiling
         */
        if (args.length != 4) {
            System.out.println("Provide 4 Arguments:");
            System.out.println("{1} Path to inputFolder");
            System.out.println("{2} Path to tempFolder");
            System.out.println("{3} Path to outputFolder");
            System.out.println("{4} Path to finishedFolder");
            System.exit(0);
        }
        String inputPath = args[0];
        String tempPath = args[1];
        String outputPath = args[2];
        String finishedPath = args[3];
        boolean recompressingStatus = true;
        while (recompressingStatus) {
            recompressingStatus = CbzUtil.constantCompressionAction(inputPath, tempPath, outputPath, finishedPath);
        }
        System.out.println("Finished");
    }
}