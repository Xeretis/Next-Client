package me.lor3mipsum.next.client.config;


import com.google.common.io.Files;
import com.google.gson.JsonObject;
import me.lor3mipsum.next.Next;
import me.lor3mipsum.next.client.setting.Setting;
import net.fabricmc.loader.FabricLoader;
import me.lor3mipsum.next.client.module.Module;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class ConfigManager {
    private final File clientDir = new File(String.valueOf(FabricLoader.INSTANCE.getConfigDir()), Next.CLIENT_NAME);
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
                moduleObject.addProperty("keybind", module.getKeybind());

                modules.add(module.getName(), moduleObject);
            }

            obj.add("modules", modules);
        }

        {
            JsonObject values = new JsonObject();

            for (Map.Entry<String, List<Setting>> stringListEntry : Next.INSTANCE.settingManager.getAllSettings().entrySet()) {
                JsonObject value = new JsonObject();

                for (Setting value1 : stringListEntry.getValue()) value1.addToJsonObject(value);

                values.add(stringListEntry.getKey(), value);
            }

            obj.add("values", values);
        }

        return obj;
    }
}
