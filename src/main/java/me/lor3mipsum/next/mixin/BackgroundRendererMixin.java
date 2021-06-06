package me.lor3mipsum.next.mixin;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import me.lor3mipsum.next.Main;
import me.lor3mipsum.next.client.impl.modules.render.NoRender;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.Camera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BackgroundRenderer.class)
public class BackgroundRendererMixin {
    @Inject(method = "applyFog", at = @At("TAIL"), cancellable = true)
    private static void onApplyFog(Camera camera, BackgroundRenderer.FogType fogType, float viewDistance, boolean thickFog, CallbackInfo info) {
        if (Main.moduleManager.getModule(NoRender.class).getEnabled() && Main.moduleManager.getModule(NoRender.class).fog.getValue()) {
            if (fogType == BackgroundRenderer.FogType.FOG_TERRAIN) {
                RenderSystem.fogStart(viewDistance * 4f);
                RenderSystem.fogEnd(viewDistance * 4.25f);
                RenderSystem.fogMode(GlStateManager.FogMode.LINEAR);
                RenderSystem.setupNvFogDistance();
            }
        }
    }
}
