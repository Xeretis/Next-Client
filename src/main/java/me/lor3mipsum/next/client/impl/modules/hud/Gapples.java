package me.lor3mipsum.next.client.impl.modules.hud;

import com.lukflug.panelstudio.hud.HUDList;
import com.lukflug.panelstudio.hud.ListComponent;
import com.lukflug.panelstudio.theme.Theme;
import me.lor3mipsum.next.client.impl.settings.BooleanSetting;
import me.lor3mipsum.next.client.impl.settings.ColorSetting;
import me.lor3mipsum.next.client.module.Category;
import me.lor3mipsum.next.client.module.HudModule;
import me.lor3mipsum.next.client.utils.player.InventoryUtils;
import net.minecraft.item.Items;
import net.minecraft.util.Formatting;

import java.awt.*;

public class Gapples extends HudModule {

    public BooleanSetting sortRight = new BooleanSetting("SortRight", false);
    public ColorSetting color = new ColorSetting("Color", new Color(255, 255, 255, 255));

    public Gapples() {
        super("Gapples", "Shows you how many gapples you have in your inventory", new Point(-2, 159), Category.HUD);
    }

    @Override
    public void populate (Theme theme) {
        component = new ListComponent(getName(), theme.getPanelRenderer(), position, new GapplesList());
    }

    private class GapplesList implements HUDList {

        public int gapples = 0;

        @Override
        public int getSize() {
            return 1;
        }

        @Override
        public String getItem(int index) {
            gapples = InventoryUtils.findItemWithCount(Items.ENCHANTED_GOLDEN_APPLE).count;
            return "Gapples: " + Formatting.GRAY + gapples;
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
