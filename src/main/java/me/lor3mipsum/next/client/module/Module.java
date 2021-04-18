package me.lor3mipsum.next.client.module;

import me.lor3mipsum.next.client.impl.settings.KeybindSetting;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;

public abstract class Module {
    protected static final MinecraftClient mc = MinecraftClient.getInstance();

    public Category category;
    public String name;
    public String description;
    public KeybindSetting keybind = new KeybindSetting(GLFW.GLFW_KEY_UNKNOWN);

    public boolean canBeEnabled;
    public boolean hidden;
    public boolean state;

    protected Module(String name, String description, Category moduleCategory) {
        this(name, description, moduleCategory, true, false, GLFW.GLFW_KEY_UNKNOWN);
    }

    protected Module(String name, String description, Category category, boolean canBeEnabled, boolean hidden, int keybind) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.canBeEnabled = canBeEnabled;
        this.hidden = hidden;
        this.keybind.setKey(keybind);
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

    public boolean isCanBeEnabled() {
        return canBeEnabled;
    }

    public boolean isHidden() {
        return hidden;
    }

    public int getKeybind() {
        return keybind.getKey();
    }

    public void setKeybind(int keybind) {
        this.keybind.setKey(keybind);
    }

    public boolean getState() {
        return state;
    }

    public void setState(boolean state) {
        if (state) {
            this.state = true;
            onEnable();
        } else {
            this.state = false;
            onDisable();
        }
    }

    protected void onEnable() {

    }

    protected void onDisable() {

    }

}
