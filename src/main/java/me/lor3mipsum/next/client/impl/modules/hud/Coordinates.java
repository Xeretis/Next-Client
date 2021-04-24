package me.lor3mipsum.next.client.impl.modules.hud;

import com.lukflug.panelstudio.hud.HUDList;
import com.lukflug.panelstudio.hud.ListComponent;
import com.lukflug.panelstudio.theme.Theme;
import me.lor3mipsum.next.client.event.EventTarget;
import me.lor3mipsum.next.client.impl.events.TickEvent;
import me.lor3mipsum.next.client.impl.settings.BooleanSetting;
import me.lor3mipsum.next.client.impl.settings.ColorSetting;
import me.lor3mipsum.next.client.impl.settings.NumberSetting;
import me.lor3mipsum.next.client.module.Category;
import me.lor3mipsum.next.client.module.HudModule;
import me.lor3mipsum.next.client.utils.Utils;
import net.minecraft.util.Formatting;

import java.awt.*;

public class Coordinates extends HudModule {

    public BooleanSetting sortRight = new BooleanSetting("SortRight", false);
    public BooleanSetting sortUp = new BooleanSetting("SortUp", false);
    public NumberSetting decimals = new NumberSetting("Decimals", 1, 0, 5, 1);
    public ColorSetting color = new ColorSetting("Color", new Color(255, 255, 255, 255));

    private final String[] coordinateString = {"", ""};

    public Coordinates() {
        super("Coordinates", "Shows you your coords", new Point(0, 515), Category.HUD);
    }

    @Override
    public void populate(Theme theme) {
        component = new ListComponent(getName(), theme.getPanelRenderer(), position, new CoordinateList());
    }

    @EventTarget
    public void onTick(TickEvent.Post e) {
        double x1 = mc.gameRenderer.getCamera().getPos().x;
        double y1 = mc.gameRenderer.getCamera().getPos().y - mc.player.getEyeHeight(mc.player.getPose());
        double z1 = mc.gameRenderer.getCamera().getPos().z;

        coordinateString[0] = "XYZ " + getFormattedCoords(x1, y1, z1);

        switch (Utils.getDimension()) {
            case Nether:
                coordinateString[1] = "Overworld "
                        + getFormattedCoords(x1 * 8.0, y1, z1 * 8.0);
                break;
            case Overworld:
                coordinateString[1] = "Nether "
                        + getFormattedCoords(x1 / 8.0, y1, z1 / 8.0);
                break;
        }
    }

    private String getFormattedCoords(double x, double y, double z) {
        return "[" + Formatting.GRAY + round(x) + Formatting.RESET + ", " + Formatting.GRAY + round(y) + Formatting.RESET + ", " + Formatting.GRAY + round(z) + Formatting.RESET + "]";
    }

    private String round(double input) {
        String separatorFormat;

        return String.format("%." + (int) decimals.getNumber() + 'f', input);
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
            return sortUp.isOn();
        }

        @Override
        public boolean sortRight() {
            return sortRight.isOn();
        }
    }
}
