package me.lor3mipsum.next.mixin;

import me.lor3mipsum.next.Next;
import me.lor3mipsum.next.client.event.EventManager;
import me.lor3mipsum.next.client.impl.events.DisconnectEvent;
import me.lor3mipsum.next.client.impl.events.TickEvent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.Window;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.InputStream;

@Mixin(value = MinecraftClient.class, priority = 1001)
public abstract class MinecraftClientMixin {

    @Shadow
    public ClientWorld world;

    @Shadow public abstract Window getWindow();

    @Inject(method = "<init>", at = @At("TAIL"))
    public void createClient(RunArgs args, CallbackInfo ci) {
        InputStream inputStream16 = Next.class.getClassLoader().getResourceAsStream("assets/next/textures/NextLogo16.png");
        InputStream inputStream32 = Next.class.getClassLoader().getResourceAsStream("assets/next/textures/NextLogo32.png");
        MinecraftClient.getInstance().getWindow().setIcon(inputStream16, inputStream32);
        MinecraftClient.getInstance().getWindow().setTitle("Next Client");
    }

    @Inject(method = "disconnect(Lnet/minecraft/client/gui/screen/Screen;)V", at = @At("HEAD"))
    private void onDisconnect(Screen screen, CallbackInfo info) {
        if (world != null) {
            EventManager.call(new DisconnectEvent());
        }
    }

    @Inject(at = @At("HEAD"), method = "tick")
    private void onPreTick(CallbackInfo info) {
        EventManager.call(new TickEvent.Pre());
    }

    @Inject(at = @At("TAIL"), method = "tick")
    private void onTick(CallbackInfo info) {
        EventManager.call(new TickEvent.Post());
    }
}
