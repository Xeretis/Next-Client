package me.lor3mipsum.next.client.impl.settings;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import me.lor3mipsum.next.client.utils.Utils;
import me.lor3mipsum.next.client.setting.Setting;

public class KeybindSetting extends Setting implements com.lukflug.panelstudio.settings.KeybindSetting {
    public int code;

    public KeybindSetting(int code) {
        this("KeyBind", code);
    }

    public KeybindSetting(String name, int code) {
        this.name = name;
        this.code = code;
    }

    @Override
    public int getKey() {
        return code;
    }

    @Override
    public String getKeyName() {
        return Utils.getKeyName(code);
    }

    @Override
    public void setKey(int key) {
        code=key;
    }

    @Override
    public void addToJsonObject(JsonObject obj) {
        obj.addProperty(name, getKey());
    }

    @Override
    public void fromJsonObject(JsonObject obj) {
        if (obj.has(name)) {
            JsonElement element = obj.get(name);

            if (element instanceof JsonPrimitive && ((JsonPrimitive) element).isNumber()) {

                setKey(obj.get(name).getAsNumber().intValue());

            } else {
                throw new IllegalArgumentException("Entry '" + name + "' is not valid");
            }
        } else {
            throw new IllegalArgumentException("Object does not have setting '" + name + "'");
        }
    }

}
