package me.lor3mipsum.next.api.event.network;

import net.minecraft.network.Packet;

public class PacketReciveEvent extends PacketEvent{

    public PacketReciveEvent(Packet<?> packet) {
        super(packet);
    }

}
