package me.lor3mipsum.next.mixin;

import me.lor3mipsum.next.client.event.EventManager;
import me.lor3mipsum.next.client.impl.events.RenderEvent;
import me.lor3mipsum.next.client.impl.events.RenderOverlayEvent;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {
    @Inject(method = "renderHotbar", at = @At("HEAD"), cancellable = true)
    private void onRenderHotbar(float tickDelta, MatrixStack matrices, CallbackInfo info) {
        EventManager.call(new RenderEvent());
    }

    @Inject(method = "renderPumpkinOverlay", at = @At("HEAD"), cancellable = true)
    private void onRenderPumpkinOverlay(CallbackInfo ci) {
        RenderOverlayEvent event = new RenderOverlayEvent(1f);
        EventManager.call(event);

        if (event.isCancelled()) {
            ci.cancel();
        }
    }
}
