package me.lor3mipsum.next.api.event.network;

import me.lor3mipsum.next.api.event.NextEvent;
import net.minecraft.network.Packet;

public class PacketEvent extends NextEvent {
    public Packet<?> packet;

    public PacketEvent(Packet<?> packet) {
        this.packet = packet;
    }

}
