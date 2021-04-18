package me.lor3mipsum.next.client.event.events;

import me.lor3mipsum.next.client.event.events.interfaces.IEvent;

public abstract class Stoppable implements IEvent {
    private boolean stopped;

    protected Stoppable() {
    }

    public void stop() {
        stopped = true;
    }

    public boolean isStopped() {
        return stopped;
    }
}
