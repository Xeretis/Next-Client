package me.lor3mipsum.next.client.impl.modules.client;

import me.lor3mipsum.next.client.impl.settings.ModeSetting;
import me.lor3mipsum.next.client.module.Category;
import me.lor3mipsum.next.client.module.Module;

public class ColorMode extends Module {

    public static ModeSetting colorModel = new ModeSetting("ColorModel", "HSB", "RGB", "HSB");

    public ColorMode() {
        super("ColorMode", "Set the color mode used by guis.", Category.CLIENT);
    }

    @Override
    public void onEnable() {
        this.setState(false);
    }
}
