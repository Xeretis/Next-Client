package me.lor3mipsum.next.mixin;

import me.lor3mipsum.next.Next;
import me.lor3mipsum.next.client.event.EventManager;
import me.lor3mipsum.next.client.impl.events.DisconnectEvent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = MinecraftClient.class, priority = 1001)
public abstract class MinecraftClientMixin {

    @Shadow
    public ClientWorld world;

    @Inject(method = "disconnect(Lnet/minecraft/client/gui/screen/Screen;)V", at = @At("HEAD"))
    private void onDisconnect(Screen screen, CallbackInfo info) {
        if (world != null) {
            EventManager.call(new DisconnectEvent());
        }
    }
}
