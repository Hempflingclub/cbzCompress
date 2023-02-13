package cbzCompress.Utils;

import java.io.*;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logger {
    static final private String logFileName = "crashLog.txt";
    static final private File logFile = Paths.get(logFileName).toFile();

    private static void writeIntoLogFile(String textLine) {
        try {
            FileWriter fw = new FileWriter(logFile);
            fw.write(getStringWithTimestamp(textLine) + System.getProperty("line.separator"));
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void logException(Exception exception) {
        String errorMessage = exception.getMessage();
        writeIntoLogFile(errorMessage);
    }

    public static String getCurrentTimeFormatted() {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime currentTime = LocalDateTime.now();
        String currentTimeFormatted = dateTimeFormatter.format(currentTime);
        return currentTimeFormatted;
    }

    public static String getStringWithTimestamp(String orgString) {
        String currentTimeFormatted = getCurrentTimeFormatted();
        return currentTimeFormatted + "| " + orgString;
    }
}
