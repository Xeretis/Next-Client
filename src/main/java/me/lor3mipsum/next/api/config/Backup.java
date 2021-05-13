package me.lor3mipsum.next.api.config;

import me.lor3mipsum.next.Main;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Backup {

    public static String rootDir = Main.CLIENT_NAME + "/";
    private static final String backupDir = "Backups/";
    private static final String mainDir = "Main/";
    private static final String moduleDir = "Modules/";
    private static final String otherDir = "Other/";

    public static void backup(String backupReason) {
        Main.LOG.warn("Creating backup '" + backupReason + "'");

        try {
            if (!Files.exists(Paths.get(rootDir + backupDir)))
                Files.createDirectories(Paths.get(rootDir + backupDir));

            File out = new File(rootDir + backupDir, "backup_" + System.currentTimeMillis());

            out.mkdirs();

            File reason = new File(out, "Reason.txt");
            reason.createNewFile();

            com.google.common.io.Files.write(backupReason.getBytes(StandardCharsets.UTF_8), reason);

            pack(rootDir + mainDir, out.getPath() + "/Main.zip");
            pack(rootDir + moduleDir, out.getPath() + "/Modules.zip");
        } catch (Exception e) {
            Main.LOG.error("Failed to backup");
            Main.LOG.error(e.getMessage(), e);
        }
    }

    private static void pack(String sourceDirPath, String zipFilePath) throws IOException {
        Path p = Files.createFile(Paths.get(zipFilePath));
        try (ZipOutputStream zs = new ZipOutputStream(Files.newOutputStream(p))) {
            Path pp = Paths.get(sourceDirPath);
            Files.walk(pp)
                    .filter(path -> !Files.isDirectory(path))
                    .forEach(path -> {
                        ZipEntry zipEntry = new ZipEntry(pp.relativize(path).toString());
                        try {
                            zs.putNextEntry(zipEntry);
                            Files.copy(path, zs);
                            zs.closeEntry();
                        } catch (IOException e) {
                            Main.LOG.error("Failed to zip backup");
                            Main.LOG.error(e.getMessage(), e);
                        }
                    });
        }
    }
}
