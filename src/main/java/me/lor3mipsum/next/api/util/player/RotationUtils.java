package me.lor3mipsum.next.api.util.player;

import me.lor3mipsum.next.api.event.NextEvent;
import me.lor3mipsum.next.api.event.client.TickEvent;
import me.lor3mipsum.next.api.event.player.SendMovementPacketsEvent;
import me.lor3mipsum.next.api.util.misc.Pool;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listenable;
import me.zero.alpine.listener.Listener;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

public class RotationUtils implements Listenable {

    private final Pool<Rotation> rotationPool = new Pool<>(Rotation::new);
    private final List<Rotation> rotations = new ArrayList<>();
    public float serverYaw;
    public float serverPitch;
    public int rotationTimer;
    private float preYaw, prePitch;
    private int i = 0;

    private Rotation lastRotation;
    private int lastRotationTimer;
    private boolean sentLastRotation;

    private static final MinecraftClient mc = MinecraftClient.getInstance();

    public static RotationUtils INSTANCE = new RotationUtils();

    public static double getYaw(Entity entity) {
        return mc.player.yaw + MathHelper.wrapDegrees((float) Math.toDegrees(Math.atan2(entity.getZ() - mc.player.getZ(), entity.getX() - mc.player.getX())) - 90f - mc.player.yaw);
    }

    public static double getYaw(Vec3d pos) {
        return mc.player.yaw + MathHelper.wrapDegrees((float) Math.toDegrees(Math.atan2(pos.getZ() - mc.player.getZ(), pos.getX() - mc.player.getX())) - 90f - mc.player.yaw);
    }

    public static double getPitch(Vec3d pos) {
        double diffX = pos.getX() - mc.player.getX();
        double diffY = pos.getY() - (mc.player.getY() + mc.player.getEyeHeight(mc.player.getPose()));
        double diffZ = pos.getZ() - mc.player.getZ();

        double diffXZ = Math.hypot(diffX, diffZ);

        return mc.player.pitch + MathHelper.wrapDegrees((float) -Math.toDegrees(Math.atan2(diffY, diffXZ)) - mc.player.pitch);
    }

    public static double getPitch(Entity entity) {
        double y;

        y = entity.getY() + entity.getHeight() / 2;


        double diffX = entity.getX() - mc.player.getX();
        double diffY = y - (mc.player.getY() + mc.player.getEyeHeight(mc.player.getPose()));
        double diffZ = entity.getZ() - mc.player.getZ();

        double diffXZ = Math.hypot(diffX, diffZ);

        return mc.player.pitch + MathHelper.wrapDegrees((float) -Math.toDegrees(Math.atan2(diffY, diffXZ)) - mc.player.pitch);
    }

    public static double getYaw(BlockPos pos) {
        return mc.player.yaw + MathHelper.wrapDegrees((float) Math.toDegrees(Math.atan2(pos.getZ() + 0.5 - mc.player.getZ(), pos.getX() + 0.5 - mc.player.getX())) - 90f - mc.player.yaw);
    }

    public static double getPitch(BlockPos pos) {
        double diffX = pos.getX() + 0.5 - mc.player.getX();
        double diffY = pos.getY() + 0.5 - (mc.player.getY() + mc.player.getEyeHeight(mc.player.getPose()));
        double diffZ = pos.getZ() + 0.5 - mc.player.getZ();

        double diffXZ = Math.hypot(diffX, diffZ);

        return mc.player.pitch + MathHelper.wrapDegrees((float) -Math.toDegrees(Math.atan2(diffY, diffXZ)) - mc.player.pitch);
    }

    public static void rotateToCam() {
        mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.LookOnly(mc.player.yaw, mc.player.pitch, mc.player.isOnGround()));
        INSTANCE.setCamRotation(mc.player.yaw, mc.player.pitch);
    }

    @EventHandler
    private Listener<SendMovementPacketsEvent> onSendMovementPackets = new Listener<>(event ->  {
        if (event.era == NextEvent.Era.PRE) {
            if (mc.cameraEntity != mc.player) return;
            sentLastRotation = false;

            if (!rotations.isEmpty()) {
                resetLastRotation();

                Rotation rotation = rotations.get(i);
                setupMovementPacketRotation(rotation);

                if (rotations.size() > 1) rotationPool.free(rotation);

                i++;
            } else if (lastRotation != null) {
                if (lastRotationTimer >= 9) {
                    resetLastRotation();
                } else {
                    setupMovementPacketRotation(lastRotation);
                    sentLastRotation = true;

                    lastRotationTimer++;
                }
            }
        } else if (event.era == NextEvent.Era.POST) {
            if (!rotations.isEmpty()) {
                if (mc.cameraEntity == mc.player) {
                    rotations.get(i - 1).runCallback();

                    if (rotations.size() == 1) lastRotation = rotations.get(i - 1);

                    resetRotation();
                }

                for (; i < rotations.size(); i++) {
                    Rotation rotation = rotations.get(i);

                    setCamRotation(rotation.yaw, rotation.pitch);
                    if (rotation.clientSide) setClientSideRotation(rotation);
                    rotation.sendPacket();
                    if (rotation.clientSide) resetRotation();

                    if (i == rotations.size() - 1) lastRotation = rotation;
                    else rotationPool.free(rotation);
                }

                rotations.clear();
                i = 0;
            } else if (sentLastRotation) {
                resetRotation();
            }
        }
    });

    @EventHandler
    private Listener<TickEvent> onTick = new Listener<>(event -> {
        if (event.era == NextEvent.Era.PRE) {
            rotationTimer++;
        }
    });

    public void rotate(double yaw, double pitch, int priority, boolean clientSide, Runnable callback) {
        Rotation rotation = rotationPool.get();
        rotation.set(yaw, pitch, priority, clientSide, callback);

        int i = 0;
        for (; i < rotations.size(); i++) {
            if (priority > rotations.get(i).priority) break;
        }

        rotations.add(i, rotation);
    }

    public void rotate(double yaw, double pitch, int priority, Runnable callback) {
        rotate(yaw, pitch, priority, false, callback);
    }

    public void rotate(double yaw, double pitch, Runnable callback) {
        rotate(yaw, pitch, 0, callback);
    }

    public void rotate(double yaw, double pitch) {
        rotate(yaw, pitch, 0, null);
    }

    private void resetLastRotation() {
        if (lastRotation != null) {
            rotationPool.free(lastRotation);

            lastRotation = null;
            lastRotationTimer = 0;
        }
    }

    private void setupMovementPacketRotation(Rotation rotation) {
        setClientSideRotation(rotation);
        setCamRotation(rotation.yaw, rotation.pitch);
    }

    private void setClientSideRotation(Rotation rotation) {
        preYaw = mc.player.yaw;
        prePitch = mc.player.pitch;

        mc.player.yaw = (float) rotation.yaw;
        mc.player.pitch = (float) rotation.pitch;
    }

    private void resetRotation() {
        mc.player.yaw = preYaw;
        mc.player.pitch = prePitch;
    }

    public void setCamRotation(double yaw, double pitch) {
        serverYaw = (float) yaw;
        serverPitch = (float) pitch;
        rotationTimer = 0;
    }

    private static class Rotation {
        public double yaw, pitch;
        public int priority;
        public boolean clientSide;
        public Runnable callback;

        public void set(double yaw, double pitch, int priority, boolean clientSide, Runnable callback) {
            this.yaw = yaw;
            this.pitch = pitch;
            this.priority = priority;
            this.clientSide = clientSide;
            this.callback = callback;
        }

        public void sendPacket() {
            mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.LookOnly((float) yaw, (float) pitch, mc.player.isOnGround()));
            runCallback();
        }

        public void runCallback() {
            if (callback != null) callback.run();
        }
    }
}
