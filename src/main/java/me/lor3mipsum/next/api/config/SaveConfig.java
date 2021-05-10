package me.lor3mipsum.next.api.config;

import me.lor3mipsum.next.Main;
import me.lor3mipsum.next.client.core.gui.GuiConfig;
import me.lor3mipsum.next.client.core.gui.NextGui;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
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
            saveClientData();
            saveGuiPositions();
        } catch (IOException e) {
            Main.LOG.error("Config saving failed");
            Main.LOG.error(e.getStackTrace());
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

    private static void saveGuiPositions() throws IOException {
        registerFiles(mainDir, "GuiPanels");
        NextGui.gui.saveConfig(new GuiConfig(rootDir + mainDir));
    }

    private static void saveClientData() throws IOException {
        registerFiles(mainDir, "ClientData");

        Yaml yaml  = new Yaml();
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
