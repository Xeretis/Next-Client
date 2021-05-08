package me.lor3mipsum.next.client.impl.modules.client;

import me.lor3mipsum.next.Main;
import me.lor3mipsum.next.api.util.misc.NextColor;
import me.lor3mipsum.next.client.core.module.Category;
import me.lor3mipsum.next.client.core.module.Module;
import me.lor3mipsum.next.client.core.module.annotation.Mod;
import me.lor3mipsum.next.client.impl.settings.BooleanSetting;
import me.lor3mipsum.next.client.impl.settings.ColorSetting;
import me.lor3mipsum.next.client.impl.settings.EnumSetting;
import me.lor3mipsum.next.client.impl.settings.IntegerSetting;
import org.lwjgl.glfw.GLFW;

@Mod(name = "ClickGui", description = "What you are currently looking at", category = Category.CLIENT, bind = GLFW.GLFW_KEY_RIGHT_SHIFT)
public class ClickGuiModule extends Module {
    public enum DescriptionMode {
        Fixed,
        Mouse
    }

    public enum ScrollMode {
        Container,
        Screen
    }

    public IntegerSetting animationSpeed = new IntegerSetting("AnimSpeed", 10, 0, 100);
    public IntegerSetting scrollSpeed = new IntegerSetting("ScrollSpeed", 10, 0, 100);
    public EnumSetting<ScrollMode> scrollMode = new EnumSetting<ScrollMode>("Scroll", ScrollMode.Container);
    public EnumSetting<DescriptionMode> descriptionMode = new EnumSetting<>("Desc", DescriptionMode.Mouse);
    public BooleanSetting csgoLayout = new BooleanSetting("CSGO Layout", false);

    @Override
    public void onEnable() {
        if (Main.clickGui != null)
            Main.clickGui.enterGUI();
        setEnabled(false);
    }

    public ColorSetting registerColor (String name, boolean rainbow, NextColor value, boolean isVisible, boolean rainbowEnabled, boolean alphaEnabled) {
        ColorSetting setting = new ColorSetting(name, rainbow, value, isVisible, rainbowEnabled, alphaEnabled);
        Main.settingManager.registerSetting(getName(), setting);
        return setting;
    }
}
