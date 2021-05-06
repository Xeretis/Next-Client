package me.lor3mipsum.next.api.event.world;

import me.lor3mipsum.next.api.event.NextEvent;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class SetBlockStateEvent extends NextEvent {
    public BlockPos pos;
    public BlockState oldState, newState;

    public SetBlockStateEvent(BlockPos pos, BlockState oldState, BlockState newState) {
        this.pos = pos;
        this.oldState = oldState;
        this.newState = newState;
    }

    public SetBlockStateEvent(BlockPos pos, BlockState oldState, BlockState newState, Era era) {
        super(era);
        this.pos = pos;
        this.oldState = oldState;
        this.newState = newState;
    }
}
