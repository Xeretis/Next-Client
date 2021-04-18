package me.lor3mipsum.next.client.module;

import com.lukflug.panelstudio.settings.Toggleable;
import me.lor3mipsum.next.client.impl.settings.KeybindSetting;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;

public abstract class Module implements Toggleable {
    protected static final MinecraftClient mc = MinecraftClient.getInstance();

    public Category category;
    public String name;
    public String description;

    public boolean canBeEnabled;
    public boolean hidden;
    public boolean state;

    protected Module(String name, String description, Category moduleCategory) {
        this(name, description, moduleCategory, true, false);
    }

    protected Module(String name, String description, Category category, boolean canBeEnabled, boolean hidden) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.canBeEnabled = canBeEnabled;
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

    public boolean isCanBeEnabled() {
        return canBeEnabled;
    }

    public boolean isHidden() {
        return hidden;
    }

    public abstract int getKeybind();

    public abstract void setKeybind(int keybind);

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

    @Override
    public void toggle() {
        setState(!state);
    }

    @Override
    public boolean isOn() {
        return state;
    }

}
