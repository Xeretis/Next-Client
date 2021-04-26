package me.lor3mipsum.next.client.impl.modules.player;

import me.lor3mipsum.next.client.event.EventTarget;
import me.lor3mipsum.next.client.impl.events.RecivePacketEvent;
import me.lor3mipsum.next.client.impl.settings.KeybindSetting;
import me.lor3mipsum.next.client.module.Category;
import me.lor3mipsum.next.client.module.Module;
import me.lor3mipsum.next.client.utils.ChatUtils;
import net.minecraft.network.packet.s2c.play.HealthUpdateS2CPacket;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;

public class DeathCoords extends Module {
    public KeybindSetting keybind = new KeybindSetting(GLFW.GLFW_KEY_UNKNOWN);

    public DeathCoords() {
        super("DeathCoords", "Writes your coords to the chat when you die", Category.PLAYER);
    }

    @EventTarget
    private void onPacketRecive(RecivePacketEvent event) {
        if (event.getPacket() instanceof HealthUpdateS2CPacket) {
            HealthUpdateS2CPacket packet = (HealthUpdateS2CPacket) event.getPacket();

            if (packet.getHealth() <= 0) onDeath();
        }
    }

    private void onDeath() {
        Vec3d dmgPos = mc.player.getPos();
        ChatUtils.moduleInfo(this, String.format("Last death: %.1f, %.1f, %.1f", dmgPos.x, dmgPos.y, dmgPos.z));
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
