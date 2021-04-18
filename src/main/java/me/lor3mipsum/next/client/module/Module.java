package me.lor3mipsum.next.client.module;

import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;

public abstract class Module {
    protected static final MinecraftClient mc = MinecraftClient.getInstance();

    private Category category;
    private String name;
    private String description;
    private int keybind;

    private boolean canBeEnabled;
    private boolean hidden;
    private boolean state;

    protected Module(String name, String description, Category moduleCategory) {
        this(name, description, moduleCategory, true, false, GLFW.GLFW_KEY_UNKNOWN);
    }

    protected Module(String name, String description, Category category, boolean canBeEnabled, boolean hidden, int keybind) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.canBeEnabled = canBeEnabled;
        this.hidden = hidden;
        this.keybind = keybind;
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
        return keybind;
    }

    public void setKeybind(int keybind) {
        this.keybind = keybind;
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
