package me.lor3mipsum.next.client.core.setting;

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
                e.printStackTrace();
            }
        }
        settingMap.put(name, values);
    }

    public List<Setting> getAllSettingsFrom(String name) {
        for (Map.Entry<String, List<Setting>> stringListEntry : settingMap.entrySet()) {
            if (stringListEntry.getKey().equalsIgnoreCase(name)) return stringListEntry.getValue();
        }
        return null;
    }

    public List<Setting> getAllSettingsFrom(Class<? extends Module> module) {
        for (Map.Entry<String, List<Setting>> stringListEntry : settingMap.entrySet()) {
            if (stringListEntry.getKey().equalsIgnoreCase(module.getName())) return stringListEntry.getValue();
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

    public Setting get(Class<? extends Module> module, String name) {
        List<Setting> found = getAllSettingsFrom(module.getName());

        if (found == null) return null;

        return found.stream().filter(val -> val.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }
}
