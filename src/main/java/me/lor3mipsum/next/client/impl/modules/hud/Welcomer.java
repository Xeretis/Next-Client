package me.lor3mipsum.next.client.impl.modules.hud;

import com.lukflug.panelstudio.hud.HUDList;
import com.lukflug.panelstudio.hud.ListComponent;
import com.lukflug.panelstudio.theme.Theme;
import me.lor3mipsum.next.client.impl.settings.ColorSetting;
import me.lor3mipsum.next.client.module.Category;
import me.lor3mipsum.next.client.module.HudModule;
import net.minecraft.util.Formatting;

import java.awt.*;

public class Welcomer extends HudModule {
    public ColorSetting color = new ColorSetting("Color", new Color(255,255,255,255));

    public Welcomer() {
        super("Welcomer", "The most useless module ever", new Point(75, 70), Category.HUD);
    }

    @Override
    public void populate(Theme theme) {
        component = new ListComponent(getName(), theme.getPanelRenderer(), position, new WelcomerList());
    }

    private class WelcomerList implements HUDList {
        @Override
        public int getSize() {
            return 1;
        }

        @Override
        public String getItem(int index) {
            return "Welcome to Next Client, " + Formatting.GRAY + mc.player.getName().asString() + Formatting.RESET + ".";
        }

        @Override
        public Color getItemColor(int index) {
            return color.getValue();
        }

        @Override
        public boolean sortUp() {
            return false;
        }

        @Override
        public boolean sortRight() {
            return false;
        }
    }
}
