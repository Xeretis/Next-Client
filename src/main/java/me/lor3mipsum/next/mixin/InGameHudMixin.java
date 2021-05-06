package me.lor3mipsum.next.mixin;

import me.lor3mipsum.next.Main;
import me.lor3mipsum.next.api.event.game.RenderEvent;
import me.lor3mipsum.next.api.event.game.RenderOverlayEvent;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin {
    @Inject(method = "renderHotbar", at = @At("HEAD"), cancellable = true)
    private void onRenderHotbar(float tickDelta, MatrixStack matrices, CallbackInfo info) {
        Main.EVENT_BUS.post(new RenderEvent());
    }

    @Inject(method = "renderPumpkinOverlay", at = @At("HEAD"), cancellable = true)
    private void onRenderPumpkinOverlay(CallbackInfo info) {
        RenderOverlayEvent event = new RenderOverlayEvent(new Identifier("textures/misc/pumpkinblur.png"), 1f);

        Main.EVENT_BUS.post(event);

        if (event.isCancelled())
            info.cancel();
    }
}
