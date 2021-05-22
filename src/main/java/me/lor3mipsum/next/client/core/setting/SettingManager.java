package me.lor3mipsum.next.client.core.setting;

import me.lor3mipsum.next.Main;
import me.lor3mipsum.next.client.core.module.Module;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SettingManager {
    private final HashMap<String, List<Setting>> settingMap;

    public SettingManager() {
        settingMap = new HashMap<>();
    }

    public void registerObject(String name, Object object) {
        List<Setting> values = new ArrayList<>();
        for (final Field field : object.getClass().getDeclaredFields()) {
            try {
                field.setAccessible(true);
                final Object obj = field.get(object);

                if (obj instanceof Setting) {
                    values.add((Setting) obj);
                }
            } catch (IllegalAccessException e) {
                Main.LOG.error("Registering settings failed");
                Main.LOG.error(e.getMessage(), e);
            }
        }
        settingMap.put(name, values);
    }

    public void registerSetting(String name, Setting value) {
        if (!settingMap.containsKey(name))
            settingMap.put(name, new ArrayList<>());

        settingMap.get(name).add(value);
    }

    public void registerSetting(Object module, Setting value) {
        if (module instanceof Module)
            if (!settingMap.containsKey(((Module) module).getName()))
                settingMap.put(((Module) module).getName(), new ArrayList<>());

        settingMap.get(((Module) module).getName()).add(value);
    }

    public List<Setting> getAllSettingsFrom(String name) {
        for (Map.Entry<String, List<Setting>> stringListEntry : settingMap.entrySet()) {
            if (stringListEntry.getKey().equalsIgnoreCase(name)) return stringListEntry.getValue();
        }
        return null;
    }

    public List<Setting> getAllSettingsFrom(Object module) {
        if (module instanceof Module)
            for (Map.Entry<String, List<Setting>> stringListEntry : settingMap.entrySet()) {
                if (stringListEntry.getKey().equalsIgnoreCase(((Module) module).getName())) return stringListEntry.getValue();
            }
        return null;
    }

    public HashMap<String, List<Setting>> getAllSettings() {
        return settingMap;
    }

    public Setting get(String owner, String name) {
        List<Setting> found = getAllSettingsFrom(owner);

        if (found == null) return null;

        return found.stream().filter(val -> val.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public Setting get(Object module, String name) {
        List<Setting> found = getAllSettingsFrom(module);

        if (found == null) return null;

        return found.stream().filter(val -> val.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public Setting getByConfigName(String owner, String name) {
        List<Setting> found = getAllSettingsFrom(owner);

        if (found == null) return null;

        return found.stream().filter(val -> val.getConfigName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public Setting getByConfigName(Object module, String name) {
        List<Setting> found = getAllSettingsFrom(module);

        if (found == null) return null;

        return found.stream().filter(val -> val.getConfigName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }
}
