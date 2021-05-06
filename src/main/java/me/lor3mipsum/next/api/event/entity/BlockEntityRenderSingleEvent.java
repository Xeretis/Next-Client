package me.lor3mipsum.next.api.event.entity;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;

public class BlockEntityRenderSingleEvent extends BlockEntityRenderEvent {
    public BlockEntityRenderSingleEvent(BlockEntity blockEntity, MatrixStack matrices, VertexConsumerProvider vertex) {
        super(blockEntity, matrices, vertex);
    }

    public BlockEntityRenderSingleEvent(BlockEntity blockEntity, MatrixStack matrices, VertexConsumerProvider vertex, Era era) {
        super(blockEntity, matrices, vertex, era);
    }
}
