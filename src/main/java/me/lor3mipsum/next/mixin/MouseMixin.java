package me.lor3mipsum.next.mixin;

import me.lor3mipsum.next.Main;
import me.lor3mipsum.next.api.event.client.MouseEvent;
import me.lor3mipsum.next.api.event.client.MouseScrollEvent;
import me.lor3mipsum.next.api.util.client.KeyboardUtils;
import net.minecraft.client.Mouse;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mouse.class)
public class MouseMixin {
    @Inject(method = "onMouseButton", at = @At("HEAD"), cancellable = true)
    private void onMouseButton(long window, int button, int action, int mods, CallbackInfo info) {
        MouseEvent event = new MouseEvent(button, KeyboardUtils.KeyAction.get(action));

        Main.EVENT_BUS.post(event);

        if (event.isCancelled())
            info.cancel();
    }

    @Inject(method = "onMouseScroll", at = @At("HEAD"), cancellable = true)
    private void onMouseScroll(long window, double horizontal, double vertical, CallbackInfo info) {
        MouseScrollEvent event = new MouseScrollEvent(vertical);

        Main.EVENT_BUS.post(event);

        if (event.isCancelled())
            info.cancel();
    }
}
