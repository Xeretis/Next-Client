package me.lor3mipsum.next.api.config;

import me.lor3mipsum.next.Main;
import me.lor3mipsum.next.api.util.misc.NextColor;
import me.lor3mipsum.next.client.core.gui.GuiConfig;
import me.lor3mipsum.next.client.core.gui.NextGui;
import me.lor3mipsum.next.client.core.module.Module;
import me.lor3mipsum.next.client.core.setting.Setting;
import me.lor3mipsum.next.client.impl.settings.ColorSetting;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class LoadConfig {

    public static String rootDir = Main.CLIENT_NAME + "/";
    private static final String backupDir = "Backups/";
    private static final String mainDir = "Main/";
    private static final String moduleDir = "Modules/";
    private static final String otherDir = "Other/";

    public static void load() {
        try {
            loadModules();
            loadClientData();
            loadGuiPositions();
        } catch (IOException e) {
            Main.LOG.error("Config loading failed");
            Main.LOG.error(e.getMessage(), e);
        }
    }

    private static void loadModules() throws IOException {
        Yaml yaml = new Yaml();
        String modulesLocation = rootDir + moduleDir;

        if (!Files.exists(Paths.get(modulesLocation)))
            return;

        for (Module module : Main.moduleManager.getModules()) {
            if (!Files.exists(Paths.get(modulesLocation + module.getName() + ".yaml")))
                return;

            InputStream inputStream = Files.newInputStream(Paths.get(modulesLocation + module.getName() + ".yaml"));

            try {
                Map<String, Object> mainMap = yaml.load(inputStream);

                module.setEnabled( (boolean) mainMap.get("Enabled"));
                module.setDrawn( (boolean) mainMap.get("Drawn"));
                module.setBind( (int) mainMap.get("Bind"));

                if (mainMap.get("Settings") != null) {
                    Map<String, Object> settingsMap = (Map<String, Object>) mainMap.get("Settings");

                    for (Setting setting : Main.settingManager.getAllSettingsFrom(module)) {
                        if (settingsMap.get(setting.getName()) == null)
                            continue;

                        if (setting instanceof ColorSetting) {
                            Map<String, Object> colorMap = (Map<String, Object>) settingsMap.get(setting.getName());
                            ((ColorSetting) setting).setRainbow((boolean) colorMap.get("Rainbow"));
                            setting.setValue(new NextColor((int) colorMap.get("Value"), true));
                        } else
                            setting.setValue(settingsMap.get(setting.getName()));
                    }
                }
            }catch (Exception e) {
                Backup.backup("Failed to load module '" + module.getName() + "'");
                Main.LOG.error("Failed to load module '" + module.getName() + "'");
                Main.LOG.error(e.getMessage(), e);
            }
        }
    }

    private static void loadGuiPositions() throws IOException {
        NextGui.gui.loadConfig(new GuiConfig(rootDir + mainDir));
    }

    private static void loadClientData() throws IOException {
        Yaml yaml = new Yaml();
        String clientDataLocation = rootDir + mainDir;

        if(!Files.exists(Paths.get(clientDataLocation)))
            return;

        InputStream inputStream = Files.newInputStream(Paths.get(clientDataLocation + "ClientData" + ".yaml"));

        try {
            Map<String, Object> mainMap = yaml.load(inputStream);

            if (!mainMap.get("Name").equals(Main.CLIENT_NAME))
                Backup.backup("Wtf did you do ...?");

            if(!mainMap.get("Version").equals(Main.CLIENT_VERSION))
                Backup.backup("Version change");

            if (mainMap.get("Prefix") != null)
                Main.prefix = (String) mainMap.get("Prefix");
        } catch (Exception e) {
            Backup.backup("Failed to load client data");
            Main.LOG.error("Failed to load client data");
            Main.LOG.error(e.getMessage(), e);
        }
    }
}
