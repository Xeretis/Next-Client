package me.lor3mipsum.next.api.event.network;

import net.minecraft.network.Packet;

public class PacketSentEvent extends PacketEvent{

    public PacketSentEvent(Packet<?> packet) {
        super(packet);
    }

}
