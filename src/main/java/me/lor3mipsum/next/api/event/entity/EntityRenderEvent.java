package me.lor3mipsum.next.api.event.entity;

import me.lor3mipsum.next.api.event.NextEvent;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;

public class EntityRenderEvent extends NextEvent {
    public Entity entity;
    public MatrixStack matrix;
    public VertexConsumerProvider vertex;

    public EntityRenderEvent(Entity entity, MatrixStack matrix, VertexConsumerProvider vertex) {
        this.entity = entity;
        this.matrix = matrix;
        this.vertex = vertex;
    }

    public EntityRenderEvent(Entity entity, MatrixStack matrix, VertexConsumerProvider vertex, Era era) {
        super(era);
        this.entity = entity;
        this.matrix = matrix;
        this.vertex = vertex;
    }

}
