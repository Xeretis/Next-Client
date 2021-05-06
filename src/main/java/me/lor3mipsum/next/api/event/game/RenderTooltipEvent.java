package me.lor3mipsum.next.api.event.game;

import me.lor3mipsum.next.api.event.NextEvent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;

import java.util.List;

public class RenderTooltipEvent extends NextEvent {
    public Screen screen;
    public MatrixStack matrix;
    public List<? extends OrderedText> text;
    public int x;
    public int y;
    public int mouseX;
    public int mouseY;

    public RenderTooltipEvent(MatrixStack matrix, List<? extends OrderedText> text, int x, int y, int mouseX, int mouseY) {
        this.matrix = matrix;
        screen = MinecraftClient.getInstance().currentScreen;
        this.text = text;
        this.x = x;
        this.y = y;
        this.mouseX = mouseX;
        this.mouseY = mouseY;
    }

    public RenderTooltipEvent(MatrixStack matrix, List<? extends OrderedText> text, int x, int y, int mouseX, int mouseY, Era era) {
        super(era);
        this.matrix = matrix;
        screen = MinecraftClient.getInstance().currentScreen;
        this.text = text;
        this.x = x;
        this.y = y;
        this.mouseX = mouseX;
        this.mouseY = mouseY;
    }
}
