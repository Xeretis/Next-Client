package me.lor3mipsum.next.client.impl.modules.player;

import me.lor3mipsum.next.api.event.NextEvent;
import me.lor3mipsum.next.api.event.client.TickEvent;
import me.lor3mipsum.next.api.event.world.WorldRenderEvent;
import me.lor3mipsum.next.api.util.misc.NextColor;
import me.lor3mipsum.next.api.util.player.PlaceUtils;
import me.lor3mipsum.next.api.util.render.RenderUtils;
import me.lor3mipsum.next.api.util.render.color.QuadColor;
import me.lor3mipsum.next.client.core.module.Category;
import me.lor3mipsum.next.client.core.module.Module;
import me.lor3mipsum.next.client.core.module.annotation.Mod;
import me.lor3mipsum.next.client.core.setting.SettingSeparator;
import me.lor3mipsum.next.client.impl.settings.BooleanSetting;
import me.lor3mipsum.next.client.impl.settings.ColorSetting;
import me.lor3mipsum.next.client.impl.settings.DoubleSetting;
import me.lor3mipsum.next.client.impl.settings.IntegerSetting;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.item.BlockItem;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.awt.*;

@Mod(name = "AirPlace", description = "Allows you to place blocks in air", category = Category.EXPLOIT, enabled = true)
public class AirPlace extends Module {

    public SettingSeparator generalSep = new SettingSeparator("General");

    public IntegerSetting dist = new IntegerSetting("Offset", 0, 0, 2);
    public BooleanSetting swing = new BooleanSetting("SwingHand", true);

    public SettingSeparator renderSep = new SettingSeparator("Render");

    public ColorSetting side = new ColorSetting("SidesColor", false, new NextColor(255, 255, 255, 100));
    public ColorSetting line = new ColorSetting("LinesColor", false, new NextColor(255, 255, 255, 255));
    public DoubleSetting lineWidth = new DoubleSetting("LineWidth", 2.5f, 0.1f, 5f);

    private BlockPos target;

    @EventHandler
    private Listener<TickEvent> onTick = new Listener<>(event -> {
        if (mc.player == null || mc.world == null || (mc.crosshairTarget == null) || !(mc.crosshairTarget instanceof BlockHitResult) || !(mc.player.getMainHandStack().getItem() instanceof BlockItem)) return;

        target = ((BlockHitResult) mc.crosshairTarget).getBlockPos();

        Vec3d forward = Vec3d.fromPolar(mc.player.pitch, mc.player.yaw).normalize();

        target = target.offset(Direction.Axis.X, (int) ((dist.getValue() + 1) * forward.getX()));
        target = target.offset(Direction.Axis.Z, (int) ((dist.getValue() + 1) * forward.getZ()));

        if (!(mc.world.getBlockState(((BlockHitResult) mc.crosshairTarget).getBlockPos()).isAir() && mc.world.getBlockState(target).isAir())) return;

        if (mc.options.keyUse.wasPressed() || mc.options.keyUse.isPressed())
            PlaceUtils.placeBlock(target, Hand.MAIN_HAND, true, true, swing.getValue(), false, 0);
    }, event -> event.era == NextEvent.Era.POST);

    @EventHandler
    private Listener<WorldRenderEvent> onRender = new Listener<>(event -> {
        if (target == null)
            return;

        if (!(mc.crosshairTarget instanceof BlockHitResult)
                || !mc.world.getBlockState(((BlockHitResult) mc.crosshairTarget).getBlockPos()).isAir()
                || !mc.world.getBlockState(target).isAir()
                || !(mc.player.getMainHandStack().getItem() instanceof BlockItem))
            return;

        RenderUtils.drawBoxBoth(target, QuadColor.gradient(side.getValue().getRGB(), Color.WHITE.getBlue(), QuadColor.CardinalDirection.NORTH), QuadColor.single(line.getValue().getRGB()), lineWidth.getValue().floatValue());
    }, event -> event.era == NextEvent.Era.POST);
}
