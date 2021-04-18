package me.lor3mipsum.next.client.impl.settings;

import me.lor3mipsum.next.client.setting.Setting;

import java.util.function.Predicate;

public class NumberSetting<T extends Number> extends Setting<T> {
    private T min;
    private T max;

    public NumberSetting(String name, T defaultVal, T min, T max) {
        this(name, defaultVal, min, max, null);
    }

    public NumberSetting(String name, T defaultVal, T min, T max, Predicate<T> validator) {
        super(name, defaultVal, validator == null ? val -> val.doubleValue() >= min.doubleValue() && val.doubleValue() <= max.doubleValue() : validator.and(val -> val.doubleValue() >= min.doubleValue() && val.doubleValue() <= max.doubleValue()));
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
