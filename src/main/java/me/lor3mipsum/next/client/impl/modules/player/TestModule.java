package me.lor3mipsum.next.client.impl.modules.player;

import me.lor3mipsum.next.api.event.game.RenderEvent;
import me.lor3mipsum.next.api.event.world.WorldRenderEvent;
import me.lor3mipsum.next.api.util.client.FontUtils;
import me.lor3mipsum.next.api.util.misc.NextColor;
import me.lor3mipsum.next.api.util.render.RenderUtils;
import me.lor3mipsum.next.api.util.render.color.QuadColor;
import me.lor3mipsum.next.client.core.module.Category;
import me.lor3mipsum.next.client.core.module.Module;
import me.lor3mipsum.next.client.core.module.annotation.Mod;
import me.lor3mipsum.next.client.core.setting.SettingSeparator;
import me.lor3mipsum.next.client.impl.settings.*;
import me.lor3mipsum.next.mixin.WorldRendererMixin;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.util.List;

@Mod(name = "TestModule", description = "Test description", category = Category.PLAYER, bind = GLFW.GLFW_KEY_R)
public class TestModule extends Module {
    public enum TestEnum {
        Val1,
        Val2,
        NotVal4
    }

    public BooleanSetting bo = new BooleanSetting("Bo", false);
    public IntegerSetting nu = new IntegerSetting("Nu", 10, 9, 11);
    public DoubleSetting du = new DoubleSetting("Du", 5.0, 6.0, 7.0);
    public FloatSetting fu = new FloatSetting("Fu", 15f, 14f, 16f);

    public SettingSeparator su = new SettingSeparator("Setting Separator");

    public EnumSetting<TestEnum> en = new EnumSetting<>("En", TestEnum.Val1);
    public EnumSetting<TestEnum> in = new EnumSetting<>("In", TestEnum.Val1, false);
    public StringSetting st = new StringSetting("String", "");
    public KeyBindSetting ke = new KeyBindSetting("Ke", GLFW.GLFW_KEY_UNKNOWN);
    public ColorSetting co = new ColorSetting("Co", false, new NextColor(255, 255, 255, 255));

    @EventHandler
    Listener<WorldRenderEvent> onRender = new Listener<>(event -> {
        RenderUtils.drawBoxBoth(mc.player.getBlockPos(), QuadColor.single(co.getValue().getRGB()), 2.5f);
    });
}
