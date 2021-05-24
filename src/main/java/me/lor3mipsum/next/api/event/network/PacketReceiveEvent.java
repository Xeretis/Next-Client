package me.lor3mipsum.next.api.event.network;

import net.minecraft.network.Packet;

public class PacketReceiveEvent extends PacketEvent{

    public PacketReceiveEvent(Packet<?> packet) {
        super(packet);
    }

}
