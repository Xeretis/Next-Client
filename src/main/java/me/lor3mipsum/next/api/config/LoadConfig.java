package me.lor3mipsum.next.api.config;

import me.lor3mipsum.next.Main;
import me.lor3mipsum.next.api.util.misc.NextColor;
import me.lor3mipsum.next.client.core.command.macro.Macro;
import me.lor3mipsum.next.client.core.gui.GuiConfig;
import me.lor3mipsum.next.client.core.gui.NextGui;
import me.lor3mipsum.next.client.core.module.Module;
import me.lor3mipsum.next.client.core.setting.Setting;
import me.lor3mipsum.next.client.core.setting.SettingSeparator;
import me.lor3mipsum.next.client.core.social.Enemy;
import me.lor3mipsum.next.client.core.social.Friend;
import me.lor3mipsum.next.client.impl.settings.ColorSetting;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

public class LoadConfig {

    public static String rootDir = Main.CLIENT_NAME + "/";
    private static final String backupDir = "Backups/";
    private static final String mainDir = "Main/";
    private static final String moduleDir = "Modules/";
    private static final String otherDir = "Other/";

    public static void load() {
        try {
            loadModules();
            loadFriends();
            loadEnemies();
            loadMacros();
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
                        } else if (setting instanceof SettingSeparator)
                            continue;
                        else
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

    private static void loadFriends() throws IOException {
        Yaml yaml = new Yaml();
        String friendsLocation = rootDir + otherDir;

        if (!Files.exists(Paths.get(friendsLocation + "Friends" + ".yaml")))
            return;

        InputStream inputStream = Files.newInputStream(Paths.get(friendsLocation + "Friends" + ".yaml"));

        try {

            Map<String, Object> mainMap = yaml.load(inputStream);

            mainMap.forEach((name, map) -> {
                Main.socialManager.addFriend(new Friend(name, (int) ((Map<String, Object>) map).get("Level")));
            });

        } catch (Exception e) {
            Backup.backup("Failed to load friends");
            Main.LOG.error("Failed to load friends");
            Main.LOG.error(e.getMessage(), e);
        }
    }

    private static void loadEnemies() throws IOException {
        Yaml yaml = new Yaml();
        String enemiesLocation = rootDir + otherDir;

        if (!Files.exists(Paths.get(enemiesLocation + "Enemies" + ".yaml")))
            return;

        InputStream inputStream = Files.newInputStream(Paths.get(enemiesLocation + "Enemies" + ".yaml"));

        try {

            Map<String, Object> mainMap = yaml.load(inputStream);

            mainMap.forEach((name, map) -> {
                Main.socialManager.addEnemy(new Enemy(name, (int) ((Map<String, Object>) map).get("Level")));
            });

        } catch (Exception e) {
            Backup.backup("Failed to load enemies");
            Main.LOG.error("Failed to load enemies");
            Main.LOG.error(e.getMessage(), e);
        }
    }

    private static void loadGuiPositions() throws IOException {
        NextGui.gui.loadConfig(new GuiConfig(rootDir + mainDir));
    }

    private static void loadMacros() throws IOException {
        Yaml yaml = new Yaml();
        String macrosLocation = rootDir + otherDir;

        if(!Files.exists(Paths.get(macrosLocation + "Macros" + ".yaml")))
            return;

        InputStream inputStream = Files.newInputStream(Paths.get(macrosLocation + "Macros" + ".yaml"));

        try {

            Map<String, Object> mainMap = yaml.load(inputStream);

            mainMap.forEach((name, map) -> {
                Map<String, Object> macroMap = (Map<String, Object>) map;
                Main.macroManager.addMacro(new Macro(name, (int) macroMap.get("Key"), (String) macroMap.get("Command")));
            });

        } catch (Exception e) {
            Backup.backup("Failed to load macros");
            Main.LOG.error("Failed to load macros");
            Main.LOG.error(e.getMessage(), e);
        }
    }

    private static void loadClientData() throws IOException {
        Yaml yaml = new Yaml();
        String clientDataLocation = rootDir + mainDir;

        if(!Files.exists(Paths.get(clientDataLocation + "ClientData" + ".yaml")))
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
