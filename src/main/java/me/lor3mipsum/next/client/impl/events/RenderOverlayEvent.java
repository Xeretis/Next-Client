package me.lor3mipsum.next.client.impl.events;

import me.lor3mipsum.next.client.event.events.Cancellable;
import me.lor3mipsum.next.client.event.events.interfaces.IEvent;

public class RenderOverlayEvent extends Cancellable {
    private float opacity;

    public RenderOverlayEvent(float opacity) {
        setOpacity(opacity);
    }

    public float getOpacity() {
        return opacity;
    }

    public void setOpacity(float opacity) {
        this.opacity = opacity;
    }
}
