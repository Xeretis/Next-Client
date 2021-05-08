package me.lor3mipsum.next.client.impl.settings;

import me.lor3mipsum.next.client.core.setting.Setting;

import java.util.function.Predicate;

public class FloatSetting extends Setting<Float> {
    private float min;
    private float max;

    public FloatSetting(String name, float defaultVal, float min, float max) {
        super(name, defaultVal);
        this.min = min;
        this.max = max;
    }

    public FloatSetting(String name, float defaultVal, float min, float max, boolean visible) {
        super(name, defaultVal, visible);
        this.min = min;
        this.max = max;
    }

    public FloatSetting(String name, float defaultVal, float min, float max, boolean visible, Predicate<Float> validator) {
        super(name, defaultVal, visible, validator);
        this.min = min;
        this.max = max;
    }

    public float getMin() {
        return min;
    }

    public float getMax() {
        return max;
    }
}
