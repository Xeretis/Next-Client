package me.lor3mipsum.next.api.util.player;

import com.google.common.collect.Sets;
import me.lor3mipsum.next.api.util.misc.IVec3d;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Set;

public class PlaceUtils {

    private static final MinecraftClient mc = MinecraftClient.getInstance();

    public static final Set<Block> RIGHTCLICKABLE_BLOCKS = Sets.newHashSet(
            Blocks.CHEST, Blocks.TRAPPED_CHEST, Blocks.ENDER_CHEST,
            Blocks.WHITE_SHULKER_BOX, Blocks.ORANGE_SHULKER_BOX, Blocks.MAGENTA_SHULKER_BOX,
            Blocks.LIGHT_BLUE_SHULKER_BOX, Blocks.YELLOW_SHULKER_BOX, Blocks.LIME_SHULKER_BOX,
            Blocks.PINK_SHULKER_BOX, Blocks.GRAY_SHULKER_BOX, Blocks.LIGHT_GRAY_SHULKER_BOX,
            Blocks.CYAN_SHULKER_BOX, Blocks.PURPLE_SHULKER_BOX, Blocks.BLUE_SHULKER_BOX,
            Blocks.BROWN_SHULKER_BOX, Blocks.GREEN_SHULKER_BOX, Blocks.RED_SHULKER_BOX,
            Blocks.BLACK_SHULKER_BOX, Blocks.ANVIL, Blocks.BELL,
            Blocks.OAK_BUTTON, Blocks.ACACIA_BUTTON, Blocks.BIRCH_BUTTON, Blocks.DARK_OAK_BUTTON,
            Blocks.JUNGLE_BUTTON, Blocks.SPRUCE_BUTTON, Blocks.STONE_BUTTON, Blocks.COMPARATOR,
            Blocks.REPEATER, Blocks.OAK_FENCE_GATE, Blocks.SPRUCE_FENCE_GATE, Blocks.BIRCH_FENCE_GATE,
            Blocks.JUNGLE_FENCE_GATE, Blocks.DARK_OAK_FENCE_GATE, Blocks.ACACIA_FENCE_GATE,
            Blocks.BREWING_STAND, Blocks.DISPENSER, Blocks.DROPPER,
            Blocks.LEVER, Blocks.NOTE_BLOCK, Blocks.JUKEBOX,
            Blocks.BEACON, Blocks.BLACK_BED, Blocks.BLUE_BED, Blocks.BROWN_BED, Blocks.CYAN_BED, Blocks.GRAY_BED,
            Blocks.GREEN_BED, Blocks.LIGHT_BLUE_BED, Blocks.LIGHT_GRAY_BED, Blocks.LIME_BED, Blocks.MAGENTA_BED,
            Blocks.ORANGE_BED, Blocks.PINK_BED, Blocks.PURPLE_BED, Blocks.RED_BED, Blocks.WHITE_BED,
            Blocks.YELLOW_BED, Blocks.FURNACE, Blocks.OAK_DOOR, Blocks.SPRUCE_DOOR,
            Blocks.BIRCH_DOOR, Blocks.JUNGLE_DOOR, Blocks.ACACIA_DOOR,
            Blocks.DARK_OAK_DOOR, Blocks.CAKE, Blocks.ENCHANTING_TABLE,
            Blocks.DRAGON_EGG, Blocks.HOPPER, Blocks.REPEATING_COMMAND_BLOCK,
            Blocks.COMMAND_BLOCK, Blocks.CHAIN_COMMAND_BLOCK, Blocks.CRAFTING_TABLE,
            Blocks.ACACIA_TRAPDOOR, Blocks.BIRCH_TRAPDOOR, Blocks.DARK_OAK_TRAPDOOR,
            Blocks.JUNGLE_TRAPDOOR, Blocks.OAK_TRAPDOOR, Blocks.SPRUCE_TRAPDOOR,
            Blocks.CAKE, Blocks.ACACIA_SIGN, Blocks.ACACIA_WALL_SIGN,
            Blocks.BIRCH_SIGN, Blocks.BIRCH_WALL_SIGN, Blocks.DARK_OAK_SIGN,
            Blocks.DARK_OAK_WALL_SIGN, Blocks.JUNGLE_SIGN, Blocks.JUNGLE_WALL_SIGN,
            Blocks.OAK_SIGN, Blocks.OAK_WALL_SIGN, Blocks.SPRUCE_SIGN,
            Blocks.SPRUCE_WALL_SIGN, Blocks.CRIMSON_SIGN, Blocks.CRIMSON_WALL_SIGN,
            Blocks.WARPED_SIGN, Blocks.WARPED_WALL_SIGN, Blocks.BLAST_FURNACE, Blocks.SMOKER,
            Blocks.CARTOGRAPHY_TABLE, Blocks.GRINDSTONE, Blocks.LECTERN, Blocks.LOOM,
            Blocks.STONECUTTER, Blocks.SMITHING_TABLE);

    public static boolean placeBlock(BlockPos blockPos, Hand hand, boolean airPlace) {
        return placeBlock(blockPos, hand, airPlace, true, false, 0);
    }

    public static boolean placeBlock(BlockPos blockPos, Hand hand, boolean airPlace, boolean swingHand) {
        return placeBlock(blockPos, hand, airPlace, swingHand, false, 0);
    }

    public static boolean placeBlock(BlockPos blockPos, Hand hand, boolean airPlace, boolean swingHand, boolean rotate, int priority) {
        return placeBlock(blockPos, hand, airPlace, swingHand, true, rotate, priority);
    }

    public static boolean placeBlock(BlockPos blockPos, Hand hand, boolean airPlace, boolean swingHand, boolean checkEntities, boolean rotate, int priority) {

        Direction side = getPlaceSide(blockPos);
        BlockPos neighbour;
        Vec3d hitPos = new Vec3d(0, 0, 0);

        if (side == null && airPlace) {
            side = Direction.UP;
            neighbour = blockPos;
            ((IVec3d) hitPos).set(blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5);
        } else {
            if (side == null) return false;
            neighbour = blockPos.offset(side.getOpposite());
            ((IVec3d) hitPos).set(neighbour.getX() + 0.5 + side.getOffsetX() * 0.5, neighbour.getY() + 0.6 + side.getOffsetY() * 0.5, neighbour.getZ() + 0.5 + side.getOffsetZ() * 0.5);
        }

        if (!canPlace(blockPos, checkEntities, airPlace)) return false;

        if (RIGHTCLICKABLE_BLOCKS.contains(mc.world.getBlockState(neighbour).getBlock()))
            mc.player.networkHandler.sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.PRESS_SHIFT_KEY));

        if (rotate) {
            Direction finalSide = side;
            RotationUtils.INSTANCE.rotate(RotationUtils.getYaw(hitPos), RotationUtils.getPitch(hitPos), priority, () -> mc.interactionManager.interactBlock(mc.player, mc.world, hand, new BlockHitResult(hitPos, finalSide, neighbour, false)));
        } else
            mc.interactionManager.interactBlock(mc.player, mc.world, hand, new BlockHitResult(hitPos, side, neighbour, false));

        if (swingHand)
            mc.player.swingHand(hand);
        else
            mc.getNetworkHandler().sendPacket(new HandSwingC2SPacket(hand));

        return true;
    }

    private static Direction getPlaceSide(BlockPos blockPos) {
        for (Direction side : Direction.values()) {
            BlockPos neighbor = blockPos.offset(side);
            Direction side2 = side.getOpposite();

            BlockState state = mc.world.getBlockState(neighbor);

            if (state.isAir()) continue;

            if (!state.getFluidState().isEmpty()) continue;

            return side2;
        }

        return null;
    }

    public static boolean canPlace(BlockPos blockPos, boolean checkEntities,  boolean airplace) {
        if (blockPos == null) return false;

        if (World.isOutOfBuildLimitVertically(blockPos)) return false;

        if (!mc.world.getBlockState(blockPos).getMaterial().isReplaceable()) return false;

        if (checkEntities && !mc.world.canPlace(Blocks.STONE.getDefaultState(), blockPos, ShapeContext.absent())) return false;

        if (!airplace) {
            for (Direction d : Direction.values()) {
                if (mc.world.getBlockState(blockPos.offset(d)).getMaterial().isReplaceable())
                    continue;
                return true;
            }
            return false;
        }

        return true;
    }
}
