package me.lor3mipsum.next.api.event.world;

import me.lor3mipsum.next.api.event.NextEvent;
import net.minecraft.client.sound.SoundInstance;

public class PlaySoundEvent extends NextEvent {
    public SoundInstance sound;

    public PlaySoundEvent(SoundInstance sound) {
        this.sound = sound;
    }

    public PlaySoundEvent(SoundInstance sound, Era era) {
        super(era);
        this.sound = sound;
    }
}
