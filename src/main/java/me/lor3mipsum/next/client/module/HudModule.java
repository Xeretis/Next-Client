package me.lor3mipsum.next.client.module;

import com.lukflug.panelstudio.FixedComponent;
import com.lukflug.panelstudio.theme.Theme;
import me.lor3mipsum.next.Next;
import org.lwjgl.glfw.GLFW;

import java.awt.*;

public abstract class HudModule extends Module {
    protected FixedComponent component;
    public Point position;

    public HudModule (String name, String description, Point defaultPos, Category category) {
        super(name, description, category);
        position = defaultPos;
    }

    public abstract void populate (Theme theme);

    public FixedComponent getComponent() {
        return component;
    }

    public void resetPosition() {
        component.setPosition(Next.INSTANCE.clickGui.guiInterface,position);
    }

    @Override
    public int getKeybind() {
        return GLFW.GLFW_KEY_UNKNOWN;
    }

    @Override
    public void setKeybind(int keybind) {

    }
}
