package me.lor3mipsum.next.api.event.entity;

import me.lor3mipsum.next.api.event.NextEvent;
import net.minecraft.entity.Entity;

public class EntityDestroyEvent extends NextEvent {
    public Entity entity;

    public EntityDestroyEvent(Entity entity) {
        this.entity = entity;
    }

    public EntityDestroyEvent(Entity entity, Era era) {
        super(era);
        this.entity = entity;
    }
}
