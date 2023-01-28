package cbzCompress;

import cbzCompress.Utils.CbzUtil;

public class Main {
    public static void main(String[] args) {
        if (args.length != 5) {
            System.out.println("Provide 5 Arguments:");
            System.out.println("{1} Path to inputFolder");
            System.out.println("{2} Path to tempFolder");
            System.out.println("{3} Path to outputFolder");
            System.out.println("{4} Path to finishedFolder");
            System.out.println("{5} Number of Minutes between Recompression (1-x)");
            System.exit(0);
        }
        String inputPath = args[0];
        String tempPath = args[1];
        String outputPath = args[2];
        String finishedPath = args[3];
        int waitingMinutes = 0;
        try {
            waitingMinutes = Integer.valueOf(args[4]);
        } catch (NumberFormatException e) {
            System.out.println("{5} was not a valid input, only whole numbers at or above 1 accepted, no decimals nor commas");
            System.exit(0);
        }
        if (waitingMinutes <= 0) {
            System.out.println("{5} was not a valid input, only whole numbers at or above 1 accepted, no decimals nor commas");
            System.exit(0);
        }
        boolean recompressingStatus = true;
        while (recompressingStatus) {
            recompressingStatus = CbzUtil.constantCompressionAction(inputPath, tempPath, outputPath, finishedPath, waitingMinutes);
        }
        System.out.println("Finished");
    }
}