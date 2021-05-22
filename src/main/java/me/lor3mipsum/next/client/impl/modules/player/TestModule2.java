package me.lor3mipsum.next.client.impl.modules.player;

import com.google.common.collect.Lists;
import me.lor3mipsum.next.Main;
import me.lor3mipsum.next.api.event.NextEvent;
import me.lor3mipsum.next.api.event.client.TickEvent;
import me.lor3mipsum.next.api.event.game.RenderEvent;
import me.lor3mipsum.next.api.util.misc.NextColor;
import me.lor3mipsum.next.api.util.player.ChatUtils;
import me.lor3mipsum.next.api.util.render.RenderUtils;
import me.lor3mipsum.next.api.util.world.CrystalUtils;
import me.lor3mipsum.next.client.core.module.Category;
import me.lor3mipsum.next.client.core.module.Module;
import me.lor3mipsum.next.client.core.module.annotation.Mod;
import me.lor3mipsum.next.client.core.setting.SettingSeparator;
import me.lor3mipsum.next.client.impl.settings.*;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

import java.util.List;
import java.util.stream.Collectors;

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

    public SettingSeparator su = new SettingSeparator("");

    public EnumSetting<TestEnum> en = new EnumSetting<>("En", TestEnum.Val1);
    public EnumSetting<TestEnum> in = new EnumSetting<>("In", TestEnum.Val1, false);
    public StringSetting st = new StringSetting("St", "");
    public KeyBindSetting ke = new KeyBindSetting("Ke", GLFW.GLFW_KEY_UNKNOWN);
    public ColorSetting co = new ColorSetting("Co", false, new NextColor(255, 255, 255, 255));
}
