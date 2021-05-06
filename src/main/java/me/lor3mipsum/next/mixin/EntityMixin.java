package me.lor3mipsum.next.mixin;

import me.lor3mipsum.next.Main;
import me.lor3mipsum.next.api.event.entity.LivingEntityMoveEvent;
import me.lor3mipsum.next.api.event.player.PlayerMoveEvent;
import me.lor3mipsum.next.api.event.player.PlayerPushedEvent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public class EntityMixin {

    @Shadow public void addVelocity(double deltaX, double deltaY, double deltaZ) {}

    @Redirect(method = "pushAwayFrom", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;addVelocity(DDD)V"))
    private void pushAwayFrom_addVelocity(Entity entity, double deltaX, double deltaY, double deltaZ) {
        if (entity == MinecraftClient.getInstance().player) {
            PlayerPushedEvent event = new PlayerPushedEvent(new Vec3d(deltaX, deltaY, deltaZ));

            Main.EVENT_BUS.post(event);

            addVelocity(event.push.x, event.push.y, event.push.z);
        }
    }

    @Inject(method = "move", at = @At("HEAD"))
    private void onMove(MovementType type, Vec3d movement, CallbackInfo info) {
        if ((Object) this == MinecraftClient.getInstance().player) {
            Main.EVENT_BUS.post(new PlayerMoveEvent(type, movement));
        } else if ((Object) this instanceof LivingEntity) {
            Main.EVENT_BUS.post(new LivingEntityMoveEvent((LivingEntity) (Object) this, movement));
        }
    }
}
