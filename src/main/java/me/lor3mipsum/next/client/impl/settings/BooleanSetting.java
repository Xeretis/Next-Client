package me.lor3mipsum.next.client.impl.settings;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.lukflug.panelstudio.settings.Toggleable;
import me.lor3mipsum.next.client.setting.Setting;

import java.util.List;

public class BooleanSetting extends Setting implements Toggleable {
    public boolean enabled;

    private static final List<String> SUGGESTIONS = ImmutableList.of("true", "false", "toggle");

    public BooleanSetting(String name, boolean enabled) {
        this.name = name;
        this.enabled = enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void toggle() {
        this.enabled = !this.enabled;
    }

    @Override
    public boolean isOn() {
        return enabled;
    }

}
