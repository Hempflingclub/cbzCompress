package cbzCompress.Utils;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logger {
    static final private String logFileName = "crashLog.txt";
    static final private File logFile = Paths.get(getJarFolder().getPath(), File.separator, logFileName).toFile();

    public static File getJarFolder() {
        try {
            File jarFolder = new File(Logger.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            jarFolder = jarFolder.getParentFile();
            return jarFolder;
        } catch (URISyntaxException e) {
            Logger.logException(e);
            throw new RuntimeException(e);
        }
    }

    private static void writeIntoLogFile(String textLine) {
        try {
            if(!logFile.exists()){while(!logFile.createNewFile());}
            FileWriter fw = new FileWriter(logFile,true);
            fw.append(getStringWithTimestamp(textLine)).append(System.getProperty("line.separator"));
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void logException(Exception exception) {
        String errorMessage = exception.getMessage();
        StackTraceElement[] stacktraceList = exception.getStackTrace();
        writeIntoLogFile(errorMessage);
        for (StackTraceElement stacktrace : stacktraceList) {
            String stacktraceString = stacktrace.toString();
            writeIntoLogFile(stacktraceString);
        }
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
