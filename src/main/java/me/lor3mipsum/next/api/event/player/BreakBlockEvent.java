package me.lor3mipsum.next.api.event.player;

import me.lor3mipsum.next.api.event.NextEvent;
import net.minecraft.util.math.BlockPos;

public class BreakBlockEvent extends NextEvent {
    public BlockPos blockPos;

    public BreakBlockEvent(BlockPos blockPos) {
        this.blockPos = blockPos;
    }

    public BreakBlockEvent(BlockPos blockPos, Era era) {
        super(era);
        this.blockPos = blockPos;
    }
}
