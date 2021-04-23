package me.lor3mipsum.next.client.impl.modules.render;

import me.lor3mipsum.next.client.event.EventTarget;
import me.lor3mipsum.next.client.impl.events.WorldRenderEvent;
import me.lor3mipsum.next.client.impl.settings.BooleanSetting;
import me.lor3mipsum.next.client.impl.settings.ColorSetting;
import me.lor3mipsum.next.client.impl.settings.KeybindSetting;
import me.lor3mipsum.next.client.impl.settings.NumberSetting;
import me.lor3mipsum.next.client.module.Category;
import me.lor3mipsum.next.client.module.Module;
import me.lor3mipsum.next.client.utils.render.RenderUtils;
import me.lor3mipsum.next.client.utils.render.WorldRenderUtils;
import me.lor3mipsum.next.client.utils.render.color.QuadColor;
import me.lor3mipsum.next.mixin.ClientPlayerInteractionManagerAccessor;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.BlockBreakingInfo;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.shape.VoxelShape;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class BreakIndicator extends Module {

    public NumberSetting lineWidth = new NumberSetting("LineWidth", 2.5, 0.1, 5.0, 0.1);
    public BooleanSetting text = new BooleanSetting("DrawProgress", false);
    public ColorSetting startLine = new ColorSetting("StartLineColor", new Color(255, 50, 50, 255));
    public ColorSetting startSide = new ColorSetting("StartSideColor", new Color(255, 50, 50, 100));
    public ColorSetting endLine = new ColorSetting("EndLineColor", new Color(50, 255, 50, 255));
    public ColorSetting endSide = new ColorSetting("EndSideColor", new Color(50, 255, 50, 100));
    public BooleanSetting others = new BooleanSetting("OtherPlayers", true);
    public BooleanSetting noDefault = new BooleanSetting("RemoveDefault", true);
    public BooleanSetting smoothAnim = new BooleanSetting("Smooth", true);
    public KeybindSetting keybind = new KeybindSetting(GLFW.GLFW_KEY_UNKNOWN);

    public final Map<Integer, BlockBreakingInfo> blocks = new HashMap<>();

    public BreakIndicator() {
        super("BreakIndicator", "Shows you your break progress in a fancy way", Category.RENDER);
    }

    @Override
    public void onDisable() {
        blocks.clear();
    }

    @EventTarget
    private void onWorldRender(WorldRenderEvent.Post event) {
        ClientPlayerInteractionManagerAccessor iam;
        boolean smooth;

        if (smoothAnim.isOn()) {
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

            if (text.isOn())
                WorldRenderUtils.drawText(Math.round(progress * 100) + "%", pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 0.5);
            RenderUtils.drawBoxBoth(new Box(x1, y1, z1, x2, y2, z2), QuadColor.single(currentSide.getRGB()), QuadColor.single(currentLine.getRGB()), (float) lineWidth.getNumber());

        });
    }

    @Override
    public int getKeybind() {
        return keybind.getKey();
    }

    @Override
    public void setKeybind(int keybind) {
        this.keybind.setKey(keybind);
    }
}
