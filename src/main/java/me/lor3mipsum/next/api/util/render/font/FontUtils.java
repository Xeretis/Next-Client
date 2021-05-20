package me.lor3mipsum.next.api.util.render.font;

import me.lor3mipsum.next.Main;
import me.lor3mipsum.next.api.util.render.font.GlyphPageFontRenderer;
import me.lor3mipsum.next.client.impl.modules.client.CustomFont;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;

import java.awt.*;

public class FontUtils {

    private static final MinecraftClient mc = MinecraftClient.getInstance();

    private static final CustomFont customFont = Main.moduleManager.getModule(CustomFont.class);

    private static GlyphPageFontRenderer arial;

    public static void drawString(String text, int x, int y, Color color) {
        if (arial == null) arial = GlyphPageFontRenderer.create("Arial", 19, false, false, false);
        if (customFont.getEnabled()) {
            arial.drawString(text, x, y-2, (color.getRed() | color.getGreen() << 8 | color.getBlue() << 16 | color.getAlpha() << 24), customFont.shadow.getValue());
        } else {
            mc.textRenderer.drawWithShadow(new MatrixStack(), text, x, y, color.getRGB());
        }
    }

    public static float getStringWidth(String string) {
        if (arial == null) arial = GlyphPageFontRenderer.create("Arial", 19, false, false, false);
        if (customFont.getEnabled()) {
            return arial.getStringWidth(string);
        } else {
            return mc.textRenderer.getWidth(string);
        }
    }
}
