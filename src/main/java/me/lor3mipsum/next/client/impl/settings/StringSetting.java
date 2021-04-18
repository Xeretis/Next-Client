package me.lor3mipsum.next.client.impl.settings;

import me.lor3mipsum.next.client.setting.Setting;

import java.util.function.Predicate;

public class StringSetting extends Setting<String> {

    public StringSetting(String name, String defaultVal) {
        this(name, defaultVal, null);
    }

    public StringSetting(String name, String defaultVal, Predicate<String> validator) {
        super(name, defaultVal, validator);
    }
}
