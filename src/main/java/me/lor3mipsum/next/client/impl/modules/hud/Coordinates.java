package me.lor3mipsum.next.client.impl.modules.hud;

import com.lukflug.panelstudio.hud.HUDList;
import com.lukflug.panelstudio.hud.ListComponent;
import com.lukflug.panelstudio.setting.Labeled;
import com.lukflug.panelstudio.theme.ITheme;
import me.lor3mipsum.next.api.event.NextEvent;
import me.lor3mipsum.next.api.event.client.TickEvent;
import me.lor3mipsum.next.api.util.misc.NextColor;
import me.lor3mipsum.next.client.core.gui.NextGui;
import me.lor3mipsum.next.client.core.module.Category;
import me.lor3mipsum.next.client.core.module.HudModule;
import me.lor3mipsum.next.client.core.module.annotation.HudMod;
import me.lor3mipsum.next.client.core.module.annotation.Mod;
import me.lor3mipsum.next.client.impl.settings.BooleanSetting;
import me.lor3mipsum.next.client.impl.settings.ColorSetting;
import me.lor3mipsum.next.client.impl.settings.IntegerSetting;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.util.Formatting;

import java.awt.*;

@Mod(name = "Coordinates", description = "Displays your coords", category = Category.HUD)
@HudMod(posX = 0, posY = 515)
public class Coordinates extends HudModule {

    public BooleanSetting sortRight = new BooleanSetting("SortRight", false);
    public BooleanSetting sortUp = new BooleanSetting("SortUp", false);
    public IntegerSetting decimals = new IntegerSetting("Decimals", 1, 0, 5);
    public ColorSetting color = new ColorSetting("Color", false, new NextColor(255, 255, 255, 255));

    private final String[] coordinateString = {"", ""};

    @Override
    public void populate(ITheme theme) {
        component = new ListComponent(new Labeled(getName(),null,()->true), position, getName(), new CoordinateList(), NextGui.FONT_HEIGHT, 1);
    }

    @EventHandler
    private Listener<TickEvent> onTick = new Listener<>(event -> {
        if (mc.player == null || mc.world == null || mc.gameRenderer == null)
            return;

        double x1 = mc.gameRenderer.getCamera().getPos().x;
        double y1 = mc.gameRenderer.getCamera().getPos().y - mc.player.getEyeHeight(mc.player.getPose());
        double z1 = mc.gameRenderer.getCamera().getPos().z;

        coordinateString[0] = "XYZ " + getFormattedCoords(x1, y1, z1);

        switch (mc.world.getRegistryKey().getValue().getPath()) {
            case "the_nether":
                coordinateString[1] = "Overworld "
                        + getFormattedCoords(x1 * 8.0, y1, z1 * 8.0);
                break;
            case "the_end":
                break;
            default:
                coordinateString[1] = "Nether "
                        + getFormattedCoords(x1 / 8.0, y1, z1 / 8.0);
                break;
        }
    }, event -> event.era == NextEvent.Era.POST);

    private String getFormattedCoords(double x, double y, double z) {
        return "[" + Formatting.GRAY + round(x) + Formatting.RESET + ", " + Formatting.GRAY + round(y) + Formatting.RESET + ", " + Formatting.GRAY + round(z) + Formatting.RESET + "]";
    }

    private String round(double input) {
        String separatorFormat;

        return String.format("%." + decimals.getValue() + 'f', input);
    }

    private class CoordinateList implements HUDList {
        @Override
        public int getSize() {
            return 2;
        }

        @Override
        public String getItem(int index) {
            return coordinateString[index];
        }

        @Override
        public Color getItemColor(int index) {
            return color.getValue();
        }

        @Override
        public boolean sortUp() {
            return sortUp.getValue();
        }

        @Override
        public boolean sortRight() {
            return sortRight.getValue();
        }
    }
}
