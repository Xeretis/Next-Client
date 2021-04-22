package me.lor3mipsum.next.client.module;

import com.lukflug.panelstudio.settings.Toggleable;
import me.lor3mipsum.next.Next;
import me.lor3mipsum.next.client.event.EventManager;
import me.lor3mipsum.next.client.impl.settings.KeybindSetting;
import me.lor3mipsum.next.client.setting.Setting;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;

import java.util.List;

public abstract class Module implements Toggleable {
    protected static final MinecraftClient mc = MinecraftClient.getInstance();

    public Category category;
    public String name;
    public String description;

    public boolean hidden;
    public boolean state;

    protected Module(String name, String description, Category moduleCategory) {
        this(name, description, moduleCategory, false);
    }

    protected Module(String name, String description, Category category, boolean hidden) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.hidden = hidden;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Category getCategory() {
        return category;
    }

    public boolean isHidden() {
        return hidden;
    }

    public abstract int getKeybind();

    public abstract void setKeybind(int keybind);

    public boolean getState() {
        return state;
    }

    public List<Setting> getSettings() {
        return Next.INSTANCE.settingManager.getAllSettingsFrom(name);
    }

    public void setState(boolean state) {
        if (state) {
            this.state = true;
            EventManager.register(this);
            onEnable();
        } else {
            this.state = false;
            EventManager.unregister(this);
            onDisable();
        }
    }

    protected void onEnable() {

    }

    protected void onDisable() {

    }

    @Override
    public void toggle() {
        setState(!state);
    }

    @Override
    public boolean isOn() {
        return state;
    }

}
