package me.lor3mipsum.next.client.impl.modules.hud;

import com.lukflug.panelstudio.hud.HUDList;
import com.lukflug.panelstudio.hud.ListComponent;
import com.lukflug.panelstudio.theme.Theme;
import me.lor3mipsum.next.client.impl.settings.ColorSetting;
import me.lor3mipsum.next.client.module.Category;
import me.lor3mipsum.next.client.module.HudModule;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.util.Formatting;

import java.awt.*;

public class Ping extends HudModule {
    public ColorSetting color = new ColorSetting("Color", new Color(255, 255, 255, 255));

    public Ping() {
        super("Ping", "Shows your ping on the hud", new Point(-2,19), Category.HUD);
    }

    @Override
    public void populate (Theme theme) {
        component = new ListComponent(getName(), theme.getPanelRenderer(), position, new PingList());
    }

    private class PingList implements HUDList {

        @Override
        public int getSize() {
            return 1;
        }

        @Override
        public String getItem(int index) {
            PlayerListEntry playerListEntry = mc.getNetworkHandler().getPlayerListEntry(mc.player.getUuid());

            String ping = "-1";

            if (playerListEntry != null) ping = Integer.toString(playerListEntry.getLatency());

            return "Ping " + Formatting.GRAY + ping;
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
