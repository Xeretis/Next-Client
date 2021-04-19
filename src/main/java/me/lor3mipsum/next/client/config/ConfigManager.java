package me.lor3mipsum.next.client.config;


import com.google.gson.*;
import me.lor3mipsum.next.Next;
import me.lor3mipsum.next.client.gui.clickgui.GuiConfig;
import me.lor3mipsum.next.client.impl.settings.*;
import me.lor3mipsum.next.client.setting.Setting;
import me.lor3mipsum.next.client.module.Module;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ConfigManager {
    public final String rootDir = Next.CLIENT_NAME + "/";
    private final String backupDir = "Backups/";
    private final String mainDir = "Main/";
    private final String moduleDir = "Modules/";

    public void save() throws IOException {
        if (!Files.exists(Paths.get(rootDir)))
            Files.createDirectories(Paths.get(rootDir));
        if (!Files.exists(Paths.get(rootDir + backupDir)))
            Files.createDirectories(Paths.get(rootDir + backupDir));
        if (!Files.exists(Paths.get(rootDir + mainDir)))
            Files.createDirectories(Paths.get(rootDir + mainDir));
        if (!Files.exists(Paths.get(rootDir + moduleDir)))
            Files.createDirectories(Paths.get(rootDir + moduleDir));

        saveMetadata();
        saveModules();
        saveStates();
        saveClickGuiPositions();

    }

    private void registerFiles(String location, String name) throws IOException {
        if (Files.exists(Paths.get(rootDir + location + name + ".json"))) {
            File file = new File(rootDir + location + name + ".json");

            file.delete();

        }
        Files.createFile(Paths.get(rootDir + location + name + ".json"));
    }

    private void saveMetadata() throws IOException {
        registerFiles(mainDir, "Metadata");

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        OutputStreamWriter fileOutputStreamWriter = new OutputStreamWriter(new FileOutputStream(rootDir + mainDir + "Metadata" + ".json"), StandardCharsets.UTF_8);
        JsonObject mainObject = new JsonObject();

        mainObject.add("Version", new JsonPrimitive(Next.CLIENT_VERSION));

        String jsonString = gson.toJson(new JsonParser().parse(mainObject.toString()));
        fileOutputStreamWriter.write(jsonString);
        fileOutputStreamWriter.close();
    }

    private void saveModules() throws IOException {
        for (Module module : Next.INSTANCE.moduleManager.getModules()) {
            try {
                registerFiles(moduleDir, module.getName());

                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                OutputStreamWriter fileOutputStreamWriter = new OutputStreamWriter(new FileOutputStream(rootDir + moduleDir + module.getName() + ".json"), StandardCharsets.UTF_8);
                JsonObject moduleObject = new JsonObject();
                JsonObject settingObject = new JsonObject();
                moduleObject.add("Module", new JsonPrimitive(module.getName()));

                for (Setting setting : Next.INSTANCE.settingManager.getAllSettingsFrom(module.getName())) {
                    if (setting instanceof BooleanSetting)
                        settingObject.add(setting.name, new JsonPrimitive(((BooleanSetting) setting).isOn()));
                    if (setting instanceof NumberSetting)
                        settingObject.add(setting.name, new JsonPrimitive(((NumberSetting) setting).getNumber()));
                    if (setting instanceof ColorSetting)
                        settingObject.add(setting.name, new JsonPrimitive(((ColorSetting) setting).toInteger()));
                    if (setting instanceof ModeSetting)
                        settingObject.add(setting.name, new JsonPrimitive(((ModeSetting) setting).getMode()));
                    if (setting instanceof KeybindSetting)
                        settingObject.add(setting.name, new JsonPrimitive(((KeybindSetting) setting).getKey()));
                }
                moduleObject.add("Settings", settingObject);
                String jsonString = gson.toJson(new JsonParser().parse(moduleObject.toString()));
                fileOutputStreamWriter.write(jsonString);
                fileOutputStreamWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveStates() throws IOException {
        registerFiles(mainDir, "States");

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        OutputStreamWriter fileOutputStreamWriter = new OutputStreamWriter(new FileOutputStream(rootDir + mainDir + "States" + ".json"), StandardCharsets.UTF_8);
        JsonObject moduleObject = new JsonObject();
        JsonObject enabledObject = new JsonObject();

        for (Module module : Next.INSTANCE.moduleManager.getModules()) {

            enabledObject.add(module.getName(), new JsonPrimitive(module.getState()));
        }
        moduleObject.add("Modules", enabledObject);
        String jsonString = gson.toJson(new JsonParser().parse(moduleObject.toString()));
        fileOutputStreamWriter.write(jsonString);
        fileOutputStreamWriter.close();
    }

    private void saveClickGuiPositions() throws IOException {
        registerFiles(mainDir, "ClickGui");
        Next.INSTANCE.clickGui.gui.saveConfig(new GuiConfig(rootDir + mainDir));
    }

    public void load() {
        try {
            checkMetadata();
            loadModules();
            loadStates();
            loadClickGuiPositions();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void checkMetadata() throws IOException {
        String metadataLocation = rootDir + mainDir;

        if (!Files.exists(Paths.get(metadataLocation + "Metadata" + ".json")))
            return;

        InputStream inputStream = Files.newInputStream(Paths.get(rootDir + mainDir + "Metadata"+ ".json"));
        JsonObject metadataObject;
        try {
            metadataObject = new JsonParser().parse(new InputStreamReader(inputStream)).getAsJsonObject();
        } catch (java.lang.IllegalStateException e) {
            return;
        }
        if(metadataObject.get("Version") == null)
            return;

        JsonElement version = metadataObject.get("Version");

        if (version.isJsonPrimitive() && version.getAsDouble() == Next.CLIENT_VERSION)
            return;
        else
            backup("Version change");
    }

    private void loadModules() throws IOException {
        String moduleLocation = rootDir + moduleDir;

        for (Module module : Next.INSTANCE.moduleManager.getModules()) {
            try {
                if (!Files.exists(Paths.get(moduleLocation + module.getName() + ".json")))
                    return;

                InputStream inputStream = Files.newInputStream(Paths.get(moduleLocation + module.getName() + ".json"));
                JsonObject moduleObject;
                try {
                    moduleObject = new JsonParser().parse(new InputStreamReader(inputStream)).getAsJsonObject();
                } catch (java.lang.IllegalStateException e) {
                    return;
                }

                if (moduleObject.get("Module") == null)
                    return;

                JsonObject settingObject = moduleObject.get("Settings").getAsJsonObject();
                for (Setting setting : Next.INSTANCE.settingManager.getAllSettingsFrom(module.name)) {
                    JsonElement dataObject = settingObject.get(setting.name);
                    try {
                        if (dataObject != null && dataObject.isJsonPrimitive()) {
                            if (setting instanceof BooleanSetting) {
                                ((BooleanSetting) setting).setEnabled(dataObject.getAsBoolean());
                            } else if (setting instanceof NumberSetting) {
                                ((NumberSetting) setting).setNumber(dataObject.getAsDouble());
                            } else if (setting instanceof KeybindSetting) {
                                ((KeybindSetting) setting).setKey(dataObject.getAsInt());
                            } else if (setting instanceof ColorSetting) {
                                ((ColorSetting) setting).fromInteger(dataObject.getAsInt());
                            } else if (setting instanceof ModeSetting) {
                                ((ModeSetting) setting).setMode(dataObject.getAsString());
                            }
                        }
                    } catch (java.lang.NumberFormatException e) {
                        backup("Failed to load settings");
                        System.out.println(setting.name + " " + module.getName());
                        System.out.println(dataObject);
                    }
                }
                inputStream.close();
            } catch (IOException e) {
                backup("Failed to load modules");
                System.out.println(module.getName());
                e.printStackTrace();
            }
        }
    }

    private void loadStates() throws IOException {
        String enabledLocation = rootDir + mainDir;

        if (!Files.exists(Paths.get(enabledLocation + "States" + ".json")))
            return;

        InputStream inputStream = Files.newInputStream(Paths.get(enabledLocation + "States" + ".json"));
        JsonObject moduleObject = new JsonParser().parse(new InputStreamReader(inputStream)).getAsJsonObject();

        if (moduleObject.get("Modules") == null)
            return;

        JsonObject settingObject = moduleObject.get("Modules").getAsJsonObject();
        for (Module module : Next.INSTANCE.moduleManager.getModules()) {
            JsonElement dataObject = settingObject.get(module.getName());

            if (dataObject != null && dataObject.isJsonPrimitive()) {
                if (dataObject.getAsBoolean())
                    try {
                        module.setState(true);
                    } catch (NullPointerException e) {
                        backup("Failed to load state");
                        e.printStackTrace();
                    }
            }
        }
        inputStream.close();
    }

    private void loadClickGuiPositions() throws IOException {
        Next.INSTANCE.clickGui.gui.loadConfig(new GuiConfig(rootDir + mainDir));
    }

    private void backup(String backupReason) {
        System.out.println("Creating backup " + backupReason);

        try {
            if (!Files.exists(Paths.get(rootDir + backupDir)))
                Files.createDirectories(Paths.get(rootDir + backupDir));

            File out = new File(rootDir + backupDir, "backup_" + System.currentTimeMillis());
            out.mkdirs();

            File reason = new File(out, "reason.txt");
            reason.createNewFile();

            com.google.common.io.Files.write(backupReason.getBytes(StandardCharsets.UTF_8), reason);

            pack(rootDir + mainDir, out.getPath() + "/Main.zip");
            pack(rootDir + moduleDir, out.getPath() + "/Modules.zip");
        } catch (Exception e) {
            System.out.println("Failed to backup");
            e.printStackTrace();
        }
    }

    private void pack(String sourceDirPath, String zipFilePath) throws IOException {
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
                            System.err.println(e);
                        }
                    });
        }
    }
}
