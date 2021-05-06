package me.lor3mipsum.next.client.impl.settings;

import me.lor3mipsum.next.client.core.setting.Setting;

import java.util.function.Predicate;

public class NumberSetting<T extends Number> extends Setting<T> {
    private T min;
    private T max;

    public NumberSetting(String name, T defaultVal, T min, T max) {
        super(name, defaultVal);
        this.min = min;
        this.max = max;
    }

    public NumberSetting(String name, T defaultVal, T min, T max, boolean visible) {
        super(name, defaultVal, visible);
        this.min = min;
        this.max = max;
    }

    public NumberSetting(String name, T defaultVal, T min, T max, boolean visible, Predicate<T> validator) {
        super(name, defaultVal, visible, validator);
        this.min = min;
        this.max = max;
    }

    public T getMin() {
        return min;
    }

    public T getMax() {
        return max;
    }
}
