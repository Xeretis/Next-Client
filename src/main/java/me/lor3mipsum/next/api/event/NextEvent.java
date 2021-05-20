package me.lor3mipsum.next.api.event;

import me.zero.alpine.event.type.Cancellable;
import net.minecraft.client.MinecraftClient;

public class NextEvent extends Cancellable {
    public Era era = Era.PRE;
    public float tickDelta = MinecraftClient.getInstance().getTickDelta();

    public NextEvent() { }

    public NextEvent(Era era) {
        this.era = era;
    }

    public enum Era {
        PRE,
        POST
    }
}
