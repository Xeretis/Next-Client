package me.lor3mipsum.next.client.impl.settings;

import me.lor3mipsum.next.client.core.setting.Setting;

import java.util.function.Predicate;

public class DoubleSetting extends Setting<Double> {
    private double min;
    private double max;

    public DoubleSetting(String name, double defaultVal, double min, double max) {
        super(name, defaultVal);
        this.min = min;
        this.max = max;
    }

    public DoubleSetting(String name, double defaultVal, double min, double max, boolean visible) {
        super(name, defaultVal, visible);
        this.min = min;
        this.max = max;
    }

    public DoubleSetting(String name, double defaultVal, double min, double max, boolean visible, Predicate<Double> validator) {
        super(name, defaultVal, visible, validator);
        this.min = min;
        this.max = max;
    }

    public double getMin() {
        return min;
    }

    public double getMax() {
        return max;
    }
}
