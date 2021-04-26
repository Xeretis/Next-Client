package me.lor3mipsum.next.client.impl.events;

import me.lor3mipsum.next.client.event.events.Cancellable;
import me.lor3mipsum.next.client.event.events.interfaces.IEvent;
import net.minecraft.entity.MovementType;
import net.minecraft.util.math.Vec3d;

public class ClientMoveEvent extends Cancellable {
    public MovementType type;
    public Vec3d vec3d;

    public ClientMoveEvent(MovementType type, Vec3d vec3d) {
        this.type = type;
        this.vec3d = vec3d;
    }
}
