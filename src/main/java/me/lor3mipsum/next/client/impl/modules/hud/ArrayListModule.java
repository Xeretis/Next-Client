package me.lor3mipsum.next.client.impl.modules.hud;

import com.lukflug.panelstudio.hud.HUDList;
import com.lukflug.panelstudio.hud.ListComponent;
import com.lukflug.panelstudio.theme.Theme;
import me.lor3mipsum.next.Next;
import me.lor3mipsum.next.client.event.EventTarget;
import me.lor3mipsum.next.client.impl.events.RenderEvent;
import me.lor3mipsum.next.client.impl.settings.BooleanSetting;
import me.lor3mipsum.next.client.impl.settings.ColorSetting;
import me.lor3mipsum.next.client.module.Category;
import me.lor3mipsum.next.client.module.HudModule;
import me.lor3mipsum.next.client.module.Module;
import me.lor3mipsum.next.client.utils.FontUtils;
import net.minecraft.util.Formatting;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ArrayListModule extends HudModule {
    public BooleanSetting sortRight = new BooleanSetting("SortRight", true);
    public BooleanSetting sortUp = new BooleanSetting("SortUp", false);
    public ColorSetting color = new ColorSetting("Color", new Color(255, 255, 255, 255));

    private ModuleList list = new ModuleList();

    public ArrayListModule() {
        super("ArrayList", "Renders active modules on your screen", new Point(785, 310), Category.HUD);
    }

    @Override
    public void populate(Theme theme) {
        component = new ListComponent(getName(), theme.getPanelRenderer(), position, list);
    }

    @EventTarget
    private void onRender(RenderEvent e) {
        list.activeModules.clear();
        for (Module module : Next.INSTANCE.moduleManager.getModules())
            if(module.isOn() && module.getDrawn()) list.activeModules.add(module);
        list.activeModules.sort(Comparator.comparing(module -> FontUtils.getStringWidth(module.getName() + Formatting.GRAY + " " + module.getHudInfo())));
    }

    private class ModuleList implements HUDList {

        public List<Module> activeModules = new ArrayList<Module>();

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
            return color.getValue();
            //return Color.getHSBColor(c.RGBtoHSB(color.getColor().getRed(), color.getColor().getGreen(), color.getColor().getBlue(), null)[0] + (color.getRainbow() ? (float) rainbowSpeed.getNumber() * index : 0), c.RGBtoHSB(color.getColor().getRed(), color.getColor().getGreen(), color.getColor().getBlue(), null)[1], c.RGBtoHSB(color.getColor().getRed(), color.getColor().getGreen(), color.getColor().getBlue(), null)[2]);
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
