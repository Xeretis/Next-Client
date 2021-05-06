package me.lor3mipsum.next.api.event.network;

import net.minecraft.network.Packet;

public class PacketSendEvent extends PacketEvent{

    public PacketSendEvent(Packet<?> packet) {
        super(packet);
    }

}
