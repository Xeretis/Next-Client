package me.lor3mipsum.next.client.event.events.interfaces;

public interface ICancellable {
    void setCancelled(boolean state);

    boolean isCancelled();
}
