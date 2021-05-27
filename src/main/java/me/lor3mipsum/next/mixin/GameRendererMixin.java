package me.lor3mipsum.next.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import me.lor3mipsum.next.Main;
import me.lor3mipsum.next.api.event.game.RenderFloatingTotemEvent;
import me.lor3mipsum.next.api.event.game.RenderHurtcamEvent;
import me.lor3mipsum.next.api.event.game.RenderShaderEvent;
import me.lor3mipsum.next.client.impl.modules.exploit.NoEntityTrace;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.ShaderEffect;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.HitResult;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Shadow private ShaderEffect shader;

    @Shadow @Final private MinecraftClient client;

    @Inject(method = "bobViewWhenHurt", at = @At("HEAD"), cancellable = true)
    private void onBobViewWhenHurt(MatrixStack matrixStack, float f, CallbackInfo info) {
        RenderHurtcamEvent event = new RenderHurtcamEvent();

        Main.EVENT_BUS.post(event);

        if(event.isCancelled())
            info.cancel();
    }

    @Inject(method = "showFloatingItem", at = @At("HEAD"), cancellable = true)
    private void showFloatingItem(ItemStack floatingItem, CallbackInfo info) {
        RenderFloatingTotemEvent event = new RenderFloatingTotemEvent();

        Main.EVENT_BUS.post(event);

        if(event.isCancelled())
            info.cancel();
    }

    @Redirect(method = "render", at = @At(value = "FIELD", target = "Lnet/minecraft/client/render/GameRenderer;shader:Lnet/minecraft/client/gl/ShaderEffect;", ordinal = 0))
    private ShaderEffect render_Shader(GameRenderer renderer, float tickDelta) {
        RenderShaderEvent event = new RenderShaderEvent(shader);

        Main.EVENT_BUS.post(event);

        if (event.effect != null) {
            RenderSystem.disableBlend();
            RenderSystem.disableDepthTest();
            RenderSystem.disableAlphaTest();
            RenderSystem.enableTexture();
            RenderSystem.matrixMode(GL11.GL_TEXTURE);
            RenderSystem.pushMatrix();
            RenderSystem.loadIdentity();
            event.effect.render(tickDelta);
            RenderSystem.popMatrix();
        }

        return null;
    }

    @Inject(method = "updateTargetedEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/projectile/ProjectileUtil;raycast(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Box;Ljava/util/function/Predicate;D)Lnet/minecraft/util/hit/EntityHitResult;"), cancellable = true)
    private void onUpdateTargetedEntity(float tickDelta, CallbackInfo info) {
        if (Main.moduleManager.getModule(NoEntityTrace.class).canWork() && client.crosshairTarget.getType() == HitResult.Type.BLOCK) {
            client.getProfiler().pop();
            info.cancel();
        }
    }
}
