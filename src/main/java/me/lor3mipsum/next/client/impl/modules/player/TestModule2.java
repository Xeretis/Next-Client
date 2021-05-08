package me.lor3mipsum.next.client.impl.modules.player;

import me.lor3mipsum.next.Main;
import me.lor3mipsum.next.api.util.client.ChatUtils;
import me.lor3mipsum.next.api.util.misc.NextColor;
import me.lor3mipsum.next.client.core.module.Category;
import me.lor3mipsum.next.client.core.module.Module;
import me.lor3mipsum.next.client.core.module.annotation.Mod;
import me.lor3mipsum.next.client.core.setting.SettingSeparator;
import me.lor3mipsum.next.client.impl.settings.*;
import org.lwjgl.glfw.GLFW;

@Mod(name = "TestModule2", description = "Test description", category = Category.PLAYER, bind = GLFW.GLFW_KEY_R)
public class TestModule2 extends Module {
    public enum TestEnum {
        Val1,
        Val2,
        NotVal4
    }

    public BooleanSetting bo = new BooleanSetting("Bo", false);
    public IntegerSetting nu = new IntegerSetting("Nu", 10, 9, 11);
    public DoubleSetting du = new DoubleSetting("Du", 5.0, 6.0, 7.0);
    public FloatSetting fu = new FloatSetting("Fu", 15f, 14f, 16f);

    public SettingSeparator su = new SettingSeparator("");

    public EnumSetting<TestEnum> en = new EnumSetting<TestEnum>("En", TestEnum.Val1);
    public EnumSetting<TestEnum> in = new EnumSetting<TestEnum>("In", TestEnum.Val1, false);
    public StringSetting st = new StringSetting("St", "");
    public KeyBindSetting ke = new KeyBindSetting("Ke", GLFW.GLFW_KEY_UNKNOWN);
    public ColorSetting co = new ColorSetting("Co", false, new NextColor(255, 255, 255, 255));



    @Override
    public void onEnable() {
        Main.LOG.info("TestModule enabled");
        ChatUtils.moduleInfo(this, "hey");
    }

    @Override
    public void onDisable() {
        Main.LOG.info("TestModule disabled");
    }
}
