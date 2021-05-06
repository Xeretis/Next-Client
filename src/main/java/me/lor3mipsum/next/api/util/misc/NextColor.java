package me.lor3mipsum.next.api.util.misc;

import java.awt.*;

public class NextColor extends Color {
    public NextColor (int rgb) {
        super(rgb);
    }

    public NextColor (int rgba, boolean hasalpha) {
        super(rgba,hasalpha);
    }

    public NextColor (int r, int g, int b) {
        super(r,g,b);
    }

    public NextColor (int r, int g, int b, int a) {
        super(r,g,b,a);
    }

    public NextColor (Color color) {
        super(color.getRed(),color.getGreen(),color.getBlue(),color.getAlpha());
    }

    public NextColor (NextColor color, int a) {
        super(color.getRed(),color.getGreen(),color.getBlue(),a);
    }

    public static NextColor fromHSB (float hue, float saturation, float brightness) {
        return new NextColor(Color.getHSBColor(hue,saturation,brightness));
    }

    public float getHue() {
        return RGBtoHSB(getRed(),getGreen(),getBlue(),null)[0];
    }

    public float getSaturation() {
        return RGBtoHSB(getRed(),getGreen(),getBlue(),null)[1];
    }

    public float getBrightness() {
        return RGBtoHSB(getRed(),getGreen(),getBlue(),null)[2];
    }
}
