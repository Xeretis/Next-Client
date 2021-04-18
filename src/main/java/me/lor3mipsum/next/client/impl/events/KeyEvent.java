package me.lor3mipsum.next.client.impl.events;

import me.lor3mipsum.next.client.event.events.interfaces.IEvent;

public class KeyEvent implements IEvent {
    private int key;

    public KeyEvent(int key) {
        this.key = key;
    }

    public int getKey() {
        return key;
    }
}
