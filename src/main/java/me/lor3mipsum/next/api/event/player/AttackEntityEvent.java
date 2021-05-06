package me.lor3mipsum.next.api.event.player;

import me.lor3mipsum.next.api.event.NextEvent;
import net.minecraft.entity.Entity;

public class AttackEntityEvent extends NextEvent {
    public Entity entity;

    public AttackEntityEvent(Entity entity) {
        this.entity = entity;
    }

    public AttackEntityEvent(Entity entity, Era era) {
        super(era);
        this.entity = entity;
    }
}
