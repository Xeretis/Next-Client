package me.lor3mipsum.next.client.impl.modules.hud;

import com.lukflug.panelstudio.hud.HUDList;
import com.lukflug.panelstudio.hud.ListComponent;
import com.lukflug.panelstudio.setting.Labeled;
import com.lukflug.panelstudio.theme.ITheme;
import me.lor3mipsum.next.Main;
import me.lor3mipsum.next.api.event.game.RenderEvent;
import me.lor3mipsum.next.api.util.misc.NextColor;
import me.lor3mipsum.next.client.core.gui.NextGui;
import me.lor3mipsum.next.client.core.module.Category;
import me.lor3mipsum.next.client.core.module.HudModule;
import me.lor3mipsum.next.client.core.module.Module;
import me.lor3mipsum.next.client.core.module.annotation.HudMod;
import me.lor3mipsum.next.client.core.module.annotation.Mod;
import me.lor3mipsum.next.client.impl.settings.BooleanSetting;
import me.lor3mipsum.next.client.impl.settings.ColorSetting;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listenable;
import me.zero.alpine.listener.Listener;
import net.minecraft.util.Formatting;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Mod(name = "ArrayList", description = "Shows you your active modules", category = Category.HUD)
@HudMod(posX = 0, posZ = 200)
public class ArrayListModule extends HudModule {
    public ColorSetting color = new ColorSetting("Color", false, new NextColor(255,255,255));
    public BooleanSetting gradient = new BooleanSetting("Gradient Rainbow", true);
    public BooleanSetting sortUp = new BooleanSetting("Sort Up", false);
    public BooleanSetting sortRight = new BooleanSetting("Sort Right", false);

    private final ModuleList list = new ModuleList();

    @Override
    public void populate(ITheme theme) {
        component = new ListComponent(new Labeled(getName(), null, ()->true), position, getName(), list, NextGui.FONT_HEIGHT, 1);
    }

    @EventHandler
    private Listener<RenderEvent> onRender = new Listener<>(event -> {
        list.activeModules.clear();
        for (Module module : Main.moduleManager.getModules())
            if(module.getEnabled() && module.getDrawn()) list.activeModules.add(module);
        list.activeModules.sort(Comparator.comparing(module -> NextGui.guiInterface.getFontWidth(NextGui.FONT_HEIGHT, module.getName() + Formatting.GRAY + " " + module.getHudInfo())));
    });

    private class ModuleList implements HUDList {

        public List<Module> activeModules = new ArrayList<>();

        @Override
        public int getSize() {
            return activeModules.size();
        }

        @Override
        public String getItem(int index) {
            Module module = activeModules.get(index);
            return (!module.getHudInfo().equals("")) ? module.getName() + Formatting.GRAY + " " + module.getHudInfo() : module.getName();
        }

        @Override
        public Color getItemColor(int index) {
            if (gradient.getValue()) {
                NextColor c = color.getValue();
                return Color.getHSBColor(c.getHue() + (color.getRainbow() ? .02f * index : 0), c.getSaturation(), c.getBrightness());
            } else
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
