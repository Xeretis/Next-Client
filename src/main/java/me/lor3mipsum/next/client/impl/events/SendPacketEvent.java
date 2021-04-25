package me.lor3mipsum.next.client.impl.events;

import me.lor3mipsum.next.client.event.events.Cancellable;
import net.minecraft.network.Packet;

public class SendPacketEvent extends Cancellable {
    private Packet<?> packet;

    public SendPacketEvent(Packet<?> packet) {
        this.packet = packet;
    }

    public Packet<?> getPacket() {
        return packet;
    }

    public void setPacket(Packet<?> packet) {
        this.packet = packet;
    }
}
