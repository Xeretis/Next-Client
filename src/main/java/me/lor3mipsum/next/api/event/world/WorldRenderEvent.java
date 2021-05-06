package me.lor3mipsum.next.api.event.world;

import me.lor3mipsum.next.api.event.NextEvent;

public class WorldRenderEvent extends NextEvent {
    public float partialTicks;

    public WorldRenderEvent(float partialTicks) {
        this.partialTicks = partialTicks;
    }

    public WorldRenderEvent(float partialTicks, Era era) {
        super(era);
        this.partialTicks = partialTicks;
    }
}
