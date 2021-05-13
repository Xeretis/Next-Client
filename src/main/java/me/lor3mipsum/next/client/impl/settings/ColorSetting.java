package me.lor3mipsum.next.client.impl.settings;

import me.lor3mipsum.next.api.util.misc.NextColor;
import me.lor3mipsum.next.client.core.setting.Setting;

import java.util.function.Predicate;

public class ColorSetting extends Setting<NextColor> {
    private boolean rainbow;
    private final boolean rainbowEnabled, alphaEnabled;

    public ColorSetting(String name, boolean rainbow, NextColor value) {
        super(name, value);
        this.rainbow = rainbow;
        this.rainbowEnabled = true;
        this.alphaEnabled = true;
    }

    public ColorSetting(String name, boolean rainbow, NextColor value, boolean rainbowEnabled, boolean alphaEnabled) {
        super(name, value);
        this.rainbow = rainbow;
        this.rainbowEnabled = rainbowEnabled;
        this.alphaEnabled = alphaEnabled;
    }

    public ColorSetting(String name, boolean rainbow, NextColor value, boolean visible) {
        super(name, value, visible);
        this.rainbow = rainbow;
        this.rainbowEnabled = true;
        this.alphaEnabled = false;
    }

    public ColorSetting(String name, boolean rainbow, NextColor value, boolean visible, boolean rainbowEnabled, boolean alphaEnabled) {
        super(name, value, visible);
        this.rainbow = rainbow;
        this.rainbowEnabled = rainbowEnabled;
        this.alphaEnabled = alphaEnabled;
    }

    public ColorSetting(String name, boolean rainbow, NextColor value, boolean visible, Predicate<NextColor> validator) {
        super(name, value, visible, validator);
        this.rainbow = rainbow;
        this.rainbowEnabled = true;
        this.alphaEnabled = false;
    }

    public ColorSetting(String name, boolean rainbow, NextColor value, boolean visible,  boolean rainbowEnabled, boolean alphaEnabled, Predicate<NextColor> validator) {
        super(name, value, visible, validator);
        this.rainbow = rainbow;
        this.rainbowEnabled = rainbowEnabled;
        this.alphaEnabled = alphaEnabled;
    }

    @Override
    public NextColor getValue() {
        if (rainbow) return new NextColor(NextColor.fromHSB((System.currentTimeMillis() % (360 * 32)) / (360f * 32), 1, 1),getColor().getAlpha());
        else return super.getValue();
    }

    public NextColor getColor() {
        return super.getValue();
    }

    public boolean getRainbow() {
        return rainbow;
    }

    public void setRainbow (boolean rainbow) {
        this.rainbow=rainbow;
    }

    public boolean getRainbowEnabled() {
        return rainbowEnabled;
    }

    public boolean getAlphaEnabled() {
        return alphaEnabled;
    }
}
