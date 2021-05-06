package me.lor3mipsum.next.client.impl.settings;

import me.lor3mipsum.next.client.core.setting.Setting;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class EnumSetting<T extends Enum<?>> extends Setting<T> {
    private boolean reverse;

    public EnumSetting(String name, T defaultVal) {
        super(name, defaultVal);
        this.reverse = false;
    }

    public EnumSetting(String name, T defaultVal, boolean visible) {
        super(name, defaultVal, visible);
        this.reverse = false;
    }

    public EnumSetting(String name, T defaultVal, boolean visible, boolean reverse) {
        super(name, defaultVal, visible);
        this.reverse = reverse;
    }

    public EnumSetting(String name, T defaultVal, boolean visible, Predicate<T> validator) {
        super(name, defaultVal, visible, validator);
        this.reverse = false;
    }

    public EnumSetting(String name, T defaultVal, boolean visible, boolean reverse, Predicate<T> validator) {
        super(name, defaultVal, visible, validator);
        this.reverse = reverse;
    }

    public List<String> getModes() {
        List<String> modes = new ArrayList<>();
        for (int i = 0; i < getValue().getClass().getEnumConstants().length; i++) {
            String name = ((Enum) getValue().getClass().getEnumConstants()[i]).toString().toLowerCase();
            modes.add(name.substring(0, 1).toUpperCase() + name.substring(1));
        }
        return modes;
    }

    public void increment() {
        int index = 0;

        for (int i = 0; i < getValue().getClass().getEnumConstants().length; i++) {
            if (getValue().getClass().getEnumConstants()[i] == getValue())
                index = i;
        }

        if (index >= getValue().getClass().getEnumConstants().length - 1)
            setValue((T) getValue().getClass().getEnumConstants()[0]);
        else
            setValue((T) getValue().getClass().getEnumConstants()[index + 1]);
    }

    public void decrement() {
        int index = 0;

        for (int i = 0; i < getValue().getClass().getEnumConstants().length; i++) {
            if (getValue().getClass().getEnumConstants()[i] == getValue())
                index = i;
        }

        if (index == 0)
            setValue((T) getValue().getClass().getEnumConstants()[getValue().getClass().getEnumConstants().length - 1]);
        else
            setValue((T) getValue().getClass().getEnumConstants()[index - 1]);
    }


}
