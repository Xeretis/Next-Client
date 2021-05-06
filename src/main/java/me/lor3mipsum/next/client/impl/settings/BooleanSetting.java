package me.lor3mipsum.next.client.impl.settings;

import me.lor3mipsum.next.client.core.setting.Setting;

public class BooleanSetting extends Setting<Boolean> {
    public BooleanSetting(String name, Boolean defaultValue) {
        super(name, defaultValue);
    }

    public BooleanSetting(String name, Boolean defaultValue, boolean visible) {
        super(name, defaultValue, visible);
    }
}
