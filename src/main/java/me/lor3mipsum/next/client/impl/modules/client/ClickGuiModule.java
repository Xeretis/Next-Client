package me.lor3mipsum.next.client.impl.modules.client;

import me.lor3mipsum.next.Next;
import me.lor3mipsum.next.client.event.EventTarget;
import me.lor3mipsum.next.client.impl.events.KeyEvent;
import me.lor3mipsum.next.client.impl.settings.BooleanSetting;
import me.lor3mipsum.next.client.impl.settings.ColorSetting;
import me.lor3mipsum.next.client.impl.settings.ModeSetting;
import me.lor3mipsum.next.client.impl.settings.NumberSetting;
import me.lor3mipsum.next.client.module.Category;
import me.lor3mipsum.next.client.module.Module;
import org.lwjgl.glfw.GLFW;

import java.awt.*;

public class ClickGuiModule extends Module {
    public static ClickGuiModule INSTANCE;

    public NumberSetting animationSpeed = new NumberSetting("AnimationSpeed", 10, 0, 100, 1);
    public NumberSetting scrollSpeed = new NumberSetting("ScrollSpeed", 10, 0, 100, 1);
    public ColorSetting activeColor = new ColorSetting("ActiveColor", new Color(121, 193, 255, 255));
    public ColorSetting backgroundColor = new ColorSetting("BackgroundColor", new Color(0, 0, 0, 200));
    public ColorSetting settingBackgroundColor = new ColorSetting("SettingBgColor", new Color(0, 0, 0, 255));
    public ColorSetting outlineColor = new ColorSetting("SettingHighlightColor", new Color(255, 255, 255, 255));
    public ColorSetting fontColor = new ColorSetting("CategoryColor", new Color(121, 193, 255, 255));
    public NumberSetting opacity = new NumberSetting("Opacity", 255, 0, 255, 5);
    public ModeSetting descriptionMode = new ModeSetting("DescriptionMode", "Mouse", "Mouse", "Fixed");
    public ModeSetting scrollMode = new ModeSetting("ScrollMode", "Container", "Container", "Screen");
    public BooleanSetting thinGui = new BooleanSetting("ThinGui", false);

    public ClickGuiModule() {
        super("ClickGui", "The settings of the click gui.", Category.CLIENT, true, false, Next.GUI_KEY);
        INSTANCE = this;
    }

    public static Module getClickGuiModule() {
        return INSTANCE;
    }

    @EventTarget
    public void keyEvent(KeyEvent event) {
        if(event.getKey() == GLFW.GLFW_KEY_ESCAPE)
            this.toggle();
    }

    @Override
    public void onEnable() {
        Next.INSTANCE.clickGui.enterGUI();
    }
}
