package me.lor3mipsum.next.mixin;

import me.lor3mipsum.next.Next;
import me.lor3mipsum.next.client.impl.modules.render.NoRender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameOverlayRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameOverlayRenderer.class)
public class InGameOverlayRendererMixin {
    @Inject(method = "renderFireOverlay", at = @At("HEAD"), cancellable = true)
    private static void onRenderFireOverlay(MinecraftClient minecraftClient, MatrixStack matrixStack, CallbackInfo ci) {
        if (Next.INSTANCE.moduleManager.getModule(NoRender.class).isOn() && Next.INSTANCE.moduleManager.getModule(NoRender.class).fire.isOn()) {
            ci.cancel();
        }
    }

    @Inject(method = "renderUnderwaterOverlay", at = @At("HEAD"), cancellable = true)
    private static void onRenderUnderwaterOverlay(MinecraftClient minecraftClient, MatrixStack matrixStack, CallbackInfo ci) {
        if (Next.INSTANCE.moduleManager.getModule(NoRender.class).isOn() && Next.INSTANCE.moduleManager.getModule(NoRender.class).liquid.isOn()) {
            ci.cancel();
        }
    }
}
