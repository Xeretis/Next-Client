package me.lor3mipsum.next.mixin;

import me.lor3mipsum.next.Next;
import me.lor3mipsum.next.client.impl.modules.render.NoRender;
import net.minecraft.client.gui.hud.BossBarHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BossBarHud.class)
public class BossBarHudMixin {
    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void render(CallbackInfo info) {
        if (Next.INSTANCE.moduleManager.getModule(NoRender.class).isOn() && Next.INSTANCE.moduleManager.getModule(NoRender.class).bossbar.isOn()) {
            info.cancel();
        }
    }
}
