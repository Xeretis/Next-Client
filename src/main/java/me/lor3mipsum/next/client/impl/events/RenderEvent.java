package me.lor3mipsum.next.client.impl.events;

import me.lor3mipsum.next.client.event.events.Cancellable;

public class RenderEvent extends Cancellable {
    private String name;

    public RenderEvent(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
