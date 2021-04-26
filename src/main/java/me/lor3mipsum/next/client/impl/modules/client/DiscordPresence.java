package me.lor3mipsum.next.client.impl.modules.client;

import club.minnced.discord.rpc.DiscordEventHandlers;
import club.minnced.discord.rpc.DiscordRPC;
import club.minnced.discord.rpc.DiscordRichPresence;
import me.lor3mipsum.next.Next;
import me.lor3mipsum.next.client.event.EventTarget;
import me.lor3mipsum.next.client.impl.commands.Rpc;
import me.lor3mipsum.next.client.impl.events.TickEvent;
import me.lor3mipsum.next.client.impl.settings.KeybindSetting;
import me.lor3mipsum.next.client.module.Category;
import me.lor3mipsum.next.client.module.Module;
import org.lwjgl.glfw.GLFW;

public class DiscordPresence extends Module {
    public KeybindSetting keybind = new KeybindSetting(GLFW.GLFW_KEY_UNKNOWN);
    private static final DiscordRPC instance = DiscordRPC.INSTANCE;

    private static final DiscordRichPresence rpc = new DiscordRichPresence();

    public DiscordPresence() {
        super("DiscordRPC", "Just displays a discord rpc", Category.CLIENT);
    }

    @Override
    public void onEnable() {
        DiscordEventHandlers handlers = new DiscordEventHandlers();
        instance.Discord_Initialize("836121301427945514", handlers, true, null);

        rpc.startTimestamp = System.currentTimeMillis() / 1000L;
        rpc.largeImageKey = "nextlogo";
        rpc.largeImageText = Next.CLIENT_NAME + " " + Next.CLIENT_VERSION;

    }

    @Override
    public void onDisable() {
        instance.Discord_ClearPresence();
        instance.Discord_Shutdown();
    }

    @EventTarget
    private void onTick(TickEvent.Post event) {
        rpc.state = Rpc.stateText;
        rpc.details = Rpc.detailsText;

        instance.Discord_UpdatePresence(rpc);
    }

    @Override
    public void setKeybind(int keybind) {
        this.keybind.setKey(keybind);
    }

    @Override
    public int getKeybind() {
        return keybind.getKey();
    }
}
