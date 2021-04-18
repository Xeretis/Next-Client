package me.lor3mipsum.next.client.impl.modules.client;

import me.lor3mipsum.next.Next;
import me.lor3mipsum.next.client.impl.settings.ColorSetting;
import me.lor3mipsum.next.client.impl.settings.NumberSetting;
import me.lor3mipsum.next.client.module.Category;
import me.lor3mipsum.next.client.module.Module;

import java.awt.*;

public class ClickGuiModule extends Module {
    public static ClickGuiModule INSTANCE;

    public NumberSetting animationSpeed = new NumberSetting("AnimationSpeed", 10, 0, 100, 1);
    public NumberSetting scrollSpeed = new NumberSetting("ScrollSpeed", 10, 0, 100, 1);
    public ColorSetting activeColor = new ColorSetting("ActiveColor", new Color(121, 193, 255, 255));
    public ColorSetting backgroudColor = new ColorSetting("BackgroundColor", new Color(0, 0, 0, 200));
    public ColorSetting settingBackgroundColor = new ColorSetting("SettinBgColor", new Color(0, 0, 0, 255));
    public ColorSetting outlineColor = new ColorSetting("SettingHighlightColor", new Color(255, 255, 255, 255));
    public ColorSetting fontColor = new ColorSetting("CategoryColor", new Color(121, 193, 255, 255));
    public NumberSetting opacity = new NumberSetting("Opacity", 255, 0, 255, 5);

    public ClickGuiModule() {
        super("ClickGui", "The settings of the click gui.", Category.CLIENT, true, false, Next.GUI_KEY);
        INSTANCE = this;
    }

    public static Module getClickGuiModule() {
        return INSTANCE;
    }
}
