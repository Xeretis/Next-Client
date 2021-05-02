package me.lor3mipsum.next.client.impl.modules.player;

import me.lor3mipsum.next.client.event.EventTarget;
import me.lor3mipsum.next.client.impl.events.ClientMoveEvent;
import me.lor3mipsum.next.client.impl.events.SendPacketEvent;
import me.lor3mipsum.next.client.impl.events.TickEvent;
import me.lor3mipsum.next.client.impl.settings.KeybindSetting;
import me.lor3mipsum.next.client.impl.settings.NumberSetting;
import me.lor3mipsum.next.client.module.Category;
import me.lor3mipsum.next.client.module.Module;
import me.lor3mipsum.next.client.utils.player.FakePlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;

public class Freecam extends Module {

    public NumberSetting speed = new NumberSetting("Speed", 0.5, 0, 3, 0.1);
    public KeybindSetting keybind = new KeybindSetting(GLFW.GLFW_KEY_UNKNOWN);

    public FakePlayerEntity dummy;
    private double[] playerPos;
    private float[] playerRot;
    private Entity riding;

    private boolean prevFlying;
    private float prevFlySpeed;

    public Freecam() {
        super("Freecam", "It rips your soul out of your body", Category.PLAYER);
    }

    @Override
    public void onEnable() {
        super.onEnable();

        playerPos = new double[] { mc.player.getX(), mc.player.getY(), mc.player.getZ() };
        playerRot = new float[] { mc.player.yaw, mc.player.pitch };

        dummy = new FakePlayerEntity(mc.player.getName().getString(), true, mc.player.getHealth());

        if (mc.player.getVehicle() != null) {
            riding = mc.player.getVehicle();
            mc.player.getVehicle().removeAllPassengers();
        }

        if (mc.player.isSprinting()) {
            mc.player.networkHandler.sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.STOP_SPRINTING));
        }

        prevFlying = mc.player.abilities.flying;
        prevFlySpeed = mc.player.abilities.getFlySpeed();

        mc.chunkCullingEnabled = false;
    }

    @Override
    public void onDisable() {
        mc.chunkCullingEnabled = true;

        dummy.despawn();
        mc.player.noClip = false;
        mc.player.abilities.flying = prevFlying;
        mc.player.abilities.setFlySpeed(prevFlySpeed);

        mc.player.refreshPositionAndAngles(playerPos[0], playerPos[1], playerPos[2], playerRot[0], playerRot[1]);
        mc.player.setVelocity(Vec3d.ZERO);

        if (riding != null && mc.world.getEntityById(riding.getEntityId()) != null) {
            mc.player.startRiding(riding);
        }

        super.onDisable();
    }

    @EventTarget
    private void sendPacket(SendPacketEvent event) {
        if (event.getPacket() instanceof ClientCommandC2SPacket || event.getPacket() instanceof PlayerMoveC2SPacket) {
            event.setCancelled(true);
        }
    }

    @EventTarget
    private void onClientMove(ClientMoveEvent event) {
        mc.player.noClip = true;
    }

    @EventTarget
    private void onTick(TickEvent.Post event) {
        mc.player.setOnGround(false);
        mc.player.abilities.setFlySpeed((float) (speed.getNumber() / 5));
        mc.player.abilities.flying = true;
        mc.player.setPose(EntityPose.STANDING);
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
