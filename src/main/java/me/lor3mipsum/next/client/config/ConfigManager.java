package me.lor3mipsum.next.client.config;


import com.google.common.io.Files;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import me.lor3mipsum.next.Next;
import me.lor3mipsum.next.client.setting.Setting;
import net.fabricmc.loader.FabricLoader;
import me.lor3mipsum.next.client.module.Module;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ConfigManager {
    private final File clientDir = new File(String.valueOf(FabricLoader.INSTANCE.getGameDir()), Next.CLIENT_NAME);
    private final File backupDir = new File(clientDir, "backups");
    private final File saveFile = new File(clientDir, "client.json");

    public void save() throws Exception {
        clientDir.mkdir();

        if (!saveFile.exists() && !saveFile.createNewFile())
            throw new IOException("Failed to create " + saveFile.getAbsolutePath());

        Files.write(toJsonObject().toString().getBytes(StandardCharsets.UTF_8), saveFile);
    }

    private JsonObject toJsonObject() {
        System.out.println("Saving config");

        JsonObject obj = new JsonObject();

        {
            JsonObject metadata = new JsonObject();

            metadata.addProperty("clientVersion", Next.CLIENT_VERSION);

            obj.add("metadata", metadata);
        }

        {
            JsonObject modules = new JsonObject();

            for (Module module : Next.INSTANCE.moduleManager.getModules()) {
                JsonObject moduleObject = new JsonObject();

                moduleObject.addProperty("state", module.getState());

                modules.add(module.getName(), moduleObject);
            }

            obj.add("modules", modules);
        }

        {
            JsonObject settings = new JsonObject();

            for (Map.Entry<String, List<Setting>> stringListEntry : Next.INSTANCE.settingManager.getAllSettings().entrySet()) {
                JsonObject setting = new JsonObject();

                for (Setting setting1 : stringListEntry.getValue()) setting1.addToJsonObject(setting);

                settings.add(stringListEntry.getKey(), setting);
            }

            obj.add("settings", settings);
        }

        return obj;
    }

    public void load() {
        if (!saveFile.exists()) return;

        List<String> backupReasons = new ArrayList<>();

        try {
            JsonObject object = (JsonObject) new JsonParser().parse(new InputStreamReader(new FileInputStream(saveFile)));

            if (object.has("metadata")) {
                JsonElement metadataElement = object.get("metadata");

                if (metadataElement instanceof JsonObject) {
                    JsonObject metadata = (JsonObject) metadataElement;

                    JsonElement clientVersion = metadata.get("clientVersion");

                    if (clientVersion != null && clientVersion.isJsonPrimitive() && ((JsonPrimitive) clientVersion).isNumber()) {
                        double version = clientVersion.getAsDouble();

                        if (version > Next.CLIENT_VERSION) {
                            backupReasons.add("Version number of save file (" + version + ") is higher than " + Next.CLIENT_VERSION);
                        }
                        if (version < Next.CLIENT_VERSION) {
                            backupReasons.add("Version number of save file (" + version + ") is lower than " + Next.CLIENT_VERSION);
                        }
                    } else {
                        backupReasons.add("'clientVersion' object is not valid.");
                    }
                } else {
                    backupReasons.add("'metadata' object is not valid.");
                }

            } else {
                backupReasons.add("Save file has no metadata");
            }

            JsonElement modulesElement = object.get("modules");

            if (modulesElement instanceof JsonObject) {
                JsonObject modules = (JsonObject) modulesElement;

                for (Map.Entry<String, JsonElement> stringJsonElementEntry : modules.entrySet()) {
                    Module module = Next.INSTANCE.moduleManager.getModule(stringJsonElementEntry.getKey(), true);

                    if (module == null) {
                        backupReasons.add("Module '" + stringJsonElementEntry.getKey() + "' doesn't exist");
                        continue;
                    }

                    if (stringJsonElementEntry.getValue() instanceof JsonObject) {
                        JsonObject moduleObject = (JsonObject) stringJsonElementEntry.getValue();

                        JsonElement state = moduleObject.get("state");

                        if (state instanceof JsonPrimitive && ((JsonPrimitive) state).isBoolean()) {
                            module.setState(state.getAsBoolean());
                        } else {
                            backupReasons.add("'" + stringJsonElementEntry.getKey() + "/state' isn't valid");
                        }

                        JsonElement keybind = moduleObject.get("keybind");

                        if (keybind instanceof JsonPrimitive && ((JsonPrimitive) keybind).isNumber()) {
                            module.setKeybind(keybind.getAsInt());
                        } else {
                            backupReasons.add("'" + stringJsonElementEntry.getKey() + "/keybind' isn't valid");
                        }
                    } else {
                        backupReasons.add("Module object '" + stringJsonElementEntry.getKey() + "' isn't valid");
                    }
                }
            } else {
                backupReasons.add("'modules' object is not valid");
            }

            JsonElement settingsElement = object.get("settings");

            if (settingsElement instanceof JsonObject) {
                for (Map.Entry<String, JsonElement> stringJsonElementEntry : ((JsonObject) settingsElement).entrySet()) {
                    List<Setting> settings = Next.INSTANCE.settingManager.getAllSettingsFrom(stringJsonElementEntry.getKey());

                    if (settings == null) {
                        backupReasons.add("Setting owner '" + stringJsonElementEntry.getKey() + "' doesn't exist");
                        continue;
                    }

                    if (!stringJsonElementEntry.getValue().isJsonObject()) {
                        backupReasons.add("'settings/" + stringJsonElementEntry.getKey() + "' is not valid");
                        continue;
                    }

                    JsonObject settingObject = (JsonObject) stringJsonElementEntry.getValue();

                    for (Setting setting : settings) {
                        try {
                            setting.fromJsonObject(settingObject);
                        } catch (Exception e) {
                            backupReasons.add("Error while applying 'settings/" + stringJsonElementEntry.getKey() + "' " + e.toString());
                        }
                    }
                }
            } else {
                backupReasons.add("'settings' is not valid");
            }

            if (backupReasons.size() > 0) {
                backup(backupReasons);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void backup(List<String> backupReasons) {
        System.out.println("Creating backup " + backupReasons);

        try {
            backupDir.mkdirs();

            File out = new File(backupDir, "backup_" + System.currentTimeMillis() + ".zip");
            out.createNewFile();

            StringBuilder reason = new StringBuilder();

            for (String backupReason : backupReasons) {
                reason.append("- ").append(backupReason).append("\n");
            }

            ZipOutputStream outputStream = new ZipOutputStream(new FileOutputStream(out));

            outputStream.putNextEntry(new ZipEntry("client.json"));
            Files.copy(saveFile, outputStream);
            outputStream.closeEntry();

            outputStream.putNextEntry(new ZipEntry("reason.txt"));
            outputStream.write(reason.toString().getBytes(StandardCharsets.UTF_8));
            outputStream.closeEntry();

            outputStream.close();
        } catch (Exception e) {
            System.out.println("Failed to backup");
            e.printStackTrace();
        }
    }
}
