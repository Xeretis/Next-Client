package me.lor3mipsum.next.client.impl.modules.render;

import me.lor3mipsum.next.Next;
import me.lor3mipsum.next.client.event.EventManager;
import me.lor3mipsum.next.client.event.EventTarget;
import me.lor3mipsum.next.client.impl.events.TickEvent;
import me.lor3mipsum.next.client.impl.settings.KeybindSetting;
import me.lor3mipsum.next.client.module.Category;
import me.lor3mipsum.next.client.module.Module;
import net.minecraft.entity.effect.StatusEffect;

public class Fullbright extends Module {

    public KeybindSetting keybind = new KeybindSetting(Next.GUI_KEY);

    private int timesEnabled;
    private int lastTimesEnabled;

    private static double prevGamma;

    public Fullbright() {
        super("Fullbright", "It's just... Fullbright.", Category.RENDER);
    }

    @Override
    public void onEnable() {
        timesEnabled++;
    }

    @Override
    public void onDisable() {
        timesEnabled--;;
    }

    @EventTarget
    public void onTick(TickEvent.Post event) {
        if (timesEnabled > 0 && lastTimesEnabled == 0) {
            prevGamma = mc.options.gamma;
        }
        else if (timesEnabled == 0 && lastTimesEnabled > 0) {
            mc.options.gamma = prevGamma;
        }

        if (timesEnabled > 0) {
            mc.options.gamma = 16;
        }

        lastTimesEnabled = timesEnabled;
    }

    @Override
    public int getKeybind() {
        return keybind.getKey();
    }

    @Override
    public void setKeybind(int keybind) {
        this.keybind.setKey(keybind);
    }
}
