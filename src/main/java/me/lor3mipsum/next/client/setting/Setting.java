package me.lor3mipsum.next.client.setting;

import java.util.function.Predicate;

public abstract class Setting<T> {
    private String name;
    private T object;
    private T defaultValue;

    private Predicate<T> validator;

    public Setting(String name, T defaultVal, Predicate<T> validator) {
        this.name = name;
        this.object = defaultVal;
        this.defaultValue = defaultVal;
        this.validator = validator;
    }

    public String getName() {
        return name;
    }

    public T getObject() {
        return object;
    }

    public boolean setObject(T object) {
        if (validator != null && !validator.test(object)) return false;

        this.object = object;

        return true;
    }

    public void setValidator(Predicate<T> validator) {
        this.validator = validator;
    }

    public Object getDefault() {
        return defaultValue;
    }
}
