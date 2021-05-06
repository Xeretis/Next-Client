package me.lor3mipsum.next.api.event.player;

import me.lor3mipsum.next.api.event.NextEvent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.fluid.Fluid;

public class CanWalkOnFluidEvent extends NextEvent {
    public LivingEntity entity;
    public Fluid fluid;
    public boolean walkOnFluid;

    public CanWalkOnFluidEvent(LivingEntity entity, Fluid fluid) {
        this.entity = entity;
        this.fluid = fluid;
        this.walkOnFluid = false;
    }

    public CanWalkOnFluidEvent(LivingEntity entity, Fluid fluid, Era era) {
        super(era);
        this.entity = entity;
        this.fluid = fluid;
        this.walkOnFluid = false;
    }
}
