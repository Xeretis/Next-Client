package me.lor3mipsum.next.api.event;

import me.zero.alpine.event.type.Cancellable;

public class NextEvent extends Cancellable {
    private Era era = Era.PRE;

    public NextEvent() { }

    public NextEvent(Era era) {
        this.era = era;
    }

    public enum Era {
        PRE,
        PERI,
        POST
    }
}
