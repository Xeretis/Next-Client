package me.lor3mipsum.next.client.utils.world;

import com.google.common.collect.Sets;
import me.lor3mipsum.next.client.utils.player.Rotations;
import me.lor3mipsum.next.mixininterface.IVec3d;
import net.minecraft.block.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.*;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

import java.util.Set;

public class WorldUtils {
    protected static final MinecraftClient mc = MinecraftClient.getInstance();

    private static final Vec3d hitPos = new Vec3d(0, 0, 0);

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

    public static final Set<Material> FLUIDS = Sets.newHashSet(
            Material.WATER, Material.LAVA, Material.UNDERWATER_PLANT, Material.REPLACEABLE_UNDERWATER_PLANT);

    public static boolean canPlace(BlockPos blockPos, boolean checkEntities,  boolean airplace) {
        if (blockPos == null) return false;

        // Check y level
        if (World.isOutOfBuildLimitVertically(blockPos)) return false;

        // Check if current block is replaceable
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

    public static boolean canPlace(BlockPos blockPos) {
        return canPlace(blockPos, true, true);
    }

    public static boolean doesBoxTouchBlock(Box box, Block block) {
        for (int x = (int) Math.floor(box.minX); x < Math.ceil(box.maxX); x++) {
            for (int y = (int) Math.floor(box.minY); y < Math.ceil(box.maxY); y++) {
                for (int z = (int) Math.floor(box.minZ); z < Math.ceil(box.maxZ); z++) {
                    if (mc.world.getBlockState(new BlockPos(x, y, z)).getBlock() == block) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public static boolean place(BlockPos blockPos, Hand hand, int slot, boolean rotate, int priority, boolean swing, boolean checkEntities, boolean swap, boolean swapBack, boolean canAirplace) {
        if (slot == -1 || !canPlace(blockPos, checkEntities, true)) return false;

        Direction side = getPlaceSide(blockPos);
        BlockPos neighbour;
        Vec3d hitPos = rotate ? new Vec3d(0, 0, 0) : WorldUtils.hitPos;

        if (side == null && canAirplace) {
            side = Direction.UP;
            neighbour = blockPos;
            ((IVec3d) hitPos).set(blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5);
        } else {
            if (side == null) return false;
            neighbour = blockPos.offset(side.getOpposite());
            // The Y is not 0.5 but 0.6 for allowing "antiAnchor" placement. This should not damage any other modules
            ((IVec3d) hitPos).set(neighbour.getX() + 0.5 + side.getOffsetX() * 0.5, neighbour.getY() + 0.6 + side.getOffsetY() * 0.5, neighbour.getZ() + 0.5 + side.getOffsetZ() * 0.5);
        }

        if (rotate) {
            Direction s = side;
            Rotations.INSTANCE.rotate(Rotations.INSTANCE.getYaw(hitPos), Rotations.INSTANCE.getPitch(hitPos), priority, () -> place(slot, hitPos, hand, s, neighbour, swing, swap, swapBack));
        } else place(slot, hitPos, hand, side, neighbour, swing, swap, swapBack);

        return true;
    }

    private static Direction getPlaceSide(BlockPos blockPos) {
        for (Direction side : Direction.values()) {
            BlockPos neighbor = blockPos.offset(side);
            Direction side2 = side.getOpposite();

            BlockState state = mc.world.getBlockState(neighbor);

            // Check if neighbour isn't empty
            if (state.isAir() || RIGHTCLICKABLE_BLOCKS.contains(state.getBlock())) continue;

            // Check if neighbour is a fluid
            if (!state.getFluidState().isEmpty()) continue;

            return side2;
        }

        return null;
    }

    public static boolean place(BlockPos blockPos, Hand hand, int slot, boolean rotate, int priority, boolean checkEntities, boolean canAirplace) {
        return place(blockPos, hand, slot, rotate, priority, true, checkEntities, true, true, canAirplace);
    }

    private static void place(int slot, Vec3d hitPos, Hand hand, Direction side, BlockPos neighbour, boolean swing, boolean swap, boolean swapBack) {
        int preSlot = mc.player.inventory.selectedSlot;
        if (swap) mc.player.inventory.selectedSlot = slot;

        boolean wasSneaking = mc.player.input.sneaking;
        mc.player.input.sneaking = false;

        mc.interactionManager.interactBlock(mc.player, mc.world, hand, new BlockHitResult(hitPos, side, neighbour, false));
        if (swing) mc.player.swingHand(hand);
        else mc.getNetworkHandler().sendPacket(new HandSwingC2SPacket(hand));

        mc.player.input.sneaking = wasSneaking;

        if (swapBack) mc.player.inventory.selectedSlot = preSlot;
    }

    public static Vec3d getLegitLookPos(BlockPos pos, Direction dir, boolean raycast, int res) {
        return getLegitLookPos(new Box(pos), dir, raycast, res, 0.01);
    }

    public static Vec3d getLegitLookPos(Box box, Direction dir, boolean raycast, int res, double extrude) {
        Vec3d eyePos = new Vec3d(mc.player.getX(), mc.player.getEyeY(), mc.player.getZ());
        Vec3d blockPos = new Vec3d(box.minX, box.minY, box.minZ).add(
                (dir == Direction.WEST ? -extrude : dir.getOffsetX() * box.getXLength() + extrude),
                (dir == Direction.DOWN ? -extrude : dir.getOffsetY() * box.getYLength() + extrude),
                (dir == Direction.NORTH ? -extrude : dir.getOffsetZ() * box.getZLength() + extrude));

        for (double i = 0; i <= 1; i += 1d / (double) res) {
            for (double j = 0; j <= 1; j += 1d / (double) res) {
                Vec3d lookPos = blockPos.add(
                        (dir.getAxis() == Direction.Axis.X ? 0 : i * box.getXLength()),
                        (dir.getAxis() == Direction.Axis.Y ? 0 : dir.getAxis() == Direction.Axis.Z ? j * box.getYLength() : i * box.getYLength()),
                        (dir.getAxis() == Direction.Axis.Z ? 0 : j * box.getZLength()));

                if (eyePos.distanceTo(lookPos) > 4.55)
                    continue;

                if (raycast) {
                    if (mc.world.raycast(new RaycastContext(eyePos, lookPos,
                            RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, mc.player)).getType() == HitResult.Type.MISS) {
                        return lookPos;
                    }
                } else {
                    return lookPos;
                }
            }
        }

        return null;
    }
}
