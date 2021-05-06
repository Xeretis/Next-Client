package me.lor3mipsum.next.api.event.world;

import me.lor3mipsum.next.api.event.NextEvent;
import net.minecraft.client.particle.Particle;

public class ParticleNormalEvent extends NextEvent {
    public Particle particle;

    public ParticleNormalEvent(Particle particle) {
        this.particle = particle;
    }
}
