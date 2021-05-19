package me.lor3mipsum.next.api.util.player;

import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class PlaceUtils {

    private static final MinecraftClient mc = MinecraftClient.getInstance();

    public static void placeBlock(BlockPos blockPos, Hand hand, boolean airPlace) {
        placeBlock(blockPos, hand, airPlace, true, false, 0);
    }

    public static void placeBlock(BlockPos blockPos, Hand hand, boolean airPlace, boolean swingHand) {
        placeBlock(blockPos, hand, airPlace, swingHand, false, 0);
    }

    public static void placeBlock(BlockPos blockPos, Hand hand, boolean airPlace, boolean swingHand, boolean rotate, int priority) {
        placeBlock(blockPos, hand, airPlace, false, swingHand, rotate, priority);
    }

    public static void placeBlock(BlockPos blockPos, Hand hand, boolean airPlace, boolean strictAirPlace, boolean swingHand, boolean rotate, int priority) {

        if (strictAirPlace) {
            if (rotate)
                RotationUtils.INSTANCE.rotate(RotationUtils.getYaw(blockPos), RotationUtils.getPitch(blockPos), priority, () -> mc.interactionManager.interactBlock(mc.player, mc.world, hand, new BlockHitResult(Vec3d.of(blockPos), Direction.UP, blockPos, false)));
            else
                mc.interactionManager.interactBlock(mc.player, mc.world, hand, new BlockHitResult(Vec3d.of(blockPos), Direction.UP, blockPos, false));
            return;
        }

        BlockPos north = blockPos.north();
        BlockPos east = blockPos.east();
        BlockPos south = blockPos.south();
        BlockPos west = blockPos.west();
        BlockPos down = blockPos.down();
        BlockPos up = blockPos.up();

        BlockPos placePos = null;
        Direction placeDir = null;

        if (!mc.world.getBlockState(north).getMaterial().isReplaceable()) {
            placePos = north;
            placeDir = Direction.SOUTH;
        } else if (!mc.world.getBlockState(south).getMaterial().isReplaceable()) {
            placePos = south;
            placeDir = Direction.NORTH;
        } else if (!mc.world.getBlockState(east).getMaterial().isReplaceable()) {
            placePos = east;
            placeDir = Direction.WEST;
        } else if (!mc.world.getBlockState(west).getMaterial().isReplaceable()) {
            placePos = west;
            placeDir = Direction.EAST;
        } else if (!mc.world.getBlockState(up).getMaterial().isReplaceable()) {
            placePos = up;
            placeDir = Direction.DOWN;
        } else if (!mc.world.getBlockState(down).getMaterial().isReplaceable()) {
            placePos = down;
            placeDir = Direction.UP;
        }
        if (placePos == null || placeDir == null)
            if (airPlace)
                if (rotate)
                    RotationUtils.INSTANCE.rotate(RotationUtils.getYaw(blockPos), RotationUtils.getPitch(blockPos), priority, () -> mc.interactionManager.interactBlock(mc.player, mc.world, hand, new BlockHitResult(Vec3d.of(blockPos), Direction.UP, blockPos, false)));
                else
                    mc.interactionManager.interactBlock(mc.player, mc.world, hand, new BlockHitResult(Vec3d.of(blockPos), Direction.UP, blockPos, false));
        else {
            Vec3d placeVec = Vec3d.of(placePos);
            BlockHitResult blockHitResult = new BlockHitResult(placeVec, placeDir, placePos, false);
            if (rotate)
                RotationUtils.INSTANCE.rotate(RotationUtils.getYaw(placePos), RotationUtils.getPitch(placePos), priority, () -> mc.interactionManager.interactBlock(mc.player, mc.world, hand, blockHitResult));
            else
                mc.interactionManager.interactBlock(mc.player, mc.world, hand, blockHitResult);
        }

        if (swingHand)
            mc.player.swingHand(hand);
        else
            mc.getNetworkHandler().sendPacket(new HandSwingC2SPacket(hand));
    }
}
