package me.lor3mipsum.next.mixin;

import me.lor3mipsum.next.Main;
import me.lor3mipsum.next.api.event.network.ConnectToServerEvent;
import me.lor3mipsum.next.api.event.network.PacketReceiveEvent;
import me.lor3mipsum.next.api.event.network.PacketSendEvent;
import me.lor3mipsum.next.api.event.network.PacketSentEvent;
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
        PacketReceiveEvent event = new PacketReceiveEvent(packet);

        Main.EVENT_BUS.post(event);

        if (event.isCancelled())
            info.cancel();
    }

    @Inject(at = @At("HEAD"), method = "send(Lnet/minecraft/network/Packet;)V", cancellable = true)
    private void onSendPacketHead(Packet<?> packet, CallbackInfo info) {
        PacketSendEvent event = new PacketSendEvent(packet);

        Main.EVENT_BUS.post(event);

        if (event.isCancelled())
            info.cancel();
    }

    @Inject(method = "send(Lnet/minecraft/network/Packet;)V", at = @At("TAIL"))
    private void onSendPacketTail(Packet<?> packet, CallbackInfo info) {
        Main.EVENT_BUS.post(new PacketSentEvent(packet));
    }

    @Inject(method = "connect", at = @At("HEAD"))
    private static void onConnect(InetAddress address, int port, boolean shouldUseNativeTransport, CallbackInfoReturnable<ClientConnection> info) {
        Main.EVENT_BUS.post(new ConnectToServerEvent());
    }
}
