package cbzCompress.Utils;

import java.io.*;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logger {
    static final private String logFileName = "crashLog.txt";
    static final private File logFile = Paths.get(logFileName).toFile();

    private static void writeIntoLogFile(String textLine) {
        if (!logFile.exists()) {
            try {
                logFile.createNewFile();
            } catch (IOException e) {
                logException(e);
            }
        }
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime currentTime = LocalDateTime.now();
        String currentTimeFormatted = dateTimeFormatter.format(currentTime);
        try {
            FileWriter FW = new FileWriter(logFile);
            FW.append(currentTimeFormatted).append("| ").append(textLine).append(System.getProperty("line.separator"));
        } catch (IOException e) {
            logException(e);
        }
    }

    public static void logException(Exception exception) {
        String errorMessage = exception.getMessage();
        writeIntoLogFile(errorMessage);
    }
}
