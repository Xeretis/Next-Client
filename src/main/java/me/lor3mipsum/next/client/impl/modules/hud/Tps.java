package me.lor3mipsum.next.client.impl.modules.hud;

import com.lukflug.panelstudio.hud.HUDList;
import com.lukflug.panelstudio.hud.ListComponent;
import com.lukflug.panelstudio.theme.Theme;
import me.lor3mipsum.next.client.impl.settings.BooleanSetting;
import me.lor3mipsum.next.client.impl.settings.ColorSetting;
import me.lor3mipsum.next.client.module.Category;
import me.lor3mipsum.next.client.module.HudModule;
import me.lor3mipsum.next.client.utils.world.TpsUtils;
import net.minecraft.util.Formatting;

import java.awt.*;

public class Tps extends HudModule {
    public BooleanSetting sortRight = new BooleanSetting("SortRight", true);
    public ColorSetting color = new ColorSetting("Color", new Color(255, 255, 255, 255));

    public Tps() {
        super("Tps", "Shows the server's tps on the hud", new Point(20,50), Category.HUD);
    }

    @Override
    public void populate (Theme theme) {
        component = new ListComponent(getName(), theme.getPanelRenderer(), position, new TpsList());
    }

    private class TpsList implements HUDList {

        @Override
        public int getSize() {
            return 1;
        }

        @Override
        public String getItem(int index) {
            return "TPS: " + Formatting.GRAY + String.format("%.1f", TpsUtils.INSTANCE.getTickRate());
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
