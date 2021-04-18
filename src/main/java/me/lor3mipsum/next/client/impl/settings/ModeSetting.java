package me.lor3mipsum.next.client.impl.settings;

import me.lor3mipsum.next.client.setting.Setting;

import java.util.function.Predicate;

public class ModeSetting extends Setting<Integer> {
    private String[] modes;

    public ModeSetting(String name, String defaultVal, String... modes) {
        this(name, defaultVal, null, modes);
    }

    public ModeSetting(String name, String defaultVal, Predicate<Integer> validator, String... modes) {
        super(name, 0, validator);
        this.modes = modes;

        setObject(defaultVal);
    }

    public String[] getModes() {
        return modes;
    }

    private void setObject(String s) {
        int object = -1;

        for (int i = 0; i < modes.length; i++) {
            String mode = modes[i];

            if (mode.equalsIgnoreCase(s)) object = i;
        }
        if (object == -1) throw new IllegalArgumentException("Value '" + object + "' wasn't found");

        setObject(object);
    }

    @Override
    public boolean setObject(Integer object) {
        if (object < 0 || modes.length <= object)
            throw new IllegalArgumentException(object + " is not valid (max: " + (modes.length - 1) + ")");

        return super.setObject(object);
    }
}
