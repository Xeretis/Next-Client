package me.lor3mipsum.next.client.impl.events;

import me.lor3mipsum.next.client.event.events.Cancellable;
import me.lor3mipsum.next.client.event.events.interfaces.IEvent;
import net.minecraft.network.Packet;

public class ReadPacketEvent extends Cancellable {
    private Packet<?> packet;

    public ReadPacketEvent(Packet<?> packet) {
        this.packet = packet;
    }

    public Packet<?> getPacket() {
        return packet;
    }

    public void setPacket(Packet<?> packet) {
        this.packet = packet;
    }
}
