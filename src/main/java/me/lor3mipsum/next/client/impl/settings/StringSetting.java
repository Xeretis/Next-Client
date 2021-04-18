package me.lor3mipsum.next.client.impl.settings;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import me.lor3mipsum.next.client.setting.Setting;

import java.util.function.Predicate;

public class StringSetting extends Setting<String> {

    public StringSetting(String name, String defaultVal) {
        this(name, defaultVal, null);
    }

    public StringSetting(String name, String defaultVal, Predicate<String> validator) {
        super(name, defaultVal, validator);
    }

    @Override
    public void addToJsonObject(JsonObject obj) {
        obj.addProperty(getName(), getObject());
    }

    @Override
    public void fromJsonObject(JsonObject obj) {
        if (obj.has(getName())) {
            JsonElement element = obj.get(getName());

            if (element instanceof JsonPrimitive && ((JsonPrimitive) element).isString()) {
                setObject(element.getAsString());
            } else {
                throw new IllegalArgumentException("Entry '" + getName() + "' is not valid");
            }
        } else {
            throw new IllegalArgumentException("Object does not have setting '" + getName() + "'");
        }
    }
}
