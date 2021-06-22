package me.lor3mipsum.next.mixin;

import com.mojang.authlib.GameProfile;
import me.lor3mipsum.next.Main;
import me.lor3mipsum.next.api.event.NextEvent;
import me.lor3mipsum.next.api.event.player.ClientMoveEvent;
import me.lor3mipsum.next.api.event.player.SendMessageEvent;
import me.lor3mipsum.next.api.event.player.SendMovementPacketsEvent;
import me.lor3mipsum.next.client.impl.modules.movement.NoSlow;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.MovementType;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ClientPlayerEntity.class, priority = 1001)
public abstract class ClientPlayerEntityMixin extends AbstractClientPlayerEntity {
    @Unique private boolean ignoreChatMessage;

    @Shadow public abstract void sendChatMessage(String string);

    @Shadow protected void autoJump(float dx, float dz) {}

    public ClientPlayerEntityMixin(ClientWorld world, GameProfile profile) {
        super(world, profile);
    }

    @Inject(at = @At("HEAD"), method = "sendChatMessage", cancellable = true)
    private void onSendChatMessage(String msg, CallbackInfo info) {
        if (ignoreChatMessage) return;

        if(msg.startsWith(Main.prefix) && msg.length() > 1) {
            Main.commandManager.executeCommand(msg);
            info.cancel();
            return;
        }

        if (!msg.startsWith(Main.prefix) && !msg.startsWith("/")) {
            SendMessageEvent event = new SendMessageEvent(msg);

            Main.EVENT_BUS.post(event);

            if (!event.isCancelled()) {
                ignoreChatMessage = true;
                sendChatMessage(event.msg);
                ignoreChatMessage = false;
            }

            info.cancel();
            return;
        }
    }

    @Inject(method = "move", at = @At("HEAD"), cancellable = true)
    public void move(MovementType type, Vec3d movement, CallbackInfo info) {
        ClientMoveEvent event = new ClientMoveEvent(type, movement);

        Main.EVENT_BUS.post(event);

        if (event.isCancelled())
            info.cancel();
        else if (!type.equals(event.type) || !movement.equals(event.vec3d)) {
            double double_1 = this.getX();
            double double_2 = this.getZ();
            super.move(event.type, event.vec3d);
            this.autoJump((float) (this.getX() - double_1), (float) (this.getZ() - double_2));
            info.cancel();
        }
    }

    @Redirect(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isUsingItem()Z"))
    private boolean isUsingItem(ClientPlayerEntity player) {
        if (Main.moduleManager.getModule(NoSlow.class).items.getValue() && Main.moduleManager.getModule(NoSlow.class).getEnabled()) return false;
        return player.isUsingItem();
    }

    @Inject(method = "shouldSlowDown", at = @At("HEAD"), cancellable = true)
    private void onShouldSlowDown(CallbackInfoReturnable<Boolean> info) {
        if (Main.moduleManager.getModule(NoSlow.class).sneak.getValue() && Main.moduleManager.getModule(NoSlow.class).getEnabled()) {
            info.setReturnValue(shouldLeaveSwimmingPose());
        }
    }

    @Inject(method = "sendMovementPackets", at = @At("HEAD"))
    private void onSendMovementPacketsHead(CallbackInfo info) {
        Main.EVENT_BUS.post(new SendMovementPacketsEvent(NextEvent.Era.PRE));
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayNetworkHandler;sendPacket(Lnet/minecraft/network/Packet;)V", ordinal = 0))
    private void onTickHasVehicleBeforeSendPackets(CallbackInfo info) {
        Main.EVENT_BUS.post(new SendMovementPacketsEvent(NextEvent.Era.PRE));
    }

    @Inject(method = "sendMovementPackets", at = @At("TAIL"))
    private void onSendMovementPacketsTail(CallbackInfo info) {
        Main.EVENT_BUS.post(new SendMovementPacketsEvent(NextEvent.Era.POST));
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayNetworkHandler;sendPacket(Lnet/minecraft/network/Packet;)V", ordinal = 1, shift = At.Shift.AFTER))
    private void onTickHasVehicleAfterSendPackets(CallbackInfo info) {
        Main.EVENT_BUS.post(new SendMovementPacketsEvent(NextEvent.Era.POST));
    }
}
