package me.lor3mipsum.next.mixin;

import me.lor3mipsum.next.Main;
import me.lor3mipsum.next.api.event.NextEvent;
import me.lor3mipsum.next.api.event.entity.EntityDamageEvent;
import me.lor3mipsum.next.api.event.player.CanWalkOnFluidEvent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.fluid.Fluid;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "damage", at = @At("HEAD"))
    private void onDamageHead(DamageSource source, float amount, CallbackInfoReturnable<Boolean> info) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc != null && mc.world != null && mc.player != null)
            Main.EVENT_BUS.post(new EntityDamageEvent((LivingEntity) (Object) this, source, NextEvent.Era.PRE));
    }

    @Inject(method = "damage", at = @At("TAIL"))
    private void onDamageTail(DamageSource source, float amount, CallbackInfoReturnable<Boolean> info) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc != null && mc.world != null && mc.player != null)
            Main.EVENT_BUS.post(new EntityDamageEvent((LivingEntity) (Object) this, source, NextEvent.Era.POST));
    }

    @Inject(method = "canWalkOnFluid", at = @At("HEAD"), cancellable = true)
    private void onCanWalkOnFluid(Fluid fluid, CallbackInfoReturnable<Boolean> info) {
        CanWalkOnFluidEvent event = new CanWalkOnFluidEvent((LivingEntity) (Object) this, fluid);

        Main.EVENT_BUS.post(event);

        if (event.walkOnFluid) info.setReturnValue(true);
    }
}
