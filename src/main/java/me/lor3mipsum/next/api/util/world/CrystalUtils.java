package me.lor3mipsum.next.api.util.world;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CrystalUtils {

    public static final MinecraftClient mc = MinecraftClient.getInstance();

    public static boolean canPlaceCrystalAt(BlockPos blockpos, boolean oldPlace, boolean ignoreCrystals)
    {
        BlockState baseState = mc.world.getBlockState(blockpos);

        if (baseState.getBlock() != Blocks.BEDROCK && baseState.getBlock() != Blocks.OBSIDIAN)
            return false;

        BlockPos placePos = blockpos.up();

        if (!mc.world.isAir(placePos) || (oldPlace && !mc.world.isAir(placePos.up())))
            return false;

        if (!ignoreCrystals) {
            return mc.world.getOtherEntities(null, new Box(blockpos).stretch(0, oldPlace ? 2 : 1, 0)).isEmpty();
        }

        for (Entity entity : mc.world.getOtherEntities(null, new Box(blockpos).stretch(0, oldPlace ? 2 : 1, 0))) {
            if (entity instanceof EndCrystalEntity)
                continue;
            return false;
        }

        return true;
    }

    public static Vec3d getCrystalPos(BlockPos blockPos) {
        return Vec3d.of(blockPos).add(0.5, 0.5, 0.5);
    }

    public static List<BlockPos> getPlacePositions(double placeRange, boolean oldPlace, boolean ignoreCrystals) {
        return getPlaceArea(mc.player.getBlockPos(), placeRange, (int) placeRange, false, true, 0).stream().filter(pos -> canPlaceCrystalAt(pos, oldPlace, ignoreCrystals)).collect(Collectors.toList());
    }

    private static List<BlockPos> getPlaceArea(BlockPos pos, double range, int height, boolean hollow, boolean sphere, int plus_y) {
        ArrayList<BlockPos> areaBlocks = new ArrayList<>();

        int playerX = pos.getX();
        int playerY = pos.getY();
        int playerZ = pos.getZ();

        int x = playerX - (int) range;
        while ((double) x <= (double) playerX + range) {

            int z = playerZ - (int) range;
            while ((double) z <= (double) playerZ + range) {

                int y = sphere ? playerY - (int) range : playerY;
                while (true) {

                    double f = sphere ? (double) playerY + range : (double) (playerY + height);

                    if (!(y < f)) break;

                    double dist = (playerX - x) * (playerX - x) + (playerZ - z) * (playerZ - z) + (sphere ? (playerY - y) * (playerY - y) : 0);
                    if (!(!(dist < (range * range)) || hollow && dist < ((range - 1.0f) * (range - 1.0f)))) {
                        BlockPos blockPos = new BlockPos(x, y + plus_y, z);
                        areaBlocks.add(blockPos);
                    }
                    ++y;
                }
                ++z;
            }
            ++x;
        }

        return areaBlocks;
    }

    public static boolean getLineRayTrace(BlockPos pos) {
        return mc.world.raycast(new RaycastContext(new Vec3d(mc.player.getX(), mc.player.getEyeY(), mc.player.getZ()), Vec3d.of(pos), RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, mc.player)).getType() == HitResult.Type.MISS;
    }

    public static boolean getFullRayTrace(BlockPos pos, Direction dir) {
        return getFullRayTrace(new Box(pos), dir, 0.01);
    }

    public static boolean getFullRayTrace(Box box, Direction dir, double extrude) {
        Vec3d eyePos = new Vec3d(mc.player.getX(), mc.player.getEyeY(), mc.player.getZ());
        Vec3d blockPos = new Vec3d(box.minX, box.minY, box.minZ).add(
                (dir == Direction.WEST ? -extrude : dir.getOffsetX() * box.getXLength() + extrude),
                (dir == Direction.DOWN ? -extrude : dir.getOffsetY() * box.getYLength() + extrude),
                (dir == Direction.NORTH ? -extrude : dir.getOffsetZ() * box.getZLength() + extrude));

        for (double i = 0; i <= 1; i += 1d / 5.0) {
            for (double j = 0; j <= 1; j += 1d / 5.0) {
                Vec3d lookPos = blockPos.add(
                        (dir.getAxis() == Direction.Axis.X ? 0 : i * box.getXLength()),
                        (dir.getAxis() == Direction.Axis.Y ? 0 : dir.getAxis() == Direction.Axis.Z ? j * box.getYLength() : i * box.getYLength()),
                        (dir.getAxis() == Direction.Axis.Z ? 0 : j * box.getZLength()));

                if (eyePos.distanceTo(lookPos) > 6)
                    continue;

                if (mc.world.raycast(new RaycastContext(eyePos, lookPos, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, mc.player)).getType() == HitResult.Type.MISS)
                    return true;
            }
        }

        return false;
    }

    public static boolean getArmorBreaker(PlayerEntity player, int percent) {
        for (ItemStack itemStack : player.getArmorItems()) {
            if (itemStack.isEmpty() || !itemStack.isDamageable()) continue;
            if (((itemStack.getDamage() / itemStack.getMaxDamage()) * 100) <= percent) return true;
        }
        return false;
    }
}
