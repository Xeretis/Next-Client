package me.lor3mipsum.next.client.impl.modules.hud;

import com.lukflug.panelstudio.hud.HUDList;
import com.lukflug.panelstudio.hud.ListComponent;
import com.lukflug.panelstudio.theme.Theme;
import me.lor3mipsum.next.client.impl.settings.BooleanSetting;
import me.lor3mipsum.next.client.impl.settings.ColorSetting;
import me.lor3mipsum.next.client.module.Category;
import me.lor3mipsum.next.client.module.HudModule;
import net.minecraft.util.Formatting;

import java.awt.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

public class Time extends HudModule {

    public BooleanSetting sortRight = new BooleanSetting("SortRight", true);
    public ColorSetting color = new ColorSetting("Color", new Color(255, 255, 255, 255));

    public Time() {
        super("Time", "Shows you the time on the hud", new Point(957,19), Category.HUD);
    }

    @Override
    public void populate (Theme theme) {
        component = new ListComponent(getName(), theme.getPanelRenderer(), position, new TimeList());
    }

    private class TimeList implements HUDList {

        @Override
        public int getSize() {
            return 1;
        }

        @Override
        public String getItem(int index) {

            return "Time: " + Formatting.GRAY + LocalTime.now().format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT));
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
