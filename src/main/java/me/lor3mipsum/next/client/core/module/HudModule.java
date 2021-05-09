package me.lor3mipsum.next.client.core.module;

import com.lukflug.panelstudio.component.IFixedComponent;
import com.lukflug.panelstudio.theme.ITheme;
import me.lor3mipsum.next.client.core.module.annotation.HudMod;

import java.awt.*;

public abstract class HudModule extends Module {
    protected IFixedComponent component;
    protected Point position = new Point(getHudModDeclaration().posX(), getHudModDeclaration().posZ());

    public HudModule() {
        setDrawn(false);
    }

    public HudMod getHudModDeclaration() {
        return getClass().getAnnotation(HudMod.class);
    }

    public abstract void populate(ITheme theme);

    public IFixedComponent getComponent() {
        return component;
    }
}
