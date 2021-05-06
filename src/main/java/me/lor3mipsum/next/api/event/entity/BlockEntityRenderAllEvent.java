package me.lor3mipsum.next.api.event.entity;

import me.lor3mipsum.next.api.event.NextEvent;

public class BlockEntityRenderAllEvent extends NextEvent {
    public BlockEntityRenderAllEvent() {

    }

    public BlockEntityRenderAllEvent(Era era) {
        super(era);
    }
}
