package me.lor3mipsum.next.client.impl.modules.hud;

import com.lukflug.panelstudio.hud.HUDList;
import com.lukflug.panelstudio.hud.ListComponent;
import com.lukflug.panelstudio.setting.Labeled;
import com.lukflug.panelstudio.theme.ITheme;
import me.lor3mipsum.next.api.util.misc.NextColor;
import me.lor3mipsum.next.api.util.player.InventoryUtils;
import me.lor3mipsum.next.client.core.gui.NextGui;
import me.lor3mipsum.next.client.core.module.Category;
import me.lor3mipsum.next.client.core.module.HudModule;
import me.lor3mipsum.next.client.core.module.annotation.HudMod;
import me.lor3mipsum.next.client.core.module.annotation.Mod;
import me.lor3mipsum.next.client.impl.settings.ColorSetting;
import net.minecraft.item.Items;
import net.minecraft.util.Formatting;

import java.awt.*;

@Mod(name = "Crapples", description = "The number of crapples in your inventory", category = Category.HUD)
@HudMod(posX = 0, posY = 180)
public class Crapples extends HudModule {

    ColorSetting color = new ColorSetting("Color", false, new NextColor(255, 255, 255));

    @Override
    public void populate(ITheme theme) {
        component = new ListComponent(new Labeled(getName(),null,()->true), position, getName(), new CrappleList(), NextGui.FONT_HEIGHT, 1);
    }

    private class CrappleList implements HUDList {

        public int crapple = 0;

        @Override
        public int getSize() {
            return 1;
        }

        @Override
        public String getItem(int index) {
            crapple = InventoryUtils.findItemInAll(Items.GOLDEN_APPLE).count;
            return "Crapples: " + Formatting.GRAY + crapple;
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
