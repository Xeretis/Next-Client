package me.lor3mipsum.next.api.event.entity;

import me.lor3mipsum.next.api.event.NextEvent;

public class EntityRenderAllEvent extends NextEvent {
    public EntityRenderAllEvent() {

    }

    public EntityRenderAllEvent(Era era) {
        super(era);
    }
}
