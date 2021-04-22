package me.lor3mipsum.next.mixin;

import me.lor3mipsum.next.Next;
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

    @Shadow public abstract void setVelocity(double x, double y, double z);

    @Inject(method = "setVelocityClient", at = @At("HEAD"), cancellable = true)
    private void onSetVelocityClient(double x, double y, double z, CallbackInfo info) {
        PlayerEntity player = MinecraftClient.getInstance().player;
        if (((Object) this) != player) return;

        Velocity velocity = Next.INSTANCE.moduleManager.getModule(Velocity.class);
        if (!velocity.isOn() || velocity.explosionOnly.isOn()) return;

        double deltaX = x - player.getVelocity().x;
        double deltaY = y - player.getVelocity().y;
        double deltaZ = z - player.getVelocity().z;

        setVelocity(
                player.getVelocity().x + deltaX * velocity.getHorizontal(),
                player.getVelocity().y + deltaY * velocity.getVertical(),
                player.getVelocity().z + deltaZ * velocity.getHorizontal()
        );

        info.cancel();
    }

    @Redirect(method = "updateMovementInFluid", at = @At(value = "INVOKE", target = "Lnet/minecraft/fluid/FluidState;getVelocity(Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/util/math/Vec3d;"))
    private Vec3d updateMovementInFluidFluidStateGetVelocity(FluidState state, BlockView world, BlockPos pos) {
        Vec3d vec = state.getVelocity(world, pos);

        Velocity velocity = Next.INSTANCE.moduleManager.getModule(Velocity.class);
        if (velocity.isOn() && !velocity.explosionOnly.isOn()) {
            vec = vec.multiply(velocity.getHorizontal(), velocity.getVertical(), velocity.getHorizontal());
        }

        return vec;
    }

    @Redirect(method = "addVelocity", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/Vec3d;add(DDD)Lnet/minecraft/util/math/Vec3d;"))
    private Vec3d addVelocityVec3dAddProxy(Vec3d vec3d, double x, double y, double z) {
        Velocity velocity = Next.INSTANCE.moduleManager.getModule(Velocity.class);

        if ((Object) this != MinecraftClient.getInstance().player || Utils.isReleasingTrident || velocity.explosionOnly.isOn()) return vec3d.add(x, y, z);

        return vec3d.add(x * velocity.getHorizontal(), y * velocity.getVertical(), z * velocity.getHorizontal());
    }
}
