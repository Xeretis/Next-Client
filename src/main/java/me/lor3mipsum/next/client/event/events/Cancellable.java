package me.lor3mipsum.next.client.event.events;

import me.lor3mipsum.next.client.event.events.interfaces.ICancellable;
import me.lor3mipsum.next.client.event.events.interfaces.IEvent;

public abstract class Cancellable implements IEvent, ICancellable {
    private boolean cancelled;

    protected Cancellable() {
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean state) {
        cancelled = state;
    }
}
