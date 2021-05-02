package me.lor3mipsum.next.client.impl.modules.combat;

import me.lor3mipsum.next.client.event.EventTarget;
import me.lor3mipsum.next.client.impl.events.SendPacketEvent;
import me.lor3mipsum.next.client.impl.settings.KeybindSetting;
import me.lor3mipsum.next.client.module.Category;
import me.lor3mipsum.next.client.module.Module;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.particle.ParticleTypes;
import org.lwjgl.glfw.GLFW;

import java.util.Random;

public class Criticals extends Module{
    public KeybindSetting keybind = new KeybindSetting(GLFW.GLFW_KEY_UNKNOWN);

    public Criticals() {
        super("Criticals", "Makes your hits critical hits", Category.COMBAT);
    }

    @EventTarget
    public void sendPacket(SendPacketEvent event) {
        if (event.getPacket() instanceof PlayerInteractEntityC2SPacket) {
            PlayerInteractEntityC2SPacket packet = (PlayerInteractEntityC2SPacket) event.getPacket();
            if (packet.getType() == PlayerInteractEntityC2SPacket.InteractionType.ATTACK) {
                this.doCritical();

                /* Lets fake some extra paricles why not */
                Entity e = packet.getEntity(mc.world);

                if (e != null) {
                    Random r = new Random();
                    for (int i = 0; i < 10; i++) {
                        mc.particleManager.addParticle(ParticleTypes.CRIT, e.getX(), e.getY() + e.getHeight() / 2, e.getZ(),
                                r.nextDouble() - 0.5, r.nextDouble() - 0.5, r.nextDouble() - 0.5);
                    }
                }
            }
        }
    }

    private void doCritical() {
        if (!mc.player.isOnGround() || mc.player.isInLava() || mc.player.isTouchingWater()) {
            return;
        }

        double posX = mc.player.getX();
        double posY = mc.player.getY();
        double posZ = mc.player.getZ();
        mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionOnly(posX, posY + 0.0625, posZ, true));
        mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionOnly(posX, posY, posZ, false));
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
