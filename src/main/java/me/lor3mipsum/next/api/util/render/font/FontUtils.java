package me.lor3mipsum.next.api.util.render.font;

import me.lor3mipsum.next.Main;
import me.lor3mipsum.next.client.impl.modules.client.CustomFont;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;

import java.awt.*;

public class FontUtils {

    private static final MinecraftClient mc = MinecraftClient.getInstance();

    private static final CustomFont customFont = Main.moduleManager.getModule(CustomFont.class);

    private static GlyphPageFontRenderer raleway;

    public static void drawString(String text, int x, int y, Color color) {
        if (raleway == null) raleway = GlyphPageFontRenderer.create("Raleway", 19, false, false, false);
        if (customFont.getEnabled()) {
            raleway.drawString(text, x-2, y-3, (color.getRed() | color.getGreen() << 8 | color.getBlue() << 16 | color.getAlpha() << 24), customFont.shadow.getValue());
        } else {
            mc.textRenderer.drawWithShadow(new MatrixStack(), text, x, y, color.getRGB());
        }
    }

    public static float getStringWidth(String string) {
        if (raleway == null) raleway = GlyphPageFontRenderer.create("Raleway", 19, false, false, false);
        if (customFont.getEnabled()) {
            return raleway.getStringWidth(string) + string.length() * 0.42f;
        } else {
            return mc.textRenderer.getWidth(string);
        }
    }
}
