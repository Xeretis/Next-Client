package me.lor3mipsum.next.client.impl.modules.movement;

import me.lor3mipsum.next.client.event.EventTarget;
import me.lor3mipsum.next.client.impl.events.PlayerPushedEvent;
import me.lor3mipsum.next.client.impl.events.ReadPacketEvent;
import me.lor3mipsum.next.client.impl.settings.BooleanSetting;
import me.lor3mipsum.next.client.impl.settings.KeybindSetting;
import me.lor3mipsum.next.client.impl.settings.NumberSetting;
import me.lor3mipsum.next.client.module.Category;
import me.lor3mipsum.next.client.module.Module;
import me.lor3mipsum.next.client.utils.FabricReflect;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;

public class Velocity extends Module{
    public BooleanSetting explosionOnly = new BooleanSetting("ExplosionOnly", false);
    public NumberSetting horizontal = new NumberSetting("Horizontal", 0, 0, 100, 1);
    public NumberSetting vertical = new NumberSetting("Vertical", 0, 0, 100, 1);
    public KeybindSetting keybind = new KeybindSetting(GLFW.GLFW_KEY_UNKNOWN);

    public Velocity() {
        super("Velocity", "Allows you to modify the amount of knockback you take", Category.MOVEMENT);
    }

    @Override
    public int getKeybind() {
        return keybind.getKey();
    }

    @Override
    public void setKeybind(int keybind) {
        this.keybind.setKey(keybind);
    }

    @EventTarget
    public void onPlayerPushed(PlayerPushedEvent event) {
        if (!explosionOnly.isOn()) {
            event.setPush(new Vec3d(event.getPush().x * horizontal.getNumber(), event.getPush().y * vertical.getNumber(), event.getPush().z * horizontal.getNumber()));
        }
    }

    @EventTarget
    public void readPacket(ReadPacketEvent event) {
        if (mc.player == null)
            return;

        if (event.getPacket() instanceof EntityVelocityUpdateS2CPacket && !explosionOnly.isOn()) {
            EntityVelocityUpdateS2CPacket packet = (EntityVelocityUpdateS2CPacket) event.getPacket();
            if (packet.getId() == mc.player.getEntityId()) {
                double velXZ = horizontal.getNumber() / 100;
                double velY = vertical.getNumber() / 100;

                double pvelX = (packet.getVelocityX() / 8000d - mc.player.getVelocity().x) * velXZ;
                double pvelY = (packet.getVelocityY() / 8000d - mc.player.getVelocity().y) * velY;
                double pvelZ = (packet.getVelocityZ() / 8000d - mc.player.getVelocity().z) * velXZ;

                FabricReflect.writeField(packet, (int) (pvelX * 8000 + mc.player.getVelocity().x * 8000), "field_12563", "velocityX");
                FabricReflect.writeField(packet, (int) (pvelY * 8000 + mc.player.getVelocity().y * 8000), "field_12562", "velocityY");
                FabricReflect.writeField(packet, (int) (pvelZ * 8000 + mc.player.getVelocity().z * 8000), "field_12561", "velocityZ");
            }
        } else if (event.getPacket() instanceof ExplosionS2CPacket) {
            ExplosionS2CPacket packet = (ExplosionS2CPacket) event.getPacket();

            double velXZ = horizontal.getNumber() / 100;
            double velY = vertical.getNumber() / 100;

            FabricReflect.writeField(event.getPacket(), (float) (packet.getPlayerVelocityX() * velXZ), "field_12176", "playerVelocityX");
            FabricReflect.writeField(event.getPacket(), (float) (packet.getPlayerVelocityY() * velY), "field_12182", "playerVelocityY");
            FabricReflect.writeField(event.getPacket(), (float) (packet.getPlayerVelocityZ() * velXZ), "field_12183", "playerVelocityZ");
        }
    }

}
