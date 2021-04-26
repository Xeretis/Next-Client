package me.lor3mipsum.next.client.impl.modules.client;

import me.lor3mipsum.next.Next;
import me.lor3mipsum.next.client.event.EventTarget;
import me.lor3mipsum.next.client.impl.commands.Rpc;
import me.lor3mipsum.next.client.impl.events.TickEvent;
import me.lor3mipsum.next.client.impl.settings.KeybindSetting;
import me.lor3mipsum.next.client.module.Category;
import me.lor3mipsum.next.client.module.Module;
import net.arikia.dev.drpc.DiscordEventHandlers;
import net.arikia.dev.drpc.DiscordRPC;
import net.arikia.dev.drpc.DiscordRichPresence;
import org.lwjgl.glfw.GLFW;

public class DiscordPresence extends Module {
    public KeybindSetting keybind = new KeybindSetting(GLFW.GLFW_KEY_UNKNOWN);

    private int tick = 0;

    public DiscordPresence() {
        super("DiscordRPC", "Just displays a discord rpc", Category.CLIENT);
    }

    @Override
    public void onEnable() {
        DiscordRPC.discordInitialize("836121301427945514", new DiscordEventHandlers(), true);
    }

    @Override
    public void onDisable() {
        DiscordRPC.discordShutdown();
    }

    @EventTarget
    private void onTick(TickEvent.Post event) {
        DiscordRPC.discordUpdatePresence(new DiscordRichPresence.Builder(Rpc.stateText).setBigImage("nextlogo", Next.CLIENT_NAME + " " + Next.CLIENT_VERSION).setStartTimestamps(System.currentTimeMillis() - tick * 50).setDetails(Rpc.detailsText).build());
        tick++;
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
