package me.lor3mipsum.next.api.util.client;

import me.lor3mipsum.next.Main;
import me.lor3mipsum.next.client.impl.modules.client.CustomFont;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import org.quantumclient.renderer.text.FontRenderer;
import org.quantumclient.renderer.text.GlyphPage;

import java.awt.*;

public class FontUtils {

    private static final MinecraftClient mc = MinecraftClient.getInstance();

    private static FontRenderer verdana;
    private static final CustomFont customFont = Main.moduleManager.getModule(CustomFont.class);

    public static void drawString(MatrixStack matrix, String text, int x, int y, Color color) {
        if (verdana == null) verdana = new FontRenderer(new GlyphPage(new Font("Verdana", Font.PLAIN, 256), 256));
        if (customFont.getEnabled()) {
            verdana.drawString(matrix, text, x, y-3, false, color);
        } else {
            mc.textRenderer.drawWithShadow(new MatrixStack(), text, x, y, color.getRGB());
        }
    }

    public static float getStringWidth(String string) {
        if (verdana == null) verdana = new FontRenderer(new GlyphPage(new Font("Verdana", Font.PLAIN, 256), 256));
        if (customFont.getEnabled()) {
            return verdana.getWidth(string);
        } else {
            return mc.textRenderer.getWidth(string);
        }
    }
}
