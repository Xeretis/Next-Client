package me.lor3mipsum.next.client.impl.settings;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import me.lor3mipsum.next.client.setting.Setting;

public class BooleanSetting extends Setting<Boolean> {
    public BooleanSetting(String name, Boolean defaultVal) {
        super(name, defaultVal, null);
    }

    @Override
    public void addToJsonObject(JsonObject obj) {
        obj.addProperty(getName(), getObject());
    }

    @Override
    public void fromJsonObject(JsonObject obj) {
        if (obj.has(getName())) {
            JsonElement element = obj.get(getName());

            if (element instanceof JsonPrimitive && ((JsonPrimitive) element).isBoolean()) {
                setObject(element.getAsBoolean());
            } else {
                throw new IllegalArgumentException("Entry '" + getName() + "' is not valid");
            }
        } else {
            throw new IllegalArgumentException("Object does not have setting '" + getName() + "'");
        }
    }

}
