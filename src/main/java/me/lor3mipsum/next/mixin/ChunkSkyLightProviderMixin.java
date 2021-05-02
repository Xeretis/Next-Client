package me.lor3mipsum.next.mixin;

import me.lor3mipsum.next.Next;
import me.lor3mipsum.next.client.impl.modules.render.NoRender;
import net.minecraft.world.chunk.light.ChunkSkyLightProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChunkSkyLightProvider.class)
public class ChunkSkyLightProviderMixin {
    @Inject(method = "recalculateLevel", at = @At("HEAD"), cancellable = true)
    protected void recalculateLevel(long id, long excludedId, int maxLevel, CallbackInfoReturnable<Integer> ci) {
        if (Next.INSTANCE.moduleManager.getModule(NoRender.class).isOn() && Next.INSTANCE.moduleManager.getModule(NoRender.class).skylight.isOn()) {
            ci.setReturnValue(15);
        }
    }
}
