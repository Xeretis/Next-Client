package me.lor3mipsum.next.client.impl.modules.render;

import me.lor3mipsum.next.api.event.NextEvent;
import me.lor3mipsum.next.api.event.client.TickEvent;
import me.lor3mipsum.next.client.core.module.Category;
import me.lor3mipsum.next.client.core.module.Module;
import me.lor3mipsum.next.client.core.module.annotation.Mod;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;

@Mod(name = "Fullbright", description = "Makes the game pretty bright", category = Category.RENDER)
public class Fullbright extends Module {

    private static double prevGamma;
    private static boolean hasSet = false;

    @Override
    public void onEnable() {
        if (mc.options != null)
            prevGamma = mc.options.gamma;
        hasSet = false;
    }

    @EventHandler
    private Listener<TickEvent> onTick = new Listener<>(event -> {
        if (mc.options != null && !hasSet) {
            mc.options.gamma = 1000;
            hasSet = true;
        }
    }, event -> event.era == NextEvent.Era.POST);

    @Override
    public void onDisable() {
        if (mc.options != null)
            mc.options.gamma = prevGamma;
    }
}
