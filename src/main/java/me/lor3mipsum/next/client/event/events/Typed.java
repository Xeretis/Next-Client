package me.lor3mipsum.next.client.event.events;

import me.lor3mipsum.next.client.event.events.interfaces.IEvent;
import me.lor3mipsum.next.client.event.events.interfaces.ITyped;

public abstract class Typed implements IEvent, ITyped {
    private final byte type;

    protected Typed(byte eventType) {
        type = eventType;
    }

    @Override
    public byte getType() {
        return type;
    }
}
