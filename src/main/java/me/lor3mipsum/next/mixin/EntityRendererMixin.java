package me.lor3mipsum.next.mixin;

import me.lor3mipsum.next.Main;
import me.lor3mipsum.next.api.event.entity.EntityRenderLabelEvent;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public class EntityRendererMixin<T extends Entity> {
    @Inject(method = "renderLabelIfPresent", at = @At("HEAD"), cancellable = true)
    public void renderLabelIfPresent(T entity, Text text, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo info) {
        EntityRenderLabelEvent event = new EntityRenderLabelEvent(entity, matrices, vertexConsumers);

        Main.EVENT_BUS.post(event);

        if (event.isCancelled()) {
            info.cancel();
        }
    }
}
