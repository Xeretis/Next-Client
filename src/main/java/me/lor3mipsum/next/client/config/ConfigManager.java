package me.lor3mipsum.next.client.config;


import com.google.gson.*;
import me.lor3mipsum.next.Next;
import me.lor3mipsum.next.client.command.macro.Macro;
import me.lor3mipsum.next.client.gui.clickgui.GuiConfig;
import me.lor3mipsum.next.client.impl.settings.*;
import me.lor3mipsum.next.client.setting.Setting;
import me.lor3mipsum.next.client.module.Module;
import me.lor3mipsum.next.client.setting.SettingManager;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ConfigManager {
    public static String rootDir;
    private static String backupDir;
    private static String mainDir;
    private static String moduleDir;
    private static String otherDir;

    public static void init() {
        rootDir = Next.CLIENT_NAME + "/";
        backupDir = "Backups/";
        mainDir = "Main/";
        moduleDir = "Modules/";
        otherDir = "Other/";
    }

    public static void save() throws IOException {
        if (!Files.exists(Paths.get(rootDir)))
            Files.createDirectories(Paths.get(rootDir));
        if (!Files.exists(Paths.get(rootDir + backupDir)))
            Files.createDirectories(Paths.get(rootDir + backupDir));
        if (!Files.exists(Paths.get(rootDir + mainDir)))
            Files.createDirectories(Paths.get(rootDir + mainDir));
        if (!Files.exists(Paths.get(rootDir + moduleDir)))
            Files.createDirectories(Paths.get(rootDir + moduleDir));
        if (!Files.exists(Paths.get(rootDir + otherDir)))
            Files.createDirectories(Paths.get(rootDir + otherDir));

        saveMetadata();
        saveClientData();
        saveModules();
        saveStates();
        saveMacros();
        saveClickGuiPositions();

    }

    private static void registerFiles(String location, String name) throws IOException {
        if (Files.exists(Paths.get(rootDir + location + name + ".json"))) {
            File file = new File(rootDir + location + name + ".json");

            file.delete();

        }
        Files.createFile(Paths.get(rootDir + location + name + ".json"));
    }

    private static void saveMetadata() throws IOException {
        registerFiles(mainDir, "Metadata");

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        OutputStreamWriter fileOutputStreamWriter = new OutputStreamWriter(new FileOutputStream(rootDir + mainDir + "Metadata" + ".json"), StandardCharsets.UTF_8);
        JsonObject mainObject = new JsonObject();

        mainObject.add("Version", new JsonPrimitive(Next.CLIENT_VERSION));

        String jsonString = gson.toJson(new JsonParser().parse(mainObject.toString()));
        fileOutputStreamWriter.write(jsonString);
        fileOutputStreamWriter.close();
    }

    private static void saveClientData() throws IOException {
        registerFiles(mainDir, "Client");

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        OutputStreamWriter fileOutputStreamWriter = new OutputStreamWriter(new FileOutputStream(rootDir + mainDir + "Client" + ".json"), StandardCharsets.UTF_8);
        JsonObject mainObject = new JsonObject();

        mainObject.add("Prefix", new JsonPrimitive(Next.prefix));

        String jsonString = gson.toJson(new JsonParser().parse(mainObject.toString()));
        fileOutputStreamWriter.write(jsonString);
        fileOutputStreamWriter.close();
    }

    private static void saveModules() throws IOException {
        for (Module module : Next.INSTANCE.moduleManager.getModules()) {
            try {
                registerFiles(moduleDir, module.getName());

                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                OutputStreamWriter fileOutputStreamWriter = new OutputStreamWriter(new FileOutputStream(rootDir + moduleDir + module.getName() + ".json"), StandardCharsets.UTF_8);
                JsonObject moduleObject = new JsonObject();
                JsonObject settingObject = new JsonObject();
                moduleObject.add("Module", new JsonPrimitive(module.getName()));

                for (Setting setting : SettingManager.getAllSettingsFrom(module.getName())) {
                    if (setting instanceof BooleanSetting)
                        settingObject.add(setting.getName(), new JsonPrimitive(((BooleanSetting) setting).isOn()));
                    else if (setting instanceof NumberSetting)
                        settingObject.add(setting.getName(), new JsonPrimitive(((NumberSetting) setting).getNumber()));
                    else if (setting instanceof ColorSetting) {
                        JsonObject colorObject = new JsonObject();
                        colorObject.add("color", new JsonPrimitive(((ColorSetting) setting).toInteger()));
                        colorObject.add("rainbow", new JsonPrimitive(((ColorSetting) setting).getRainbow()));
                        settingObject.add(setting.getName(), colorObject);;
                    } else if (setting instanceof ModeSetting)
                        settingObject.add(setting.getName(), new JsonPrimitive(((ModeSetting) setting).getMode()));
                    else if (setting instanceof KeybindSetting)
                        settingObject.add(setting.getName(), new JsonPrimitive(((KeybindSetting) setting).getKey()));
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

    private static void saveStates() throws IOException {
        registerFiles(otherDir, "States");

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        OutputStreamWriter fileOutputStreamWriter = new OutputStreamWriter(new FileOutputStream(rootDir + otherDir + "States" + ".json"), StandardCharsets.UTF_8);
        JsonObject moduleObject = new JsonObject();
        JsonObject enabledObject = new JsonObject();

        for (Module module : Next.INSTANCE.moduleManager.getModules())
            enabledObject.add(module.getName(), new JsonPrimitive(module.getState()));

        moduleObject.add("Modules", enabledObject);
        String jsonString = gson.toJson(new JsonParser().parse(moduleObject.toString()));
        fileOutputStreamWriter.write(jsonString);
        fileOutputStreamWriter.close();
    }

    private static void saveMacros() throws IOException {
        registerFiles(otherDir, "Macros");

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        OutputStreamWriter fileOutputStreamWriter = new OutputStreamWriter(new FileOutputStream(rootDir + otherDir + "Macros" + ".json"), StandardCharsets.UTF_8);
        JsonObject mainObject = new JsonObject();
        JsonObject macroObject = new JsonObject();

        for (Macro macro : Next.INSTANCE.macroManager.getMacros())
            macroObject.add(String.valueOf(macro.key), new JsonPrimitive(macro.command));

        mainObject.add("Macros", macroObject);
        String jsonString = gson.toJson(new JsonParser().parse(mainObject.toString()));
        fileOutputStreamWriter.write(jsonString);
        fileOutputStreamWriter.close();
    }

    private static void saveClickGuiPositions() throws IOException {
        registerFiles(mainDir, "ClickGui");
        Next.INSTANCE.clickGui.gui.saveConfig(new GuiConfig(rootDir + mainDir));
    }

    public static void load() {
        System.out.println("Loading config");
        try {
            checkMetadata();
            loadClientData();
            loadModules();
            loadStates();
            loadMacros();
            loadClickGuiPositions();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void checkMetadata() throws IOException {
        String metadataLocation = rootDir + mainDir;

        if (!Files.exists(Paths.get(metadataLocation + "Metadata" + ".json")))
            return;

        InputStream inputStream = Files.newInputStream(Paths.get(metadataLocation + "Metadata"+ ".json"));
        JsonObject metadataObject;
        try {
            metadataObject = new JsonParser().parse(new InputStreamReader(inputStream)).getAsJsonObject();
        } catch (java.lang.IllegalStateException e) {
            backup("Couldn't check version");
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

    private static void loadClientData() throws IOException {
        String clientDataLocation = rootDir + mainDir;

        if (!Files.exists(Paths.get(clientDataLocation + "Client" + ".json")))
            return;

        InputStream inputStream = Files.newInputStream(Paths.get(clientDataLocation + "Client"+ ".json"));
        JsonObject clientDataObject;
        try {
            clientDataObject = new JsonParser().parse(new InputStreamReader(inputStream)).getAsJsonObject();
        } catch (java.lang.IllegalStateException e) {
            backup("Couldn't load client data");
            return;
        }

        if(clientDataObject.get("Prefix") == null)
            return;

        JsonElement prefix = clientDataObject.get("Prefix");

        Next.prefix = prefix.getAsString();

    }

    private static void loadModules() throws IOException {
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
                    backup("Couldn't load modules");
                    return;
                }

                if (moduleObject.get("Module") == null)
                    return;

                JsonObject settingObject = moduleObject.get("Settings").getAsJsonObject();
                for (Setting setting : SettingManager.getAllSettingsFrom(module.name)) {
                    JsonElement dataObject = settingObject.get(setting.getName());
                    try {
                        if (dataObject != null) {
                            if (setting instanceof BooleanSetting)
                                ((BooleanSetting) setting).setEnabled(dataObject.getAsBoolean());
                            else if (setting instanceof NumberSetting)
                                ((NumberSetting) setting).setNumber(dataObject.getAsDouble());
                            else if (setting instanceof KeybindSetting)
                                ((KeybindSetting) setting).setKey(dataObject.getAsInt());
                            else if (setting instanceof ColorSetting) {
                                ((ColorSetting) setting).setRainbow(dataObject.getAsJsonObject().get("rainbow").getAsBoolean());
                                ((ColorSetting) setting).fromInteger(dataObject.getAsJsonObject().get("color").getAsInt());
                            } else if (setting instanceof ModeSetting)
                                ((ModeSetting) setting).setMode(dataObject.getAsString());
                        }
                    } catch (Exception e) {
                        backup("Failed to load settings");
                        System.out.println(setting.getName() + " " + module.getName());
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

    private static void loadStates() throws IOException {
        String satesLocation = rootDir + otherDir;

        if (!Files.exists(Paths.get(satesLocation + "States" + ".json")))
            return;

        InputStream inputStream = Files.newInputStream(Paths.get(satesLocation + "States" + ".json"));
        JsonObject moduleObject;
        try {
            moduleObject = new JsonParser().parse(new InputStreamReader(inputStream)).getAsJsonObject();
        } catch(java.lang.IllegalStateException e) {
            backup("Failed to load states");
            return;
        }

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

    private static void loadMacros() throws IOException {
        String macrosLocation = rootDir + otherDir;

        if (!Files.exists(Paths.get(macrosLocation + "Macros" + ".json")))
            return;

        InputStream inputStream = Files.newInputStream(Paths.get(macrosLocation + "Macros" + ".json"));
        JsonObject mainObject;
        try {
            mainObject = new JsonParser().parse(new InputStreamReader(inputStream)).getAsJsonObject();
        } catch(java.lang.IllegalStateException e) {
            backup("Failed to load macros");
            return;
        }

        if (mainObject.get("Macros") == null)
            return;

        JsonObject macroObject = mainObject.get("Macros").getAsJsonObject();
        if (macroObject != null)
            for (Map.Entry<String, JsonElement> entry : macroObject.entrySet()) {
                try {
                    Next.INSTANCE.macroManager.addMacro(new Macro(Integer.parseInt(entry.getKey().toString()), entry.getValue().getAsString()));
                } catch (Exception e) {
                    backup("Failed to load macro value");
                }
            }
        else
            backup("Failed to load macro values");

        inputStream.close();
    }

    private static void loadClickGuiPositions() throws IOException {
        Next.INSTANCE.clickGui.gui.loadConfig(new GuiConfig(rootDir + mainDir));
    }

    private static void backup(String backupReason) {
        System.out.println("Creating backup " + backupReason);

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
            System.out.println("Failed to backup");
            e.printStackTrace();
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
                            System.err.println(e);
                        }
                    });
        }
    }
}
