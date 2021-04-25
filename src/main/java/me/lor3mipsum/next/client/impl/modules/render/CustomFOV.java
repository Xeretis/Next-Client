package me.lor3mipsum.next.client.impl.modules.render;

import me.lor3mipsum.next.client.event.EventTarget;
import me.lor3mipsum.next.client.impl.events.TickEvent;
import me.lor3mipsum.next.client.impl.settings.KeybindSetting;
import me.lor3mipsum.next.client.impl.settings.NumberSetting;
import me.lor3mipsum.next.client.module.Category;
import me.lor3mipsum.next.client.module.Module;
import org.lwjgl.glfw.GLFW;

public class CustomFOV extends Module {

    public NumberSetting fov = new NumberSetting("FOV", 100, 1, 170, 0.5);
    public KeybindSetting keybind = new KeybindSetting(GLFW.GLFW_KEY_UNKNOWN);

    private float prevFov = 110;

    public CustomFOV() {
        super("CustomFOV", "Allows you to set your fov higher or lower than what the options let you to", Category.RENDER);
    }

    @Override
    public void onEnable() {
        if (mc.options != null)
            prevFov = (float) mc.options.fov;
    }

    @Override
    public void onDisable() {
        mc.options.fov = prevFov;
    }

    @EventTarget
    private void onTick(TickEvent.Post event) {
        mc.options.fov = fov.getNumber();
    }

    @Override
    public int getKeybind() {
        return keybind.getKey();
    }

    @Override
    public void setKeybind(int keybind) {
        this.keybind.setKey(keybind);
    }
}
