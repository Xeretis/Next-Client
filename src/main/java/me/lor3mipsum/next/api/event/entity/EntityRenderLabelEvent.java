package me.lor3mipsum.next.api.event.entity;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;

public class EntityRenderLabelEvent extends EntityRenderEvent{

    public EntityRenderLabelEvent(Entity entity, MatrixStack matrix, VertexConsumerProvider vertex) {
        super(entity, matrix, vertex);
    }

    public EntityRenderLabelEvent(Entity entity, MatrixStack matrix, VertexConsumerProvider vertex, Era era) {
        super(entity, matrix, vertex, era);
    }

}
