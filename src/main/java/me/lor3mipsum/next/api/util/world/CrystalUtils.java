package me.lor3mipsum.next.api.util.world;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

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

    private Vec3d getCrystalPos(BlockPos blockPos) {
        return new Vec3d(blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5);
    }

    public static List<BlockPos> getPlacePositions(float placeRange, boolean oldPlace, boolean ignoreCrystals) {
        return getPlaceArea(mc.player.getBlockPos(), placeRange, (int) placeRange, false, true, 0).stream().filter(pos -> canPlaceCrystalAt(pos, oldPlace, ignoreCrystals)).collect(Collectors.toList());
    }

    public static List<BlockPos> getPlaceArea(BlockPos pos, float range, int height, boolean hollow, boolean sphere, int plus_y) {
        ArrayList<BlockPos> areaBlocks = new ArrayList<>();

        int playerX = pos.getX();
        int playerY = pos.getY();
        int playerZ = pos.getZ();

        int x = playerX - (int) range;
        while ((float) x <= (float) playerX + range) {

            int z = playerZ - (int) range;
            while ((float) z <= (float) playerZ + range) {

                int y = sphere ? playerY - (int) range : playerY;
                while (true) {

                    float f = y;
                    float f2 = sphere ? (float) playerY + range : (float) (playerY + height);

                    if (!(f < f2)) break;

                    double dist = (playerX - x) * (playerX - x) + (playerZ - z) * (playerZ - z) + (sphere ? (playerY - y) * (playerY - y) : 0);
                    if (!(!(dist < (double) (range * range)) || hollow && dist < (double) ((range - 1.0f) * (range - 1.0f)))) {
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
}
