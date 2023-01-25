package cbzCompress.Utils;


import java.io.File;
import java.nio.file.Path;
import java.util.Objects;

abstract public class CbzUtil {
    //Use Utils to solve Minimize CBZ Size, by recompressing after lowering Image Quality to 70
    protected static final int quality = 70;

    private static Path extractFirstArchiveInTarget(String targetArchiveFolder, String destFolder) {
        Path targetArchiveFolderPath = Path.of(targetArchiveFolder);
        File targetArchiveFolderFile = targetArchiveFolderPath.toFile();
        if (targetArchiveFolderFile.listFiles() != null) {
            File firstFile = Objects.requireNonNull(targetArchiveFolderFile.listFiles())[0];
            return SevenZUtil.extractArchive(firstFile.getPath(), destFolder);
        }
        return null;
    }

}
