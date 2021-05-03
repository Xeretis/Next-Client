package me.lor3mipsum.next.client.impl.modules.hud;

import com.lukflug.panelstudio.hud.HUDList;
import com.lukflug.panelstudio.hud.ListComponent;
import com.lukflug.panelstudio.theme.Theme;
import me.lor3mipsum.next.client.event.EventTarget;
import me.lor3mipsum.next.client.impl.events.TickEvent;
import me.lor3mipsum.next.client.impl.settings.BooleanSetting;
import me.lor3mipsum.next.client.impl.settings.ColorSetting;
import me.lor3mipsum.next.client.impl.settings.ModeSetting;
import me.lor3mipsum.next.client.module.Category;
import me.lor3mipsum.next.client.module.HudModule;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.Formatting;

import java.awt.*;

public class SpeedHud extends HudModule {

    public ModeSetting spUnit = new ModeSetting("Unit", KMH, KMH, BPS, MPH);
    public BooleanSetting sortRight = new BooleanSetting("SortRight", true);
    public ColorSetting color = new ColorSetting("Color", new Color(255, 255, 255, 255));

    private static final String BPS = "b/s";
    private static final String KMH = "km/h";
    private static final String MPH = "mph";

    private String speedString = "";

    public SpeedHud() {
        super("SpeedHud", "Shows your speed on the hud", new Point(957,4), Category.HUD);
    }

    @Override
    public void populate (Theme theme) {
        component = new ListComponent(getName(), theme.getPanelRenderer(), position, new SpeedList());
    }

    @EventTarget
    private void onTick(TickEvent.Post event)  {
        ClientPlayerEntity player = mc.player;
        if (player == null) return;

        String unit = spUnit.getMode();
        double speed = calcSpeed(player, unit);
        double displaySpeed = speed;

        speedString = "Speed: " + Formatting.GRAY + String.format("%.2f", displaySpeed) + ' ' + Formatting.RESET + unit;
    }

    private double calcSpeed(ClientPlayerEntity player, String unit) {
        double xDiff = player.getX() - player.prevX;
        double zDiff = player.getZ() - player.prevZ;

        double speed = Math.hypot(xDiff, zDiff) * 20;

        switch (unit) {
            case KMH:
                speed *= 3.6;
                break;
            case MPH:
                speed *= 2.237;
                break;
            default:
                break;
        }

        return speed;
    }

    private class SpeedList implements HUDList {

        @Override
        public int getSize() {
            return 1;
        }

        @Override
        public String getItem(int index) {
            return speedString;
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
