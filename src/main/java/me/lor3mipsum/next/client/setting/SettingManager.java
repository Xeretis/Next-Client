package me.lor3mipsum.next.client.setting;

import java.lang.reflect.Field;
import java.util.*;

public class SettingManager {
    private static HashMap<String, List<Setting>> settingMap;

    public static void init() {
        settingMap = new HashMap<>();
    }

    public static void registerObject(String name, Object object) {
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

    public static List<Setting> getAllSettingsFrom(String name) {
        for (Map.Entry<String, List<Setting>> stringListEntry : settingMap.entrySet()) {
            if (stringListEntry.getKey().equalsIgnoreCase(name)) return stringListEntry.getValue();
        }
        return null;
    }

    public static HashMap<String, List<Setting>> getAllSettings() {
        return settingMap;
    }

    public static Setting get(String owner, String name) {
        List<Setting> found = getAllSettingsFrom(owner);

        if (found == null) return null;

        return found.stream().filter(val -> name.equalsIgnoreCase(val.name)).findFirst().orElse(null);
    }
}
