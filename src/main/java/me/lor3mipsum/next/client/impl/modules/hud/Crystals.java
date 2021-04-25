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

public class Crystals extends HudModule {
    public BooleanSetting sortRight = new BooleanSetting("SortRight", false);
    public ColorSetting color = new ColorSetting("Color", new Color(255, 255, 255, 255));

    public Crystals() {
        super("Crystals", "Shows you how many crystals you have in your inventory", new Point(40, 120), Category.HUD);
    }

    @Override
    public void populate (Theme theme) {
        component = new ListComponent(getName(), theme.getPanelRenderer(), position, new CrystalsList());
    }

    private class CrystalsList implements HUDList {

        public int crystals = 0;

        @Override
        public int getSize() {
            return 1;
        }

        @Override
        public String getItem(int index) {
            crystals = InventoryUtils.findItemWithCount(Items.END_CRYSTAL).count;
            return "Crystals: " + Formatting.GRAY + crystals;
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
