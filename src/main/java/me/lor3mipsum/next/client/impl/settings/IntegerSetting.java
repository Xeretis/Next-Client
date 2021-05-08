package me.lor3mipsum.next.client.impl.settings;

import me.lor3mipsum.next.client.core.setting.Setting;

import java.util.function.Predicate;

public class IntegerSetting extends Setting<Integer> {
    private int min;
    private int max;

    public IntegerSetting(String name, int defaultVal, int min, int max) {
        super(name, defaultVal);
        this.min = min;
        this.max = max;
    }

    public IntegerSetting(String name, int defaultVal,int min, int max, boolean visible) {
        super(name, defaultVal, visible);
        this.min = min;
        this.max = max;
    }

    public IntegerSetting(String name, int defaultVal, int min, int max, boolean visible, Predicate<Integer> validator) {
        super(name, defaultVal, visible, validator);
        this.min = min;
        this.max = max;
    }

    public int getMin() {
        return min;
    }

    public int getMax() {
        return max;
    }
}
