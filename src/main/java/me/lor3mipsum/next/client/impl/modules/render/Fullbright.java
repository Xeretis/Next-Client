package me.lor3mipsum.next.client.impl.modules.render;

import me.lor3mipsum.next.client.event.EventTarget;
import me.lor3mipsum.next.client.impl.events.TickEvent;
import me.lor3mipsum.next.client.impl.settings.KeybindSetting;
import me.lor3mipsum.next.client.module.Category;
import me.lor3mipsum.next.client.module.Module;
import org.lwjgl.glfw.GLFW;

public class Fullbright extends Module {

    public KeybindSetting keybind = new KeybindSetting(GLFW.GLFW_KEY_UNKNOWN);

    private int timesEnabled;
    private int lastTimesEnabled;

    private static double prevGamma;

    public Fullbright() {
        super("Fullbright", "It's just... Fullbright", Category.RENDER);
    }

    @Override
    public void onEnable() {
        prevGamma = mc.options.gamma;
    }

    @Override
    public void onDisable() {
        mc.options.gamma = prevGamma;
    }

    @EventTarget
    private void onTick(TickEvent.Post event) {
        mc.options.gamma = 1000;
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
