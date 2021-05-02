package me.lor3mipsum.next.client.impl.events;

import me.lor3mipsum.next.client.event.events.interfaces.IEvent;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class SetBlockStateEvent implements IEvent {
    public BlockPos pos;
    public BlockState oldState, newState;

    public SetBlockStateEvent(BlockPos pos, BlockState oldState, BlockState newState) {
        this.pos = pos;
        this.oldState = oldState;
        this.newState = newState;
    }
}
