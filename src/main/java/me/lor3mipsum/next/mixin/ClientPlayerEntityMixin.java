package me.lor3mipsum.next.mixin;

import me.lor3mipsum.next.Main;
import me.lor3mipsum.next.api.event.NextEvent;
import me.lor3mipsum.next.api.event.player.SendMessageEvent;
import me.lor3mipsum.next.api.event.player.SendMovementPacketsEvent;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin {
    @Unique private boolean ignoreChatMessage;

    @Shadow public abstract void sendChatMessage(String string);

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
