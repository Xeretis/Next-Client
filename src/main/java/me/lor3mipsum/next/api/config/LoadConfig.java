package me.lor3mipsum.next.api.config;

import me.lor3mipsum.next.Main;
import me.lor3mipsum.next.client.core.gui.GuiConfig;

import java.io.IOException;

public class LoadConfig {

    public static String rootDir = Main.CLIENT_NAME + "/";
    private static String backupDir = "Backups/";
    private static String mainDir = "Main/";
    private static String moduleDir = "Modules/";
    private static String otherDir = "Other/";

    public static void load() {
        try {
            loadGuiPositions();
        } catch (IOException e) {
            Main.LOG.error("Failed to load config");
        }
    }

    private static void loadGuiPositions() throws IOException {
        Main.clickGui.gui.loadConfig(new GuiConfig(rootDir + mainDir));
    }

}
