package me.lor3mipsum.next.client.impl.modules.render;

import me.lor3mipsum.next.api.event.NextEvent;
import me.lor3mipsum.next.api.event.client.TickEvent;
import me.lor3mipsum.next.client.core.module.Category;
import me.lor3mipsum.next.client.core.module.Module;
import me.lor3mipsum.next.client.core.module.annotation.Mod;
import me.lor3mipsum.next.client.core.setting.SettingSeparator;
import me.lor3mipsum.next.client.impl.settings.IntegerSetting;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;

@Mod(name = "CustomFOV", description = "Allows you to change your FOV to be over/under the limit", category = Category.RENDER)
public class CustomFOV extends Module {

    public SettingSeparator generalSep = new SettingSeparator("General");

    public IntegerSetting fov = new IntegerSetting("FOV", 100, 1, 170);

    private static double prevFov = 110;

    @Override
    public void onEnable() {
        if (mc.options != null)
            prevFov = mc.options.fov;
    }

    @EventHandler
    private Listener<TickEvent> onTick = new Listener<>(event -> {
        if (mc.player != null) {
            mc.options.fov = fov.getValue();
        }
    }, event -> event.era == NextEvent.Era.POST);

    @Override
    public void onDisable() {
        mc.options.fov = prevFov;
    }
}
