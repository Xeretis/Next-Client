package me.lor3mipsum.next.api.event.network;

import me.lor3mipsum.next.api.event.NextEvent;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;

public class PlaySoundPacketEvent extends NextEvent {
    public PlaySoundS2CPacket packet;

    public PlaySoundPacketEvent(PlaySoundS2CPacket packet) {
        this.packet = packet;
    }

    public PlaySoundPacketEvent(PlaySoundS2CPacket packet, Era era) {
        super(era);
        this.packet = packet;
    }
}
