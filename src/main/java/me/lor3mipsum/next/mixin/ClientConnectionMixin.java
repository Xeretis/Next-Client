package me.lor3mipsum.next.mixin;

import me.lor3mipsum.next.client.event.EventManager;
import me.lor3mipsum.next.client.impl.events.GameJoinEvent;
import me.lor3mipsum.next.client.impl.events.RecivePacketEvent;
import me.lor3mipsum.next.client.impl.events.SendPacketEvent;
import me.lor3mipsum.next.client.impl.events.SentPacketEvent;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.PacketListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.net.InetAddress;

@Mixin(ClientConnection.class)
public class ClientConnectionMixin {
    @Inject(method = "handlePacket", at = @At("HEAD"), cancellable = true)
    private static <T extends PacketListener> void onHandlePacket(Packet<T> packet, PacketListener listener, CallbackInfo info) {
        RecivePacketEvent event = new RecivePacketEvent(packet);

        EventManager.call(event);

        if (event.isCancelled()) info.cancel();
    }

    @Inject(method = "connect", at = @At("HEAD"))
    private static void onConnect(InetAddress address, int port, boolean shouldUseNativeTransport, CallbackInfoReturnable<ClientConnection> info) {
        EventManager.call(new GameJoinEvent());
    }

    @Inject(at = @At("HEAD"), method = "send(Lnet/minecraft/network/Packet;)V", cancellable = true)
    private void onSendPacketHead(Packet<?> packet, CallbackInfo info) {
        SendPacketEvent event = new SendPacketEvent(packet);

        EventManager.call(event);

        if (event.isCancelled()) info.cancel();
    }

    @Inject(method = "send(Lnet/minecraft/network/Packet;)V", at = @At("TAIL"))
    private void onSendPacketTail(Packet<?> packet, CallbackInfo info) {
        EventManager.call(new SentPacketEvent(packet));
    }
}
