package me.lor3mipsum.next.client.impl.modules.combat;

import me.lor3mipsum.next.api.event.network.PacketSendEvent;
import me.lor3mipsum.next.client.core.module.Category;
import me.lor3mipsum.next.client.core.module.Module;
import me.lor3mipsum.next.client.core.module.annotation.Mod;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.particle.ParticleTypes;

import java.util.Random;

@Mod(name = "Criticals", description = "Always deal critical damage", category = Category.COMBAT)
public class Criticals extends Module {

    @EventHandler
    private Listener<PacketSendEvent> onPacketSend = new Listener<>(event -> {
        if (event.packet instanceof PlayerInteractEntityC2SPacket) {
            PlayerInteractEntityC2SPacket packet = (PlayerInteractEntityC2SPacket) event.packet;
            if (packet.getType() == PlayerInteractEntityC2SPacket.InteractionType.ATTACK) {
                this.doCritical();

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
    });

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


}
