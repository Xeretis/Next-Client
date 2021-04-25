package me.lor3mipsum.next.client.impl.settings;

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

}
