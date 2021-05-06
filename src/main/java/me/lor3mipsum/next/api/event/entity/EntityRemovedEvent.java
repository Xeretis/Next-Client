package me.lor3mipsum.next.api.event.entity;

import me.lor3mipsum.next.api.event.NextEvent;
import net.minecraft.entity.Entity;

public class EntityRemovedEvent extends NextEvent {
    public Entity entity;

    public EntityRemovedEvent(Entity entity) {
        this.entity = entity;
    }

    public EntityRemovedEvent(Entity entity, Era era) {
        super(era);
        this.entity = entity;
    }
}
