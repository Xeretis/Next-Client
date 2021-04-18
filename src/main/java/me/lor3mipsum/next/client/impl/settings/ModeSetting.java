package me.lor3mipsum.next.client.impl.settings;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.lukflug.panelstudio.settings.EnumSetting;
import me.lor3mipsum.next.client.setting.Setting;

import java.util.Arrays;
import java.util.List;

public class ModeSetting extends Setting implements EnumSetting {
    public int index;

    public List<String> modes;

    public ModeSetting(String name, String defaultMode, String... modes) {
        this.name = name;
        this.modes = Arrays.asList(modes);
        this.index = this.modes.indexOf(defaultMode);
    }

    public String getMode() {
        return this.modes.get(this.index);
    }

    public void setMode(String mode) {
        this.index = this.modes.indexOf(mode);
    }

    public boolean is(String mode) {
        return (this.index == this.modes.indexOf(mode));
    }

    public void cycle() {
        if (this.index < this.modes.size() - 1) {
            this.index++;
        }else {
            this.index = 0;
        }
    }

    @Override
    public String getValueName() {
        return this.modes.get(this.index);
    }

    @Override
    public void increment() {
        if (this.index < this.modes.size() - 1) {
            this.index++;
        }else {
            this.index = 0;
        }
    }

    @Override
    public void addToJsonObject(JsonObject obj) {
        obj.addProperty(name, getMode());
    }

    @Override
    public void fromJsonObject(JsonObject obj) {
        if (obj.has(name)) {
            JsonElement element = obj.get(name);

            if (element instanceof JsonPrimitive && ((JsonPrimitive) element).isString()) {
                setMode(element.getAsString());
            } else {
                throw new IllegalArgumentException("Entry '" + name + "' is not valid");
            }
        } else {
            throw new IllegalArgumentException("Object does not have setting '" + name + "'");
        }
    }

}
