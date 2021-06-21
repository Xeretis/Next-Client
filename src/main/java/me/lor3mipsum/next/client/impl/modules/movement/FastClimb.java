package me.lor3mipsum.next.client.impl.modules.movement;

import me.lor3mipsum.next.api.event.NextEvent;
import me.lor3mipsum.next.api.event.client.TickEvent;
import me.lor3mipsum.next.client.core.module.Category;
import me.lor3mipsum.next.client.core.module.Module;
import me.lor3mipsum.next.client.core.module.annotation.Mod;
import me.lor3mipsum.next.client.core.setting.SettingSeparator;
import me.lor3mipsum.next.client.impl.settings.DoubleSetting;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.util.math.Vec3d;

@Mod(name = "FastClimb", description = "Makes you climb faster", category = Category.MOVEMENT)
public class FastClimb extends Module {

    public DoubleSetting speed = new DoubleSetting("Speed", 2.8, 0, 10);

    @EventHandler
    private Listener<TickEvent> onTick = new Listener<>(event -> {

        if (mc.player == null || mc.world == null) return;

        if (!mc.player.isClimbing() || !mc.player.horizontalCollision) return;
        if (mc.player.input.movementForward == 0 && mc.player.input.movementSideways == 0) return;

        Vec3d velocity = mc.player.getVelocity();
        mc.player.setVelocity(velocity.getX(), speed.getValue() / 10, velocity.getZ());

    }, event -> event.era == NextEvent.Era.POST);

}
