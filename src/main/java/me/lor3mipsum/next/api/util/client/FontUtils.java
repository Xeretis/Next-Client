package me.lor3mipsum.next.api.util.client;

import me.lor3mipsum.next.Main;
import me.lor3mipsum.next.api.util.render.font.GlyphPageFontRenderer;
import me.lor3mipsum.next.client.impl.modules.client.CustomFont;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import org.lwjgl.opengl.GLUtil;
import org.quantumclient.renderer.text.FontRenderer;
import org.quantumclient.renderer.text.GlyphPage;

import java.awt.*;

public class FontUtils {

    private static final MinecraftClient mc = MinecraftClient.getInstance();

    //private static FontRenderer verdana;
    private static final CustomFont customFont = Main.moduleManager.getModule(CustomFont.class);

    private static GlyphPageFontRenderer verdana;

    public static void drawString(MatrixStack matrix, String text, int x, int y, Color color) {
        if (verdana == null) verdana = GlyphPageFontRenderer.create("Verdana", 15, false, false, false);
        if (customFont.getEnabled()) {

            verdana.drawString(text, x, y, (color.getRed() | color.getGreen() << 8 | color.getBlue() << 16 | color.getAlpha() << 24), false);
            //verdana.drawString(matrix, text, x, y-3, false, color);
        } else {
            mc.textRenderer.drawWithShadow(new MatrixStack(), text, x, y, color.getRGB());
        }
    }

    public static float getStringWidth(String string) {
        if (verdana == null) verdana = GlyphPageFontRenderer.create("Verdana", 15, false, false, false);
        if (customFont.getEnabled()) {
            return verdana.getStringWidth(string);
            //return verdana.getWidth(string);
        } else {
            return mc.textRenderer.getWidth(string);
        }
    }
}
