package me.lor3mipsum.next.client.impl.modules.player;

import me.lor3mipsum.next.api.event.network.PacketReceiveEvent;
import me.lor3mipsum.next.api.util.player.ChatUtils;
import me.lor3mipsum.next.client.core.module.Category;
import me.lor3mipsum.next.client.core.module.Module;
import me.lor3mipsum.next.client.core.module.annotation.Mod;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.network.packet.s2c.play.HealthUpdateS2CPacket;
import net.minecraft.util.math.Vec3d;

@Mod(name = "DeathCoords", description = "Shows your coords in the chat when you die (client side only)", category = Category.PLAYER)
public class DeathCoords extends Module {

    @EventHandler
    private Listener<PacketReceiveEvent> onPacketRecive = new Listener<>(event -> {
        if (event.packet instanceof HealthUpdateS2CPacket) {
            HealthUpdateS2CPacket packet = (HealthUpdateS2CPacket) event.packet;

            if (packet.getHealth() <= 0) {
                Vec3d dmgPos = mc.player.getPos();
                ChatUtils.moduleInfo(this, String.format("Last death: %.1f, %.1f, %.1f", dmgPos.x, dmgPos.y, dmgPos.z));
            }
        }
    });

}
