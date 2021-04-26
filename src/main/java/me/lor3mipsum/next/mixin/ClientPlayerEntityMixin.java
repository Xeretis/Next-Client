package me.lor3mipsum.next.mixin;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.lor3mipsum.next.Next;
import me.lor3mipsum.next.client.command.CommandManager;
import me.lor3mipsum.next.client.event.EventManager;
import me.lor3mipsum.next.client.impl.events.ClientMoveEvent;
import me.lor3mipsum.next.client.impl.modules.movement.NoSlow;
import me.lor3mipsum.next.client.utils.ChatUtils;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.MovementType;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin extends AbstractClientPlayerEntity {
    @Shadow
    public abstract void sendChatMessage(String string);

    @Shadow protected void autoJump(float dx, float dz) {}

    public ClientPlayerEntityMixin(ClientWorld world, GameProfile profile) {
        super(world, profile);
    }

    @Inject(at = @At("HEAD"), method = "sendChatMessage", cancellable = true)
    private void onSendChatMessage(String msg, CallbackInfo info) {
        if (msg.startsWith(Next.prefix) && msg.length() > 1) {
            CommandManager.executeCommand(msg);
            info.cancel();
        }
    }

    @Redirect(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isUsingItem()Z"))
    private boolean proxy_tickMovement_isUsingItem(ClientPlayerEntity player) {
        if (Next.INSTANCE.moduleManager.getModule(NoSlow.class).items.isOn() && Next.INSTANCE.moduleManager.getModule(NoSlow.class).isOn()) return false;
        return player.isUsingItem();
    }

    @Inject(method = "move", at = @At("HEAD"), cancellable = true)
    public void move(MovementType type, Vec3d movement, CallbackInfo info) {
        ClientMoveEvent event = new ClientMoveEvent(type, movement);
        EventManager.call(event);
        if (event.isCancelled()) {
            info.cancel();
        } else if (!type.equals(event.type) || !movement.equals(event.vec3d)) {
            double double_1 = this.getX();
            double double_2 = this.getZ();
            super.move(event.type, event.vec3d);
            this.autoJump((float) (this.getX() - double_1), (float) (this.getZ() - double_2));
            info.cancel();
        }
    }

    @Inject(method = "shouldSlowDown", at = @At("HEAD"), cancellable = true)
    private void onShouldSlowDown(CallbackInfoReturnable<Boolean> info) {
        if (Next.INSTANCE.moduleManager.getModule(NoSlow.class).sneak.isOn() && Next.INSTANCE.moduleManager.getModule(NoSlow.class).isOn()) {
            info.setReturnValue(shouldLeaveSwimmingPose());
        }
    }
}
