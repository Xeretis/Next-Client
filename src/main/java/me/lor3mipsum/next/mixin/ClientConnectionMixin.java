package me.lor3mipsum.next.mixin;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import me.lor3mipsum.next.client.event.EventManager;
import me.lor3mipsum.next.client.impl.events.ReadPacketEvent;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientConnection.class)
public class ClientConnectionMixin {
    @Shadow
    private Channel channel;

    @Inject(method = "channelRead0", at = @At("HEAD"), cancellable = true)
    public void IchannelRead0(ChannelHandlerContext channelHandlerContext_1, Packet<?> packet_1, CallbackInfo callback) {
        if (this.channel.isOpen() && packet_1 != null) {
            try {
                ReadPacketEvent event = new ReadPacketEvent(packet_1);
                EventManager.call(event);
                if (event.isCancelled())
                    callback.cancel();
            } catch (Exception exception) {
            }
        }
    }
}
