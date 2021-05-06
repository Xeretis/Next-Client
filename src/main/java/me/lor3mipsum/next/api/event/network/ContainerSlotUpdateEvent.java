package me.lor3mipsum.next.api.event.network;

import me.lor3mipsum.next.api.event.NextEvent;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;

public class ContainerSlotUpdateEvent extends NextEvent {
    public ScreenHandlerSlotUpdateS2CPacket packet;

    public ContainerSlotUpdateEvent(ScreenHandlerSlotUpdateS2CPacket packet) {
        this.packet = packet;
    }

    public ContainerSlotUpdateEvent(ScreenHandlerSlotUpdateS2CPacket packet, Era era) {
        super(era);
        this.packet = packet;
    }
}
