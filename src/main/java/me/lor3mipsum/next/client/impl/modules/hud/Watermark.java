package me.lor3mipsum.next.client.impl.modules.hud;

import com.lukflug.panelstudio.hud.HUDList;
import com.lukflug.panelstudio.hud.ListComponent;
import com.lukflug.panelstudio.theme.Theme;
import me.lor3mipsum.next.Next;
import me.lor3mipsum.next.client.impl.settings.ColorSetting;
import me.lor3mipsum.next.client.module.Category;
import me.lor3mipsum.next.client.module.HudModule;
import net.minecraft.util.Formatting;

import java.awt.*;

public class Watermark extends HudModule {
    public ColorSetting color = new ColorSetting("Color", new Color(255,255,255,255));

    public Watermark() {
        super("Watermark", "The name and version of the client", new Point(0, 4), Category.HUD);
    }

    @Override
    public void populate(Theme theme) {
        component = new ListComponent(getName(), theme.getPanelRenderer(), position, new WatermarkList());
    }

    private class WatermarkList implements HUDList {
        @Override
        public int getSize() {
            return 1;
        }

        @Override
        public String getItem(int index) {
            return Next.CLIENT_NAME + Formatting.GRAY + " v" + Next.CLIENT_VERSION;
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
