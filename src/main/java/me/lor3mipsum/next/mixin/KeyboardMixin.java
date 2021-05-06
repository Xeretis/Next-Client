package me.lor3mipsum.next.mixin;

import me.lor3mipsum.next.Main;
import me.lor3mipsum.next.api.event.client.KeyEvent;
import me.lor3mipsum.next.api.util.client.KeyboardUtils;
import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Keyboard.class)
public class KeyboardMixin {
    @Inject(method = "onKey", at = @At("HEAD"), cancellable = true)
    public void onKey(long window, int key, int scancode, int i, int j, CallbackInfo info) {
        if(key != GLFW.GLFW_KEY_UNKNOWN) {
            KeyEvent event = new KeyEvent(key, KeyboardUtils.KeyAction.get(i));

            Main.EVENT_BUS.post(event);

            if(event.isCancelled())
                info.cancel();
        }
    }
}
