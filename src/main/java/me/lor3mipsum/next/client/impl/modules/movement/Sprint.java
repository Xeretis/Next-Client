package me.lor3mipsum.next.client.impl.modules.movement;

import me.lor3mipsum.next.client.event.EventTarget;
import me.lor3mipsum.next.client.impl.events.TickEvent;
import me.lor3mipsum.next.client.impl.settings.KeybindSetting;
import me.lor3mipsum.next.client.impl.settings.ModeSetting;
import me.lor3mipsum.next.client.module.Category;
import me.lor3mipsum.next.client.module.Module;
import net.minecraft.util.Formatting;
import org.lwjgl.glfw.GLFW;

public class Sprint extends Module {

    public ModeSetting mode = new ModeSetting("Mode", "Normal", "Normal", "Boost");
    public KeybindSetting keybind = new KeybindSetting(GLFW.GLFW_KEY_UNKNOWN);

    public Sprint() {
        super("Sprint", "Do I really have to explain this?", Category.MOVEMENT);
    }

    @Override
    public void onDisable() {
        mc.player.setSprinting(false);
    }

    @EventTarget
    private void onTick(TickEvent.Post event) {
        switch (mode.getMode()) {
            case "Boost":
                if(mc.options.keyForward.isPressed() || mc.options.keyBack.isPressed() || mc.options.keyRight.isPressed() || mc.options.keyLeft.isPressed() && !mc.player.isSneaking() && !mc.player.horizontalCollision)
                    mc.player.setSprinting(true);
            case "Normal":
                if(mc.options.keyForward.isPressed() && !mc.player.isSneaking() && !mc.player.horizontalCollision)
                    mc.player.setSprinting(true);
        }

    }

    @Override
    public int getKeybind() {
        return keybind.getKey();
    }

    @Override
    public void setKeybind(int keybind) {
        this.keybind.setKey(keybind);
    }

    @Override
    public String getHudInfo() {
        return "[" + Formatting.WHITE + mode.getMode() + Formatting.GRAY + "]";
    }
}
