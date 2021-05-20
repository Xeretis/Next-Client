package me.lor3mipsum.next.api.config;

import me.lor3mipsum.next.Main;
import me.lor3mipsum.next.client.core.gui.GuiConfig;
import me.lor3mipsum.next.client.core.gui.NextGui;
import me.lor3mipsum.next.client.core.module.Module;
import me.lor3mipsum.next.client.core.setting.Setting;
import me.lor3mipsum.next.client.core.setting.SettingSeparator;
import me.lor3mipsum.next.client.core.social.Enemy;
import me.lor3mipsum.next.client.core.social.Friend;
import me.lor3mipsum.next.client.impl.settings.ColorSetting;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SaveConfig {

    public static String rootDir = Main.CLIENT_NAME + "/";
    private static final String backupDir = "Backups/";
    private static final String mainDir = "Main/";
    private static final String moduleDir = "Modules/";
    private static final String otherDir = "Other/";

    public static void save() {
        try {
            saveConfig();
            saveModules();
            saveFriends();
            saveEnemies();
            saveClientData();
            saveGuiPositions();
        } catch (IOException e) {
            Main.LOG.error("Config saving failed");
            Main.LOG.error(e.getMessage(), e);
        }
    }

    private static void saveConfig() throws IOException {
        if (!Files.exists(Paths.get(rootDir)))
            Files.createDirectories(Paths.get(rootDir));
        if (!Files.exists(Paths.get(rootDir + backupDir)))
            Files.createDirectories(Paths.get(rootDir + backupDir));
        if (!Files.exists(Paths.get(rootDir + moduleDir)))
            Files.createDirectories(Paths.get(rootDir + moduleDir));
        if (!Files.exists(Paths.get(rootDir + mainDir)))
            Files.createDirectories(Paths.get(rootDir + mainDir));
        if (!Files.exists(Paths.get(rootDir + otherDir)))
            Files.createDirectories(Paths.get(rootDir + otherDir));
    }

    private static void registerFiles(String location, String name) throws IOException {
        if (Files.exists(Paths.get(rootDir + location + name + ".yaml"))) {
            File file = new File(rootDir + location + name + ".yaml");

            file.delete();

        }
        Files.createFile(Paths.get(rootDir + location + name + ".yaml"));
    }

    private static void saveModules() throws IOException {
        DumperOptions options = new DumperOptions();
        options.setIndent(4);
        options.setPrettyFlow(true);

        Yaml yaml  = new Yaml(options);

        for (Module module : Main.moduleManager.getModules()) {
            OutputStreamWriter fileOutputStreamWriter = new OutputStreamWriter(new FileOutputStream(rootDir + moduleDir + module.getName() + ".yaml"), StandardCharsets.UTF_8);

            Map<String, Object> mainMap = new LinkedHashMap<>();

            mainMap.put("Enabled", module.getEnabled());
            mainMap.put("Drawn", module.getDrawn());
            mainMap.put("Bind", module.getBind());

            if (!Main.settingManager.getAllSettingsFrom(module).isEmpty()) {
                Map<String, Object> settingsMap = new LinkedHashMap<>();

                for (Setting setting : Main.settingManager.getAllSettingsFrom(module)) {
                    if (setting instanceof ColorSetting) {
                        Map<String, Object> colorMap = new LinkedHashMap<>();
                        colorMap.put("Rainbow", ((ColorSetting) setting).getRainbow());
                        colorMap.put("Value", ((ColorSetting) setting).getColor().getRGB());
                        settingsMap.put(setting.getName(), colorMap);
                    } else if (setting instanceof SettingSeparator)
                        continue;
                    else
                        settingsMap.put(setting.getName(), setting.getValue());
                }

                mainMap.put("Settings", settingsMap);
            }

            StringWriter writer = new StringWriter();
            yaml.dump(mainMap, writer);

            fileOutputStreamWriter.write(writer.toString());
            fileOutputStreamWriter.close();
        }
    }

    private static void saveFriends() throws IOException {
        registerFiles(otherDir, "Friends");

        DumperOptions options = new DumperOptions();
        options.setIndent(4);
        options.setPrettyFlow(true);

        Yaml yaml  = new Yaml(options);
        OutputStreamWriter fileOutputStreamWriter = new OutputStreamWriter(new FileOutputStream(rootDir + otherDir + "Friends" + ".yaml"), StandardCharsets.UTF_8);

        Map<String, Object> mainMap = new LinkedHashMap<>();

        List<Map<String, Object>> friends = new ArrayList<>();

        for (Friend friend : Main.socialManager.getFriends()) {
            Map<String, Object> friendMap = new LinkedHashMap<>();

            friendMap.put("Name", friend.getName());
            friendMap.put("Level", friend.getLevel());

            friends.add(friendMap);
        }

        mainMap.put("Friends", friends);

        StringWriter writer = new StringWriter();
        yaml.dump(mainMap, writer);

        fileOutputStreamWriter.write(writer.toString());
        fileOutputStreamWriter.close();
    }

    private static void saveEnemies() throws IOException {
        registerFiles(otherDir, "Enemies");

        DumperOptions options = new DumperOptions();
        options.setIndent(4);
        options.setPrettyFlow(true);

        Yaml yaml  = new Yaml(options);
        OutputStreamWriter fileOutputStreamWriter = new OutputStreamWriter(new FileOutputStream(rootDir + otherDir + "Enemies" + ".yaml"), StandardCharsets.UTF_8);

        Map<String, Object> mainMap = new LinkedHashMap<>();

        List<Map<String, Object>> enemies = new ArrayList<>();

        for (Enemy enemy : Main.socialManager.getEnemies()) {
            Map<String, Object> enemyMap = new LinkedHashMap<>();

            enemyMap.put("Name", enemy.getName());
            enemyMap.put("Level", enemy.getLevel());

            enemies.add(enemyMap);
        }

        mainMap.put("Enemies", enemies);

        StringWriter writer = new StringWriter();
        yaml.dump(mainMap, writer);

        fileOutputStreamWriter.write(writer.toString());
        fileOutputStreamWriter.close();
    }

    private static void saveGuiPositions() throws IOException {
        registerFiles(mainDir, "GuiPanels");
        NextGui.gui.saveConfig(new GuiConfig(rootDir + mainDir));
    }

    private static void saveClientData() throws IOException {
        registerFiles(mainDir, "ClientData");

        DumperOptions options = new DumperOptions();
        options.setIndent(4);
        options.setPrettyFlow(true);

        Yaml yaml  = new Yaml(options);
        OutputStreamWriter fileOutputStreamWriter = new OutputStreamWriter(new FileOutputStream(rootDir + mainDir + "ClientData" + ".yaml"), StandardCharsets.UTF_8);

        Map<String, Object> mainMap = new LinkedHashMap<>();

        mainMap.put("Name", Main.CLIENT_NAME);
        mainMap.put("Version", Main.CLIENT_VERSION);
        mainMap.put("Prefix", Main.prefix);

        StringWriter writer = new StringWriter();
        yaml.dump(mainMap, writer);

        fileOutputStreamWriter.write(writer.toString());
        fileOutputStreamWriter.close();
    }

}
