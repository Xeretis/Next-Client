package me.lor3mipsum.next.api.event.entity;

import me.lor3mipsum.next.api.event.NextEvent;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;

public class BlockEntityRenderEvent extends NextEvent {
    public BlockEntity blockEntity;
    public MatrixStack matrices;
    public VertexConsumerProvider vertex;

    public BlockEntityRenderEvent(BlockEntity blockEntity, MatrixStack matrices, VertexConsumerProvider vertex) {
        this.blockEntity = blockEntity;
        this.matrices = matrices;
        this.vertex = vertex;
    }

    public BlockEntityRenderEvent(BlockEntity blockEntity, MatrixStack matrices, VertexConsumerProvider vertex, Era era) {
        super(era);
        this.blockEntity = blockEntity;
        this.matrices = matrices;
        this.vertex = vertex;
    }
}
