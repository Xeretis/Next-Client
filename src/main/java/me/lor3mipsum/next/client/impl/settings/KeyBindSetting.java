package me.lor3mipsum.next.client.impl.settings;

import me.lor3mipsum.next.client.core.setting.Setting;

import java.util.function.Predicate;

public class KeyBindSetting extends Setting<Integer> {
    public KeyBindSetting(String name, int defaultVal) {
        super(name, defaultVal);
    }

    public KeyBindSetting(String name, int defaultVal, boolean visible) {
        super(name, defaultVal, visible);
    }

    public KeyBindSetting(String name, int defaultVal, boolean visible, Predicate<Integer> validator) {
        super(name, defaultVal, visible, validator);
    }
}
