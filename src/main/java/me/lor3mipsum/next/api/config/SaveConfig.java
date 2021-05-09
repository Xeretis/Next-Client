package me.lor3mipsum.next.api.config;

import me.lor3mipsum.next.Main;
import me.lor3mipsum.next.client.core.gui.GuiConfig;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class SaveConfig {

    public static String rootDir = Main.CLIENT_NAME + "/";
    private static String backupDir = "Backups/";
    private static String mainDir = "Main/";
    private static String moduleDir = "Modules/";
    private static String otherDir = "Other/";

    public static void save() {
        try {
            saveConfig();
            saveGuiPositions();
        } catch (IOException e) {
            Main.LOG.error("Config saving failed");
            Main.LOG.error(e.getStackTrace());
        }
    }

    private static void saveConfig() throws IOException {
        if (!Files.exists(Paths.get(rootDir))) {
            Files.createDirectories(Paths.get(rootDir));
        }
        if (!Files.exists(Paths.get(rootDir + moduleDir))) {
            Files.createDirectories(Paths.get(rootDir + moduleDir));
        }
        if (!Files.exists(Paths.get(rootDir + mainDir))) {
            Files.createDirectories(Paths.get(rootDir + mainDir));
        }
        if (!Files.exists(Paths.get(rootDir + otherDir))) {
            Files.createDirectories(Paths.get(rootDir + otherDir));
        }
    }

    private static void registerFiles(String location, String name) throws IOException {
        if (Files.exists(Paths.get(rootDir + location + name + ".yaml"))) {
            File file = new File(rootDir + location + name + ".yaml");

            file.delete();

        }
        Files.createFile(Paths.get(rootDir + location + name + ".yaml"));
    }

    private static void saveGuiPositions() throws IOException {
        registerFiles(mainDir, "GuiPanels");
        Main.clickGui.gui.saveConfig(new GuiConfig(rootDir + mainDir));
    }

}
