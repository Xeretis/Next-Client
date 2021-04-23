package me.lor3mipsum.next.mixin;

import me.lor3mipsum.next.Next;
import me.lor3mipsum.next.client.event.EventManager;
import me.lor3mipsum.next.client.impl.events.PlayerPushedEvent;
import me.lor3mipsum.next.client.impl.modules.movement.Velocity;
import me.lor3mipsum.next.client.utils.Utils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityMixin {

    @Shadow public void addVelocity(double deltaX, double deltaY, double deltaZ) {}

    @Redirect(method = "pushAwayFrom", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;addVelocity(DDD)V"))
    private void pushAwayFrom_addVelocity(Entity entity, double deltaX, double deltaY, double deltaZ) {
        if (entity == MinecraftClient.getInstance().player) {
            PlayerPushedEvent event = new PlayerPushedEvent(new Vec3d(deltaX, deltaY, deltaZ));
            EventManager.call(event);

            addVelocity(event.getPush().x, event.getPush().y, event.getPush().z);
        }
    }
}
