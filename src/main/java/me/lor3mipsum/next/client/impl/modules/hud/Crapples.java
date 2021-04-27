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

public class Crapples extends HudModule {
    public BooleanSetting sortRight = new BooleanSetting("SortRight", false);
    public ColorSetting color = new ColorSetting("Color", new Color(255, 255, 255, 255));

    public Crapples() {
        super("Crapples", "Shows you how many crapples you have in your inventory", new Point(-2, 178), Category.HUD);
    }

    @Override
    public void populate (Theme theme) {
        component = new ListComponent(getName(), theme.getPanelRenderer(), position, new CrapplesList());
    }

    private class CrapplesList implements HUDList {

        public int crapples = 0;

        @Override
        public int getSize() {
            return 1;
        }

        @Override
        public String getItem(int index) {
            crapples = InventoryUtils.findItemWithCount(Items.GOLDEN_APPLE).count;
            return "Crapples: " + Formatting.GRAY + crapples;
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
