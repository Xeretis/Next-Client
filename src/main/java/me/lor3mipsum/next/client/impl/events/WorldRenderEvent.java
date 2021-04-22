package me.lor3mipsum.next.client.impl.events;

import me.lor3mipsum.next.client.event.events.Cancellable;
import me.lor3mipsum.next.client.event.events.interfaces.IEvent;

public class WorldRenderEvent extends Cancellable {
    protected float partialTicks;

    public static class Pre extends WorldRenderEvent {

        public Pre(float partialTicks) {
            this.partialTicks = partialTicks;
        }

    }

    public static class Post extends WorldRenderEvent {

        public Post(float partialTicks) {
            this.partialTicks = partialTicks;
        }

    }

    public float getPartialTicks() {
        return partialTicks;
    }
}
