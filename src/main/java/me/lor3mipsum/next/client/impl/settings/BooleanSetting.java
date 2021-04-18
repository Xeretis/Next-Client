package me.lor3mipsum.next.client.impl.settings;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.lukflug.panelstudio.settings.Toggleable;
import me.lor3mipsum.next.client.setting.Setting;

public class BooleanSetting extends Setting implements Toggleable {
    public boolean enabled;

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

    @Override
    public void addToJsonObject(JsonObject obj) {
        obj.addProperty(name, enabled);
    }

    @Override
    public void fromJsonObject(JsonObject obj) {
        if (obj.has(name)) {
            JsonElement element = obj.get(name);

            if (element instanceof JsonPrimitive && ((JsonPrimitive) element).isBoolean()) {
                setEnabled(element.getAsBoolean());
            } else {
                throw new IllegalArgumentException("Entry '" + name + "' is not valid");
            }
        } else {
            throw new IllegalArgumentException("Object does not have setting '" + name + "'");
        }
    }

}
