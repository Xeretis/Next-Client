package me.lor3mipsum.next.api.event.entity;

import me.lor3mipsum.next.api.event.NextEvent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Vec3d;

public class LivingEntityMoveEvent extends NextEvent {
    public LivingEntity entity;
    public Vec3d movement;

    public LivingEntityMoveEvent(LivingEntity entity, Vec3d movement) {
        this.entity = entity;
        this.movement = movement;
    }

    public LivingEntityMoveEvent(LivingEntity entity, Vec3d movement, Era era) {
        super(era);
        this.entity = entity;
        this.movement = movement;
    }
}
