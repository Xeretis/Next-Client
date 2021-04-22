package me.lor3mipsum.next.client.impl.modules.movement;

import me.lor3mipsum.next.client.impl.settings.BooleanSetting;
import me.lor3mipsum.next.client.impl.settings.KeybindSetting;
import me.lor3mipsum.next.client.impl.settings.NumberSetting;
import me.lor3mipsum.next.client.module.Category;
import me.lor3mipsum.next.client.module.Module;
import org.lwjgl.glfw.GLFW;

public class Velocity extends Module{
    public BooleanSetting explosionOnly = new BooleanSetting("ExplosionOnly", false);
    public NumberSetting horizontal = new NumberSetting("Horizontal", 0, 0, 100, 1);
    public NumberSetting vertical = new NumberSetting("Vertical", 0, 0, 100, 1);
    public KeybindSetting keybind = new KeybindSetting(GLFW.GLFW_KEY_UNKNOWN);

    public Velocity() {
        super("Velocity", "Allows you to modify the amount of knockback you take.", Category.MOVEMENT);
    }

    @Override
    public int getKeybind() {
        return keybind.getKey();
    }

    @Override
    public void setKeybind(int keybind) {
        this.keybind.setKey(keybind);
    }

    public double getHorizontal() {
        return isOn() ? horizontal.getNumber() : 1;
    }

    public double getVertical() {
        return isOn() ? vertical.getNumber() : 1;
    }
}
