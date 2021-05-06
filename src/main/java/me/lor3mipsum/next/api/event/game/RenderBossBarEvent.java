package me.lor3mipsum.next.api.event.game;

import me.lor3mipsum.next.api.event.NextEvent;

public class RenderBossBarEvent extends NextEvent {
    public RenderBossBarEvent() {

    }

    public RenderBossBarEvent(Era era) {
        super(era);
    }
}
