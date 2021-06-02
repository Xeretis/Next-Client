package me.lor3mipsum.next.client.impl.modules.render;

import me.lor3mipsum.next.api.event.NextEvent;
import me.lor3mipsum.next.api.event.world.WorldRenderEvent;
import me.lor3mipsum.next.api.util.misc.NextColor;
import me.lor3mipsum.next.api.util.render.RenderUtils;
import me.lor3mipsum.next.api.util.render.color.QuadColor;
import me.lor3mipsum.next.client.core.module.Category;
import me.lor3mipsum.next.client.core.module.Module;
import me.lor3mipsum.next.client.core.module.annotation.Mod;
import me.lor3mipsum.next.client.impl.settings.BooleanSetting;
import me.lor3mipsum.next.client.impl.settings.ColorSetting;
import me.lor3mipsum.next.client.impl.settings.DoubleSetting;
import me.lor3mipsum.next.mixin.accessor.ClientPlayerInteractionManagerAccessor;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.BlockBreakingInfo;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.shape.VoxelShape;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

@Mod(name = "BreakIndicator", description = "Fancy indication of your block breaking status", category = Category.RENDER)
public class BreakIndicator extends Module {
    public BooleanSetting text = new BooleanSetting("DrawProgress", false);
    public ColorSetting startLine = new ColorSetting("StartLineColor", false, new NextColor(255, 50, 50, 255));
    public ColorSetting startSide = new ColorSetting("StartSideColor", false, new NextColor(255, 50, 50, 100));
    public ColorSetting endLine = new ColorSetting("EndLineColor", false, new NextColor(50, 255, 50, 255));
    public ColorSetting endSide = new ColorSetting("EndSideColor", false, new NextColor(50, 255, 50, 100));
    public DoubleSetting lineWidth = new DoubleSetting("LineWidth", 2.5, 0, 5.0);
    public BooleanSetting others = new BooleanSetting("OtherPlayers", true);
    public BooleanSetting noDefault = new BooleanSetting("RemoveDefault", true);
    public BooleanSetting smoothAnim = new BooleanSetting("Smooth", true);

    public final Map<Integer, BlockBreakingInfo> blocks = new HashMap<>();

    @Override
    public void onDisable() {
        blocks.clear();
    }

    @EventHandler
    private Listener<WorldRenderEvent> onRender = new Listener<>(event -> {
        ClientPlayerInteractionManagerAccessor iam;
        boolean smooth;

        if (smoothAnim.getValue()) {
            iam = (ClientPlayerInteractionManagerAccessor) mc.interactionManager;
            BlockPos pos = iam.getCurrentBreakingBlockPos();
            smooth = pos != null && iam.getBreakingProgress() > 0;

            if (smooth && blocks.values().stream().noneMatch(info -> info.getPos().equals(pos))) {
                blocks.put(mc.player.getEntityId(), new BlockBreakingInfo(mc.player.getEntityId(), pos));
            }
        } else {
            iam = null;
            smooth = false;
        }

        blocks.values().forEach(info -> {
            BlockPos pos = info.getPos();
            int stage = info.getStage();

            BlockState state = mc.world.getBlockState(pos);
            VoxelShape shape = state.getOutlineShape(mc.world, pos);
            if (shape.isEmpty()) return;
            Box orig = shape.getBoundingBox();
            Box box = orig;

            double shrinkFactor;
            if (smooth && iam.getCurrentBreakingBlockPos().equals(pos)) {
                shrinkFactor = 1d - iam.getBreakingProgress();
            } else {
                shrinkFactor = (9 - (stage + 1)) / 9d;
            }
            double progress = 1d - shrinkFactor;

            box = box.shrink(
                    box.getXLength() * shrinkFactor,
                    box.getYLength() * shrinkFactor,
                    box.getZLength() * shrinkFactor
            );

            double xShrink = (orig.getXLength() * shrinkFactor) / 2;
            double yShrink = (orig.getYLength() * shrinkFactor) / 2;
            double zShrink = (orig.getZLength() * shrinkFactor) / 2;

            double x1 = pos.getX() + box.minX + xShrink;
            double y1 = pos.getY() + box.minY + yShrink;
            double z1 = pos.getZ() + box.minZ + zShrink;
            double x2 = pos.getX() + box.maxX + xShrink;
            double y2 = pos.getY() + box.maxY + yShrink;
            double z2 = pos.getZ() + box.maxZ + zShrink;

            Color currentSide = new Color(
                    (int) Math.round(startSide.getValue().getRed() + (endSide.getValue().getRed() - startSide.getValue().getRed()) * progress),
                    (int) Math.round(startSide.getValue().getGreen() + (endSide.getValue().getGreen() - startSide.getValue().getGreen()) * progress),
                    (int) Math.round(startSide.getValue().getBlue() + (endSide.getValue().getBlue() - startSide.getValue().getBlue()) * progress),
                    (int) Math.round(startSide.getValue().getAlpha() + (endSide.getValue().getAlpha() - startSide.getValue().getAlpha()) * progress)
            );

            Color currentLine = new Color(
                    (int) Math.round(startLine.getValue().getRed() + (endLine.getValue().getRed() - startLine.getValue().getRed()) * progress),
                    (int) Math.round(startLine.getValue().getGreen() + (endLine.getValue().getGreen() - startLine.getValue().getGreen()) * progress),
                    (int) Math.round(startLine.getValue().getBlue() + (endLine.getValue().getBlue() - startLine.getValue().getBlue()) * progress),
                    (int) Math.round(startLine.getValue().getAlpha() + (endLine.getValue().getAlpha() - startLine.getValue().getAlpha()) * progress)
            );

//            if (text.isOn())
//                WorldRenderUtils.drawText(Math.round(progress * 100) + "%", pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 0.5);

            RenderUtils.drawBoxBoth(new Box(x1, y1, z1, x2, y2, z2), QuadColor.single(currentSide.getRGB()), QuadColor.single(currentLine.getRGB()), lineWidth.getValue().floatValue());

        });
    }, event -> event.era == NextEvent.Era.POST);

}
