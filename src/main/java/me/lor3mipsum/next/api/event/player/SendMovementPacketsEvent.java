package me.lor3mipsum.next.api.event.player;

import me.lor3mipsum.next.api.event.NextEvent;

public class SendMovementPacketsEvent extends NextEvent {
    public SendMovementPacketsEvent() {

    }

    public SendMovementPacketsEvent(Era era) {
        super(era);
    }
}
