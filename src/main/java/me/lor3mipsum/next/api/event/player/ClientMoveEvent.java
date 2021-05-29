package me.lor3mipsum.next.api.event.player;

import me.lor3mipsum.next.api.event.NextEvent;
import net.minecraft.entity.MovementType;
import net.minecraft.util.math.Vec3d;

public class ClientMoveEvent extends NextEvent {
    public MovementType type;
    public Vec3d vec3d;

    public ClientMoveEvent(MovementType type, Vec3d vec3d) {
        this.type = type;
        this.vec3d = vec3d;
    }

    public ClientMoveEvent(MovementType type, Vec3d vec3d, Era era) {
        super(era);
        this.type = type;
        this.vec3d = vec3d;
    }
}
