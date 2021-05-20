package me.lor3mipsum.next.client.impl.settings;

import me.lor3mipsum.next.client.core.setting.Setting;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class EnumSetting<T extends Enum<?>> extends Setting<T> {

    public EnumSetting(String name, T defaultVal) {
        super(name, defaultVal);
    }

    public EnumSetting(String name, T defaultVal, boolean visible) {
        super(name, defaultVal, visible);
    }


    public EnumSetting(String name, T defaultVal, boolean visible, Predicate<T> validator) {
        super(name, defaultVal, visible, validator);
    }

    public List<String> getModes() {
        List<String> modes = new ArrayList<>();
        for (int i = 0; i < getValue().getClass().getEnumConstants().length; i++) {
            modes.add(getValue().getClass().getEnumConstants()[i].toString());
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
