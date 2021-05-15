package me.lor3mipsum.next.api.event.client;

import me.lor3mipsum.next.api.event.NextEvent;

public class TickEvent extends NextEvent {

    public TickEvent() {
    }

    public TickEvent(Era era) {
        super(era);
    }
}
