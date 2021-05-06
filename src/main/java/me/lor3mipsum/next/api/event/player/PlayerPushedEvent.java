package me.lor3mipsum.next.api.event.player;

import me.lor3mipsum.next.api.event.NextEvent;
import net.minecraft.util.math.Vec3d;

public class PlayerPushedEvent extends NextEvent {
    public Vec3d push;

    public PlayerPushedEvent(Vec3d push) {
        this.push = push;
    }

    public PlayerPushedEvent(Vec3d push, Era era) {
        super(era);
        this.push = push;
    }
}
