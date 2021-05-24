package me.lor3mipsum.next.client.impl.modules.movement;

import me.lor3mipsum.next.api.event.network.PacketReceiveEvent;
import me.lor3mipsum.next.api.event.player.PlayerPushedEvent;
import me.lor3mipsum.next.api.util.misc.ReflectUtils;
import me.lor3mipsum.next.client.core.module.Category;
import me.lor3mipsum.next.client.core.module.Module;
import me.lor3mipsum.next.client.core.module.annotation.Mod;
import me.lor3mipsum.next.client.impl.settings.BooleanSetting;
import me.lor3mipsum.next.client.impl.settings.IntegerSetting;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;
import net.minecraft.util.math.Vec3d;

@Mod(name = "Velocity", description = "You can change your knockback with this", category = Category.MOVEMENT)
public class Velocity extends Module {

    public BooleanSetting explosionOnly = new BooleanSetting("ExplosionOnly", false);
    public IntegerSetting horizontal = new IntegerSetting("Horizontal", 0, 0, 100);
    public IntegerSetting vertical = new IntegerSetting("Vertical", 0, 0, 100);

    @EventHandler
    private Listener<PlayerPushedEvent> onPlayerPushed = new Listener<>(event -> {
        if (!explosionOnly.getValue()) {
            event.push = new Vec3d(event.push.x * horizontal.getValue(), event.push.y * vertical.getValue(), event.push.z * horizontal.getValue());
        }
    });

    @EventHandler
    private Listener<PacketReceiveEvent> onPacketRecive = new Listener<>(event -> {
        if (mc.player == null)
            return;

        if (event.packet instanceof EntityVelocityUpdateS2CPacket && !explosionOnly.getValue()) {
            EntityVelocityUpdateS2CPacket packet = (EntityVelocityUpdateS2CPacket) event.packet;
            if (packet.getId() == mc.player.getEntityId()) {
                double velXZ = horizontal.getValue() / 100;
                double velY = vertical.getValue() / 100;

                double pvelX = (packet.getVelocityX() / 8000d - mc.player.getVelocity().x) * velXZ;
                double pvelY = (packet.getVelocityY() / 8000d - mc.player.getVelocity().y) * velY;
                double pvelZ = (packet.getVelocityZ() / 8000d - mc.player.getVelocity().z) * velXZ;

                ReflectUtils.writeField(packet, (int) (pvelX * 8000 + mc.player.getVelocity().x * 8000), "field_12563", "velocityX");
                ReflectUtils.writeField(packet, (int) (pvelY * 8000 + mc.player.getVelocity().y * 8000), "field_12562", "velocityY");
                ReflectUtils.writeField(packet, (int) (pvelZ * 8000 + mc.player.getVelocity().z * 8000), "field_12561", "velocityZ");
            }
        } else if (event.packet instanceof ExplosionS2CPacket) {
            ExplosionS2CPacket packet = (ExplosionS2CPacket) event.packet;

            double velXZ = horizontal.getValue() / 100;
            double velY = vertical.getValue() / 100;

            ReflectUtils.writeField(event.packet, (float) (packet.getPlayerVelocityX() * velXZ), "field_12176", "playerVelocityX");
            ReflectUtils.writeField(event.packet, (float) (packet.getPlayerVelocityY() * velY), "field_12182", "playerVelocityY");
            ReflectUtils.writeField(event.packet, (float) (packet.getPlayerVelocityZ() * velXZ), "field_12183", "playerVelocityZ");
        }
    });

}
