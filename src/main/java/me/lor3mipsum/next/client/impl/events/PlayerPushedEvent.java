package me.lor3mipsum.next.client.impl.events;

import me.lor3mipsum.next.client.event.events.interfaces.IEvent;
import net.minecraft.util.math.Vec3d;

public class PlayerPushedEvent implements IEvent {
    private Vec3d push;

    public PlayerPushedEvent(Vec3d push) {
        this.push = push;
    }

    public Vec3d getPush() {
        return push;
    }

    public void setPush(Vec3d push) {
        this.push = push;
    }
}
