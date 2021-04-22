package me.lor3mipsum.next.mixin;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.lor3mipsum.next.Next;
import me.lor3mipsum.next.client.command.CommandManager;
import me.lor3mipsum.next.client.utils.ChatUtils;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin {
    @Shadow
    public abstract void sendChatMessage(String string);

    @Inject(at = @At("HEAD"), method = "sendChatMessage", cancellable = true)
    private void onSendChatMessage(String msg, CallbackInfo info) {
        if (msg.startsWith(Next.prefix) && msg.length() > 1) {
            CommandManager.executeCommand(msg);
            info.cancel();
        }
    }
}
