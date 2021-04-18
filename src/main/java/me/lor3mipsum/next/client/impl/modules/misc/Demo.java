package me.lor3mipsum.next.client.impl.modules.misc;

import me.lor3mipsum.next.client.impl.settings.*;
import me.lor3mipsum.next.client.module.Category;
import me.lor3mipsum.next.client.module.Module;
import org.lwjgl.glfw.GLFW;

import java.awt.*;

public class Demo extends Module {

    private NumberSetting testNumberSetting = new NumberSetting("testNumberSetting", 4, 1, 7, 1);
    private BooleanSetting testBooleanSetting = new BooleanSetting("testBooleanSetting", true);
    private ColorSetting testColorSetting = new ColorSetting("testColorSetting", new Color(0, 0, 0, 255));
    private ModeSetting testModeSetting = new ModeSetting("testModeSetting", "first", "first", "second");
    private KeybindSetting testKeybindSetting = new KeybindSetting("testKeybindSetting", GLFW.GLFW_KEY_U);

    public Demo() {
        super("Demo", "A demo module", Category.MISC, true, false, GLFW.GLFW_KEY_R);
    }

    @Override
    public void onEnable() {
        System.out.println("Hello Fabric world!");
    }
}
