package me.lor3mipsum.next.client.impl.events;

import me.lor3mipsum.next.client.event.events.Cancellable;
import net.minecraft.client.sound.SoundInstance;

public class PlaySoundEvent extends Cancellable {
    public SoundInstance sound;

    public PlaySoundEvent(SoundInstance sound) {
        this.setCancelled(false);
        this.sound = sound;
    }
}
