package me.lor3mipsum.next.client.impl.modules.client;

import me.lor3mipsum.next.Main;
import me.lor3mipsum.next.api.util.misc.NextColor;
import me.lor3mipsum.next.client.core.module.Category;
import me.lor3mipsum.next.client.core.module.Module;
import me.lor3mipsum.next.client.core.module.annotation.Mod;
import me.lor3mipsum.next.client.core.setting.SettingSeparator;
import me.lor3mipsum.next.client.impl.settings.BooleanSetting;
import me.lor3mipsum.next.client.impl.settings.ColorSetting;
import me.lor3mipsum.next.client.impl.settings.EnumSetting;
import me.lor3mipsum.next.client.impl.settings.IntegerSetting;
import org.lwjgl.glfw.GLFW;

@Mod(name = "ClickGui", description = "What you are currently looking at", category = Category.CLIENT, bind = GLFW.GLFW_KEY_RIGHT_SHIFT)
public class ClickGuiModule extends Module {
    public enum DescriptionMode {
        Mouse,
        Fixed
    }

    public enum ScrollMode {
        Container,
        Screen
    }

    public enum Layout {
        CSGO,
        Normal,
        Searchable
    }

    public SettingSeparator generalSep = new SettingSeparator("General");

    public IntegerSetting animationSpeed = new IntegerSetting("Animation Speed", 10, 0, 100);
    public IntegerSetting scrollSpeed = new IntegerSetting("Scroll Speed", 10, 0, 100);
    public EnumSetting<ScrollMode> scrollMode = new EnumSetting<>("Scroll Model", ScrollMode.Container);
    public EnumSetting<DescriptionMode> descriptionMode = new EnumSetting<>("Desc Mode", DescriptionMode.Mouse);
    public EnumSetting<Layout> layout = new EnumSetting<>("Layout", Layout.CSGO);

    public SettingSeparator colorSep = new SettingSeparator("Colors");

    @Override
    public void onEnable() {
        if (Main.clickGui != null)
            Main.clickGui.enterGUI();
        setEnabled(false);
    }

    public void registerColor (String name, boolean rainbow, NextColor value, boolean isVisible, boolean rainbowEnabled, boolean alphaEnabled) {
        ColorSetting setting = new ColorSetting(name, rainbow, value, isVisible, rainbowEnabled, alphaEnabled);
        Main.settingManager.registerSetting(getName(), setting);
    }
}
