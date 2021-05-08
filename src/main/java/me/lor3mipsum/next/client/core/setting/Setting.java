package me.lor3mipsum.next.client.core.setting;

import java.util.function.Predicate;

public abstract class Setting<T> {
    private String name;
    private T value;
    private T defaultVal;

    private boolean visible;

    private Predicate<T> validator;

    protected Setting(String name, T defaultVal) {
        this(name, defaultVal, true, null);
    }

    protected Setting(String name, T defaultVal, boolean visible) {
        this(name, defaultVal, visible, null);
    }

    protected Setting(String name, T defaultVal, boolean visible, Predicate<T> validator) {
        this.name = name;
        this.value = defaultVal;
        this.defaultVal = defaultVal;
        this.visible = visible;
        this.validator = validator;
    }

    public boolean setValue(T value) {
        if (validator != null && !validator.test(value)) return false;

        this.value = value;

        return true;
    }

    public void setValidator(Predicate<T> validator) {
        this.validator = validator;
    }

    public String getName() {
        return name;
    }

    public String getConfigName() {
        return name.replace(" ", "_").toLowerCase();
    }

    public T getValue() {
        return value;
    }

    public Object getDefault() {
        return defaultVal;
    }

    public boolean getVisible() {
        return visible;
    }
}
