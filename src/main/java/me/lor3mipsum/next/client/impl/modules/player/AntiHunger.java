package me.lor3mipsum.next.client.impl.modules.player;

import me.lor3mipsum.next.client.event.EventTarget;
import me.lor3mipsum.next.client.impl.events.SendPacketEvent;
import me.lor3mipsum.next.client.impl.events.TickEvent;
import me.lor3mipsum.next.client.impl.settings.BooleanSetting;
import me.lor3mipsum.next.client.impl.settings.KeybindSetting;
import me.lor3mipsum.next.client.module.Category;
import me.lor3mipsum.next.client.module.Module;
import me.lor3mipsum.next.mixin.PlayerMoveC2SPacketAccessor;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import org.lwjgl.glfw.GLFW;

public class AntiHunger extends Module {

    public BooleanSetting sprint = new BooleanSetting("Sprinting", true);
    public BooleanSetting onGround = new BooleanSetting("OnGround", true);
    public KeybindSetting keybind = new KeybindSetting(GLFW.GLFW_KEY_UNKNOWN);

    private boolean lastOnGround;
    private boolean sendOnGroundTruePacket;
    private boolean ignorePacket;

    public AntiHunger() {
        super("AntiHunger", "Reduces your hunger", Category.PLAYER);
    }

    @EventTarget
    private void onSendPacket(SendPacketEvent event) {
        if (ignorePacket) return;

        if (event.getPacket() instanceof ClientCommandC2SPacket && sprint.isOn()) {
            ClientCommandC2SPacket.Mode mode = ((ClientCommandC2SPacket) event.getPacket()).getMode();

            if (mode == ClientCommandC2SPacket.Mode.START_SPRINTING || mode == ClientCommandC2SPacket.Mode.STOP_SPRINTING) {
                event.setCancelled(true);
            }
        }

        if (event.getPacket() instanceof PlayerMoveC2SPacket && onGround.isOn() && mc.player.isOnGround() && mc.player.fallDistance <= 0.0 && !mc.interactionManager.isBreakingBlock()) {
            ((PlayerMoveC2SPacketAccessor) event.getPacket()).setOnGround(false);
        }
    }

    @EventTarget
    private void onTick(TickEvent.Post event) {
        if (mc.player.isOnGround() && !lastOnGround && !sendOnGroundTruePacket) sendOnGroundTruePacket = true;

        if (mc.player.isOnGround() && sendOnGroundTruePacket && onGround.isOn()) {
            ignorePacket = true;
            mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket(true));
            ignorePacket = false;

            sendOnGroundTruePacket = false;
        }

        lastOnGround = mc.player.isOnGround();
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
