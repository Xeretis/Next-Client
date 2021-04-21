package me.lor3mipsum.next.client.impl.events;

import me.lor3mipsum.next.client.event.events.interfaces.IEvent;

public class TickEvent implements IEvent {
    public static class Pre extends TickEvent {
    }

    public static class Post extends TickEvent {
    }
}
