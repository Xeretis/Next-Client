package me.lor3mipsum.next.client.impl.modules.client;

import me.lor3mipsum.next.Next;
import me.lor3mipsum.next.client.event.EventTarget;
import me.lor3mipsum.next.client.gui.clickgui.NextGui;
import me.lor3mipsum.next.client.impl.settings.*;
import me.lor3mipsum.next.client.module.Category;
import me.lor3mipsum.next.client.module.Module;

import java.awt.*;

public class ClickGuiModule extends Module {
    public static ClickGuiModule INSTANCE;

    public NumberSetting animationSpeed = new NumberSetting("AnimSpeed", 10, 0, 100, 1);
    public NumberSetting scrollSpeed = new NumberSetting("ScrollSpeed", 10, 0, 100, 1);
    public ColorSetting activeColor = new ColorSetting("ActiveColor", new Color(100, 206, 252, 255));
    public ColorSetting backgroundColor = new ColorSetting("BackgroundColor", new Color(59, 59, 59, 255));
    public ColorSetting settingBackgroundColor = new ColorSetting("SettingBgColor", new Color(100, 100, 100, 255));
    public ColorSetting outlineColor = new ColorSetting("OutlineColor", new Color(77, 77, 77, 255));
    public ColorSetting highlightColor = new ColorSetting("HighlightColor", new Color(0, 0, 0, 30));
    public ColorSetting fontColor = new ColorSetting("FontColor", new Color(255, 255, 255, 255));
    public NumberSetting opacity = new NumberSetting("Opacity", 255, 0, 255, 5);
    public ModeSetting descriptionMode = new ModeSetting("Desc", "Mouse", "Mouse", "Fixed");
    public ModeSetting scrollMode = new ModeSetting("Scroll", "Container", "Container", "Screen");
    public ModeSetting theme = new ModeSetting("Theme", "Next", "Next", "GameSense", "Clear", "ClearGradient");
    public BooleanSetting line = new BooleanSetting("Line", true);
    public BooleanSetting outline = new BooleanSetting("NextOutline", false);
    public KeybindSetting keybind = new KeybindSetting(Next.GUI_KEY);

    public ClickGuiModule() {
        super("ClickGui", "The settings of the click gui.", Category.CLIENT);
        INSTANCE = this;
    }

    @Override
    public int getKeybind() {
        return keybind.getKey();
    }

    @Override
    public void setKeybind(int keybind) {
        this.keybind.setKey(keybind);
    }

    @Override
    public void onEnable() {
        Next.INSTANCE.clickGui.enterGUI();
        this.toggle();
    }
}
