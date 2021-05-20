package me.lor3mipsum.next.client.impl.modules.client;

import me.lor3mipsum.next.Main;
import me.lor3mipsum.next.api.event.NextEvent;
import me.lor3mipsum.next.api.event.client.TickEvent;
import me.lor3mipsum.next.client.core.module.Category;
import me.lor3mipsum.next.client.core.module.Module;
import me.lor3mipsum.next.client.core.module.annotation.Mod;
import me.lor3mipsum.next.client.core.setting.SettingSeparator;
import me.lor3mipsum.next.client.impl.settings.StringSetting;
import me.zero.alpine.event.EventPriority;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.arikia.dev.drpc.DiscordEventHandlers;
import net.arikia.dev.drpc.DiscordRPC;
import net.arikia.dev.drpc.DiscordRichPresence;

@Mod(name = "DiscordRPC", description = "Shows that you are playing minecraft with Next Client in discord", category = Category.CLIENT, enabled = true)
public class DiscordRPCModule extends Module {

    public SettingSeparator generalSep = new SettingSeparator("General");

    public StringSetting details = new StringSetting("Details", "normal text");
    public StringSetting state = new StringSetting("State", "also normal text");

    private int tick = 0;

    @Override
    public void onEnable() {
        DiscordRPC.discordInitialize("836121301427945514", new DiscordEventHandlers(), true);
    }

    @Override
    public void onDisable() {
        DiscordRPC.discordShutdown();
    }

    @EventHandler
    private Listener<TickEvent> onTick = new Listener<>(event -> {
        if (event.era == NextEvent.Era.POST) {
            DiscordRPC.discordUpdatePresence(new DiscordRichPresence.Builder(state.getValue()).setBigImage("nextlogo", Main.CLIENT_NAME + " " + Main.CLIENT_VERSION).setStartTimestamps(System.currentTimeMillis() - tick * 50L).setDetails(details.getValue()).build());
            tick++;
        }
    }, EventPriority.LOW);
}
