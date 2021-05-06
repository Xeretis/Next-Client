package me.lor3mipsum.next.mixin;

import me.lor3mipsum.next.Main;
import me.lor3mipsum.next.api.event.entity.EntityDestroyEvent;
import me.lor3mipsum.next.api.event.game.GameJoinedEvent;
import me.lor3mipsum.next.api.event.game.GameLeftEvent;
import me.lor3mipsum.next.api.event.network.PlaySoundPacketEvent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.packet.s2c.play.EntitiesDestroyS2CPacket;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {
    @Unique private boolean worldNotNull;

    @Shadow private MinecraftClient client;

    @Shadow private ClientWorld world;

    @Inject(at = @At("HEAD"), method = "onGameJoin")
    private void onGameJoinHead(GameJoinS2CPacket packet, CallbackInfo info) {
        worldNotNull = world != null;
    }

    @Inject(at = @At("TAIL"), method = "onGameJoin")
    private void onGameJoinTail(GameJoinS2CPacket packet, CallbackInfo info) {
        if (worldNotNull) {
            Main.EVENT_BUS.post(new GameLeftEvent());
        }

        Main.EVENT_BUS.post(new GameJoinedEvent());
    }

    @Inject(at = @At("HEAD"), method = "onPlaySound")
    private void onPlaySound(PlaySoundS2CPacket packet, CallbackInfo info) {
        Main.EVENT_BUS.post(new PlaySoundPacketEvent(packet));
    }

    @Inject(method = "onEntitiesDestroy", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/world/ClientWorld;removeEntity(I)V"), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void onEntityDestroy(EntitiesDestroyS2CPacket packet, CallbackInfo info, int i, int j) {
        Main.EVENT_BUS.post(new EntityDestroyEvent(client.world.getEntityById(j)));
    }
}
