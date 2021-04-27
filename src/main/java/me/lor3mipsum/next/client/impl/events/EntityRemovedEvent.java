package me.lor3mipsum.next.client.impl.events;

import me.lor3mipsum.next.client.event.events.interfaces.IEvent;
import net.minecraft.entity.Entity;

public class EntityRemovedEvent implements IEvent {
    private Entity entity;

    public EntityRemovedEvent(Entity entity) {
        this.entity = entity;
    }

    public Entity getEntity() {
        return entity;
    }
}
