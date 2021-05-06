package me.lor3mipsum.next.api.event.entity;

import me.lor3mipsum.next.api.event.NextEvent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;

public class EntityDamageEvent extends NextEvent {
    public LivingEntity entity;
    public DamageSource source;

    public EntityDamageEvent(LivingEntity entity, DamageSource source) {
        this.entity = entity;
        this.source = source;
    }

    public EntityDamageEvent(LivingEntity entity, DamageSource source, Era era) {
        super(era);
        this.entity = entity;
        this.source = source;
    }
}
