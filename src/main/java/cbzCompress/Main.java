package cbzCompress;

import cbzCompress.Utils.CbzUtil;

public class Main {
    public static void main(String[] args) {
        if (args.length < 5 || args.length > 6) {
            System.out.println("Provide atleast 5 Arguments:");
            System.out.println("{1} Path to inputFolder");
            System.out.println("{2} Path to tempFolder");
            System.out.println("{3} Path to outputFolder");
            System.out.println("{4} Path to finishedFolder");
            System.out.println("{5} Number of Minutes between Recompression (1-x)");
            System.out.println("{6} 'optional' JPG compression quality default: 70 Integer [0-100]");
            System.exit(0);
        }
        String inputPath = args[0];
        String tempPath = args[1];
        String outputPath = args[2];
        String finishedPath = args[3];
        int waitingMinutes = 0;
        try {
            waitingMinutes = Integer.parseInt(args[4]);
        } catch (NumberFormatException e) {
            System.out.println("{5} was not a valid input, only whole numbers at or above 1 accepted, no decimals nor commas");
            System.exit(0);
        }
        if (waitingMinutes < 0) {
            System.out.println("{5} was not a valid input, only whole numbers at or above 1 accepted, no decimals nor commas");
            System.exit(0);
        }
        int quality = 70;
        if (args.length > 5) {
            try {
                quality = Integer.parseInt(args[5]);
            } catch (NumberFormatException e) {
                System.out.println("{6} was not a valid input, only Integers in the range 0-100 are accepted");
                System.exit(0);
            }
        }
        if (quality < 0 || quality > 100) {
            System.out.println("{6} was not a valid input, only Integers in the range 0-100 are accepted");
            System.exit(0);
        }
        boolean recompressingStatus = true;
        System.out.println("Started cbzCompress V1.3.17");
        while (recompressingStatus) {
            recompressingStatus = CbzUtil.constantCompressionAction(inputPath, tempPath, outputPath, finishedPath, waitingMinutes,quality);
            System.gc();
        }
        System.out.println("Finished");
    }
}