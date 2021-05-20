package me.lor3mipsum.next.client.impl.modules.hud;

import com.lukflug.panelstudio.base.Context;
import com.lukflug.panelstudio.base.IInterface;
import com.lukflug.panelstudio.hud.HUDComponent;
import com.lukflug.panelstudio.setting.Labeled;
import com.lukflug.panelstudio.theme.ITheme;
import me.lor3mipsum.next.api.event.game.RenderEvent;
import me.lor3mipsum.next.api.util.misc.NextColor;
import me.lor3mipsum.next.api.util.render.RenderUtils;
import me.lor3mipsum.next.client.core.gui.NextGui;
import me.lor3mipsum.next.client.core.module.Category;
import me.lor3mipsum.next.client.core.module.HudModule;
import me.lor3mipsum.next.client.core.module.annotation.HudMod;
import me.lor3mipsum.next.client.core.module.annotation.Mod;
import me.lor3mipsum.next.client.impl.settings.ColorSetting;
import me.lor3mipsum.next.client.impl.settings.DoubleSetting;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

import java.awt.*;

@Mod(name = "Logo", description = "The logo of the client", category = Category.HUD)
@HudMod(posX = 0, posY = 0)
public class HudLogo extends HudModule {
    public DoubleSetting scale = new DoubleSetting("Scale", 1, 0.1, 5);
    public ColorSetting color = new ColorSetting("Color", false, new NextColor(255, 255, 255, 255));

    private Identifier img = new Identifier("next", "textures/hudlogo.png");

    @Override
    public void populate(ITheme theme) {
        component = new LogoComponent(theme);
    }

    @EventHandler
    private Listener<RenderEvent> onRender = new Listener<>(event -> {
        RenderUtils.drawImage(new MatrixStack(), img, component.getPosition(NextGui.guiInterface).x, component.getPosition(NextGui.guiInterface).y, (int) (50 * scale.getValue()), (int) (50 * scale.getValue()), color.getValue().getRed() / 255f, color.getValue().getGreen() / 255f, color.getValue().getBlue() / 255f, color.getValue().getAlpha() / 255f);
    });

    private class LogoComponent extends HUDComponent {

        public LogoComponent(ITheme theme) {
            super(new Labeled(getName(),null,()->true), HudLogo.this.position, getName());
        }

        @Override
        public void render(Context context) {
            super.render(context);
        }

        @Override
        public Dimension getSize(IInterface inter) {
            if (!((int) (50 * scale.getValue()) > inter.getFontWidth(NextGui.FONT_HEIGHT, "Logo")))
                return new Dimension(inter.getFontWidth(NextGui.FONT_HEIGHT, "Logo"), (int) (50 * scale.getValue()));
            else
                return new Dimension((int) (50 * scale.getValue()),(int) (50 * scale.getValue()));
        }
    }
}
