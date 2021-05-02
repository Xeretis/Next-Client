package me.lor3mipsum.next.client.impl.events;

import me.lor3mipsum.next.client.event.events.interfaces.IEvent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;

public class TookDamageEvent implements IEvent {
    public LivingEntity entity;
    public DamageSource source;

    public TookDamageEvent(LivingEntity entity, DamageSource source) {
        this.entity = entity;
        this.source = source;
    }
}
