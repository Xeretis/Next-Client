package me.lor3mipsum.next.client.impl.settings;

import me.lor3mipsum.next.client.core.setting.Setting;

import java.util.function.Predicate;

public class StringSetting extends Setting<String> {
    public StringSetting(String name, String defaultVal) {
        super(name, defaultVal);
    }

    public StringSetting(String name, String defaultVal, boolean visible) {
        super(name, defaultVal, visible);
    }

    public StringSetting(String name, String defaultVal, boolean visible, Predicate<String> validator) {
        super(name, defaultVal, visible, validator);
    }
}
