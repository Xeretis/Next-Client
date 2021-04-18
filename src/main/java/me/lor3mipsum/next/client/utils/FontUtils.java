package me.lor3mipsum.next.client.utils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;

import java.awt.*;

public class FontUtils {
    private static final MinecraftClient mc = MinecraftClient.getInstance();

    public static float drawStringWithShadow(String text, int x, int y, Color color) {
        return  mc.textRenderer.drawWithShadow(new MatrixStack(), text, x, y, color.getRGB());
    }

    public static int getStringWidth(String string) {
            return mc.textRenderer.getWidth(string);
    }

    public static int getFontHeight() {
        return mc.textRenderer.fontHeight;
    }
}
