package cbzCompress.DataTypes;


import java.io.File;

public record Result(File recompressedArchive, File originalArchive) {}
