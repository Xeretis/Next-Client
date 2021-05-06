package me.lor3mipsum.next.mixin;

import me.lor3mipsum.next.Main;
import me.lor3mipsum.next.api.event.game.RenderBossBarEvent;
import net.minecraft.client.gui.hud.BossBarHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BossBarHud.class)
public class BossBarHudMixin {
    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void render(CallbackInfo info) {
        RenderBossBarEvent event = new RenderBossBarEvent();

        Main.EVENT_BUS.post(event);

        if(event.isCancelled())
            info.cancel();
    }
}
