package me.lor3mipsum.next.api.event.world;

import me.lor3mipsum.next.api.event.NextEvent;
import net.minecraft.particle.ParticleEffect;

public class ParticleEmitterEvent extends NextEvent {
    public ParticleEffect particle;

    public ParticleEmitterEvent(ParticleEffect particle) {
        this.particle = particle;
    }
}
