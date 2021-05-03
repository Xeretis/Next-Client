package me.lor3mipsum.next.client.impl.modules.movement;

import me.lor3mipsum.next.client.event.EventTarget;
import me.lor3mipsum.next.client.impl.events.TickEvent;
import me.lor3mipsum.next.client.impl.settings.BooleanSetting;
import me.lor3mipsum.next.client.impl.settings.KeybindSetting;
import me.lor3mipsum.next.client.impl.settings.ModeSetting;
import me.lor3mipsum.next.client.impl.settings.NumberSetting;
import me.lor3mipsum.next.client.module.Category;
import me.lor3mipsum.next.client.module.Module;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;

public class SpeedModule extends Module {

    public ModeSetting mode = new ModeSetting("Mode", "Strafe", "Strafe", "OnGround", "MiniHop", "BHop");
    public NumberSetting strafe = new NumberSetting("StrafeSpeed", 2.7, 1.4,  4, 0.1);
    public NumberSetting speed = new NumberSetting("Speed", 2, 0.1, 10, 0.01);
    public BooleanSetting sneak = new BooleanSetting("StopOnSneak", true);
    public KeybindSetting keybind = new KeybindSetting(GLFW.GLFW_KEY_UNKNOWN);

    private boolean jumping;

    public SpeedModule() {
        super("Speed", "Makes you a fast boi", Category.MOVEMENT);
    }

    @EventTarget
    public void onTick(TickEvent.Post event) {
        if (mc.options.keySneak.isPressed() && sneak.isOn())
            return;

        if (mode.getMode() == "Strafe") {
            if ((mc.player.forwardSpeed != 0 || mc.player.sidewaysSpeed != 0) /*&& mc.player.isOnGround()*/) {
                if (!mc.player.isSprinting()) {
                    mc.player.networkHandler.sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.START_SPRINTING));
                }

                mc.player.setVelocity(new Vec3d(0, mc.player.getVelocity().y, 0));
                mc.player.updateVelocity((float) strafe.getNumber() / 10,
                        new Vec3d(mc.player.sidewaysSpeed, 0, mc.player.forwardSpeed));

                double vel = Math.abs(mc.player.getVelocity().getX()) + Math.abs(mc.player.getVelocity().getZ());

                if (mc.player.isOnGround()) {
                    mc.player.updateVelocity(vel >= 0.3 ? 0.0f : 0.15f, new Vec3d(mc.player.sidewaysSpeed, 0, mc.player.forwardSpeed));
                    mc.player.jump();
                }
            }

            /* OnGround */
        } else if (mode.getMode() == "OnGround") {
            if (mc.options.keyJump.isPressed() || mc.player.fallDistance > 0.25)
                return;

            double speeds = 0.85 + speed.getNumber() / 30;

            if (jumping && mc.player.getY() >= mc.player.prevY + 0.399994D) {
                mc.player.setVelocity(mc.player.getVelocity().x, -0.9, mc.player.getVelocity().z);
                mc.player.setPos(mc.player.getX(), mc.player.prevY, mc.player.getZ());
                jumping = false;
            }

            if (mc.player.forwardSpeed != 0.0F && !mc.player.horizontalCollision) {
                if (mc.player.verticalCollision) {
                    mc.player.setVelocity(mc.player.getVelocity().x * speeds, mc.player.getVelocity().y, mc.player.getVelocity().z * speeds);
                    jumping = true;
                    mc.player.jump();
                    // 1.0379
                }

                if (jumping && mc.player.getY() >= mc.player.prevY + 0.399994D) {
                    mc.player.setVelocity(mc.player.getVelocity().x, -100, mc.player.getVelocity().z);
                    jumping = false;
                }

            }

            /* MiniHop */
        } else if (mode.getMode() == "MiniHop") {
            if (mc.player.horizontalCollision || mc.options.keyJump.isPressed() || mc.player.forwardSpeed == 0)
                return;

            double speeds = 0.9 + speed.getNumber() / 30;

            if (mc.player.isOnGround()) {
                mc.player.jump();
            } else if (mc.player.getVelocity().y > 0) {
                mc.player.setVelocity(mc.player.getVelocity().x * speeds, -1, mc.player.getVelocity().z * speeds);
                mc.player.input.movementSideways += 1.5F;
            }

            /* Bhop */
        } else if (mode.getMode() == "BHop") {
            if (mc.player.forwardSpeed > 0 && mc.player.isOnGround()) {
                double speeds = 0.65 + speed.getNumber() / 30;

                mc.player.jump();
                mc.player.setVelocity(mc.player.getVelocity().x * speeds, 0.255556, mc.player.getVelocity().z * speeds);
                mc.player.sidewaysSpeed += 3.0F;
                mc.player.jump();
                mc.player.setSprinting(true);
            }
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
        return "[" + Formatting.WHITE + mode.getMode() + Formatting.GRAY + "]";
    }

}

