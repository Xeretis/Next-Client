package me.lor3mipsum.next.api.event.game;

import me.lor3mipsum.next.api.event.NextEvent;

public class RenderEvent extends NextEvent {
    public RenderEvent() {

    }

    public RenderEvent(Era era) {
        super(era);
    }
}
