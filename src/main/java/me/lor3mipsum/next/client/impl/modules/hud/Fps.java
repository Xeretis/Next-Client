package me.lor3mipsum.next.client.impl.modules.hud;

import com.lukflug.panelstudio.hud.HUDList;
import com.lukflug.panelstudio.hud.ListComponent;
import com.lukflug.panelstudio.theme.Theme;
import me.lor3mipsum.next.Next;
import me.lor3mipsum.next.client.impl.settings.BooleanSetting;
import me.lor3mipsum.next.client.impl.settings.ColorSetting;
import me.lor3mipsum.next.client.module.Category;
import me.lor3mipsum.next.client.module.HudModule;
import me.lor3mipsum.next.mixin.MinecraftClientAccessor;
import net.minecraft.util.Formatting;

import java.awt.*;

public class Fps extends HudModule {
    public BooleanSetting sortRight = new BooleanSetting("SortRight", true);
    public ColorSetting color = new ColorSetting("Color", new Color(255,255,255,255));

    public Fps() {
        super("Fps", "Displays your fps", new Point(100, 50), Category.HUD);
    }

    @Override
    public void populate(Theme theme) {
        component = new ListComponent(getName(), theme.getPanelRenderer(), position, new FpsList());
    }

    private class FpsList implements HUDList {
        @Override
        public int getSize() {
            return 1;
        }

        @Override
        public String getItem(int index) {
            return "FPS: " + Formatting.GRAY + ((MinecraftClientAccessor) mc).getFps();
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
            return sortRight.isOn();
        }
    }
}
