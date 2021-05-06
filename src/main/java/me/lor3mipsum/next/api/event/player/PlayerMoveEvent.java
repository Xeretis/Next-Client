package me.lor3mipsum.next.api.event.player;

import me.lor3mipsum.next.api.event.NextEvent;
import net.minecraft.entity.MovementType;
import net.minecraft.util.math.Vec3d;

public class PlayerMoveEvent extends NextEvent {
    public MovementType type;
    public Vec3d movement;

    public PlayerMoveEvent(MovementType type, Vec3d movement) {
        this.type = type;
        this.movement = movement;
    }

    public PlayerMoveEvent(MovementType type, Vec3d movement, Era era) {
        super(era);
        this.type = type;
        this.movement = movement;
    }
}
