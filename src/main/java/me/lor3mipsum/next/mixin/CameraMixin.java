package me.lor3mipsum.next.mixin;

import me.lor3mipsum.next.Next;
import me.lor3mipsum.next.client.impl.modules.render.NoRender;
import net.minecraft.client.render.Camera;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Camera.class)
public class CameraMixin {
    @Inject(method = "getSubmergedFluidState", at = @At("HEAD"), cancellable = true)
    private void getSubmergedFluidState(CallbackInfoReturnable<FluidState> cir) {
        if (Next.INSTANCE.moduleManager.getModule(NoRender.class).isOn() && Next.INSTANCE.moduleManager.getModule(NoRender.class).liquid.isOn()) {
            cir.setReturnValue(Fluids.EMPTY.getDefaultState());
        }
    }
}
