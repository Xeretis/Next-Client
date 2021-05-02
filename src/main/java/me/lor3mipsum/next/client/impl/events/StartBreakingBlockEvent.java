package me.lor3mipsum.next.client.impl.events;

import me.lor3mipsum.next.client.event.events.Cancellable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class StartBreakingBlockEvent extends Cancellable {

    public BlockPos blockPos;
    public Direction direction;

    public StartBreakingBlockEvent(BlockPos blockPos, Direction direction) {
        this.setCancelled(false);
        this.blockPos = blockPos;
        this.direction = direction;
    }

}
