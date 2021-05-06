package me.lor3mipsum.next.api.event.entity;

import me.lor3mipsum.next.api.event.NextEvent;
import net.minecraft.entity.Entity;

public class EntityAddedEvent extends NextEvent {
    public Entity entity;

    public EntityAddedEvent(Entity entity) {
        this.entity = entity;
    }

    public EntityAddedEvent(Entity entity, Era era) {
        super(era);
        this.entity = entity;
    }
}
