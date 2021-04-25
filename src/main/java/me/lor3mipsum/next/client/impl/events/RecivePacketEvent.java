package me.lor3mipsum.next.client.impl.events;

import me.lor3mipsum.next.client.event.events.Cancellable;
import net.minecraft.network.Packet;

public class RecivePacketEvent extends Cancellable {
    private Packet<?> packet;

    public RecivePacketEvent(Packet<?> packet) {
        this.packet = packet;
    }

    public Packet<?> getPacket() {
        return packet;
    }

    public void setPacket(Packet<?> packet) {
        this.packet = packet;
    }
}
