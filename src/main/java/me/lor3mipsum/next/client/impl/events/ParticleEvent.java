package me.lor3mipsum.next.client.impl.events;

import me.lor3mipsum.next.client.event.events.Cancellable;
import net.minecraft.client.particle.Particle;
import net.minecraft.particle.ParticleEffect;

public class ParticleEvent extends Cancellable {
    public static class Normal extends ParticleEvent {

        public Particle particle;

        public Normal(Particle particle) {
            this.particle = particle;
        }
    }

    public static class Emitter extends ParticleEvent {

        public ParticleEffect effect;

        public Emitter(ParticleEffect effect) {
            this.effect = effect;

        }
    }
}