package me.lor3mipsum.next.api.event.player;

import me.lor3mipsum.next.api.event.NextEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class AttackBlockEvent extends NextEvent {
    public BlockPos blockPos;
    public Direction direction;

    public AttackBlockEvent(BlockPos blockPos, Direction direction) {
        this.blockPos = blockPos;
        this.direction = direction;
    }

    public AttackBlockEvent(BlockPos blockPos, Direction direction, Era era) {
        super(era);
        this.blockPos = blockPos;
        this.direction = direction;
    }
}
