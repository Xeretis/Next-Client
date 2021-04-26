package me.lor3mipsum.next.client.impl.modules.movement;

import me.lor3mipsum.next.client.event.EventTarget;
import me.lor3mipsum.next.client.impl.events.SendPacketEvent;
import me.lor3mipsum.next.client.impl.events.TickEvent;
import me.lor3mipsum.next.client.impl.settings.KeybindSetting;
import me.lor3mipsum.next.client.module.Category;
import me.lor3mipsum.next.client.module.Module;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.Formatting;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class Blink extends Module {

    public KeybindSetting keybind = new KeybindSetting(GLFW.GLFW_KEY_UNKNOWN);

    private final List<PlayerMoveC2SPacket> packets = new ArrayList<>();
    private int timer = 0;

    public Blink() {
        super("Blink", "Makes it look like you teleported  for others", Category.MOVEMENT);
    }

    @Override
    public void onDisable() {
        synchronized (packets) {
            packets.forEach(p -> mc.player.networkHandler.sendPacket(p));
            packets.clear();
            timer = 0;
        }
    }

    @EventTarget
    private void onTick(TickEvent.Post event) {
        timer++;
    }

    @EventTarget
    private void onSendPacket(SendPacketEvent event) {
        if (!(event.getPacket() instanceof PlayerMoveC2SPacket)) return;
        event.setCancelled(true);

        synchronized (packets) {
            PlayerMoveC2SPacket p = (PlayerMoveC2SPacket) event.getPacket();
            PlayerMoveC2SPacket prev = packets.size() == 0 ? null : packets.get(packets.size() - 1);

            if (prev != null &&
                    p.isOnGround() == prev.isOnGround() &&
                    p.getYaw(-1) == prev.getYaw(-1) &&
                    p.getPitch(-1) == prev.getPitch(-1) &&
                    p.getX(-1) == prev.getX(-1) &&
                    p.getY(-1) == prev.getY(-1) &&
                    p.getZ(-1) == prev.getZ(-1)
            ) return;

            packets.add(p);
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
        return "[" + Formatting.WHITE + String.format("%.1fs", timer / 20f) + Formatting.GRAY + "]";
    }

}
