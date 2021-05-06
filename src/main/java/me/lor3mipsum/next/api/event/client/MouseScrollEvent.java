package me.lor3mipsum.next.api.event.client;

import me.lor3mipsum.next.api.event.NextEvent;

public class MouseScrollEvent extends NextEvent {
    public double value;

    public MouseScrollEvent(double value) {
        this.value = value;
    }

    public MouseScrollEvent(double value, Era era) {
        super(era);
        this.value = value;
    }
}
