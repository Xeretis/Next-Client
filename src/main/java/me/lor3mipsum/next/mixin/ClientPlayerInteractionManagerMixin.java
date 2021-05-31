package me.lor3mipsum.next.mixin;

import me.lor3mipsum.next.Main;
import me.lor3mipsum.next.api.event.player.AttackBlockEvent;
import me.lor3mipsum.next.api.event.player.AttackEntityEvent;
import me.lor3mipsum.next.api.event.player.BreakBlockEvent;
import me.lor3mipsum.next.api.event.player.InteractItemEvent;
import me.lor3mipsum.next.api.util.mixininterface.IClientPlayerInteractionManager;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerInteractionManager.class)
public abstract class ClientPlayerInteractionManagerMixin implements IClientPlayerInteractionManager {

    @Shadow protected abstract void syncSelectedSlot();

    @Inject(method = "attackEntity", at = @At("HEAD"), cancellable = true)
    private void onAttackEntity(PlayerEntity player, Entity target, CallbackInfo info) {
        AttackEntityEvent event = new AttackEntityEvent(target);

        Main.EVENT_BUS.post(event);

        if (event.isCancelled()) info.cancel();
    }

    @Inject(method = "attackBlock", at = @At("HEAD"), cancellable = true)
    private void onAttackBlock(BlockPos blockPos, Direction direction, CallbackInfoReturnable<Boolean> info) {
        AttackBlockEvent event = new AttackBlockEvent(blockPos, direction);

        Main.EVENT_BUS.post(event);

        if (event.isCancelled())
            info.cancel();
    }

    @Inject(method = "breakBlock", at = @At("HEAD"))
    private void onBreakBlock(BlockPos blockPos, CallbackInfoReturnable<Boolean> info) {
        Main.EVENT_BUS.post(new BreakBlockEvent(blockPos));
    }

    @Inject(method = "interactItem", at = @At("HEAD"), cancellable = true)
    private void onInteractItem(PlayerEntity player, World world, Hand hand, CallbackInfoReturnable<ActionResult> info) {
        InteractItemEvent event = new InteractItemEvent(hand);

        Main.EVENT_BUS.post(event);

        if (event.toReturn != null) info.setReturnValue(event.toReturn);
    }

    @Override
    public void syncSlot() {
        syncSelectedSlot();
    }
}
