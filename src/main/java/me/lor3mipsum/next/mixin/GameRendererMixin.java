package me.lor3mipsum.next.mixin;

import me.lor3mipsum.next.Next;
import me.lor3mipsum.next.client.impl.modules.exploit.NoEntityTrace;
import me.lor3mipsum.next.client.impl.modules.render.NoRender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.ShaderEffect;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Shadow @Final
    private MinecraftClient client;

    @Inject(method = "bobViewWhenHurt", at = @At("HEAD"), cancellable = true)
    private void onBobViewWhenHurt(MatrixStack matrixStack, float f, CallbackInfo ci) {
        if (Next.INSTANCE.moduleManager.getModule(NoRender.class).isOn() && Next.INSTANCE.moduleManager.getModule(NoRender.class).hurtcam.isOn()) {
            ci.cancel();
        }
    }

    @Inject(method = "showFloatingItem", at = @At("HEAD"), cancellable = true)
    private void showFloatingItem(ItemStack floatingItem, CallbackInfo ci) {
        if (Next.INSTANCE.moduleManager.getModule(NoRender.class).isOn() && Next.INSTANCE.moduleManager.getModule(NoRender.class).totem.isOn()
                && floatingItem.getItem() == Items.TOTEM_OF_UNDYING) {
            ci.cancel();
        }
    }

    @Redirect(method = "renderWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/MathHelper;lerp(FFF)F", ordinal = 0),
            require = 0)
    private float nauseaWobble(float delta, float first, float second) {
        if (!(Next.INSTANCE.moduleManager.getModule(NoRender.class).isOn() && Next.INSTANCE.moduleManager.getModule(NoRender.class).wobble.isOn())) {
            return MathHelper.lerp(delta, first, second);
        }

        return 0;
    }

    @Inject(method = "updateTargetedEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/projectile/ProjectileUtil;raycast(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Box;Ljava/util/function/Predicate;D)Lnet/minecraft/util/hit/EntityHitResult;"), cancellable = true)
    private void onUpdateTargetedEntity(float tickDelta, CallbackInfo info) {
        if (Next.INSTANCE.moduleManager.getModule(NoEntityTrace.class).canWork() && client.crosshairTarget.getType() == HitResult.Type.BLOCK) {
            client.getProfiler().pop();
            info.cancel();
        }
    }
}
