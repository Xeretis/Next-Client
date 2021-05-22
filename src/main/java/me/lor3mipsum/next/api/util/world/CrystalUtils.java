package me.lor3mipsum.next.api.util.world;

import com.mojang.authlib.GameProfile;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
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

    public static boolean canSeePos(BlockPos pos) {
        return mc.world.raycast(new RaycastContext(new Vec3d(mc.player.getX(), mc.player.getEyeY(), mc.player.getZ()), Vec3d.of(pos), RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, mc.player)).getType() == HitResult.Type.MISS;
    }

    public static Vec3d getCrystalPos(BlockPos blockPos) {
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

    public static boolean getArmorBreaker(PlayerEntity player, int percent) {
        for (ItemStack stack : player.getArmorItems()) {
            if (stack == null || stack.getItem() == Items.AIR) return true;

            int armorPercent = ((stack.getMaxDamage() - stack.getDamage()) /
                    stack.getMaxDamage()) * 100;

            if (percent >= armorPercent) return true;
        }
        return false;
    }

    private static Vec3d getPredictedPosition(LivingEntity entity, double ticks, PredictMode pMode) {

        if (ticks == 0) return entity.getPos();

        double motionX = entity.getX() - entity.prevX;
        double motionY = entity.getY() - entity.prevY;
        double motionZ = entity.getZ() - entity.prevZ;

        double motion = Math.sqrt(motionX * motionX + motionZ * motionZ + motionY * motionY);

        if (motion < 0.1) {
            return entity.getPos();
        }

        Vec3d predictedPos = entity.getPos();

        if (pMode == PredictMode.Strafe) {
            boolean shouldStrafe = motion > 0.31;

            for (int i = 0; i < ticks; i++) {
                if (entity.isOnGround()) {
                    motionY = shouldStrafe ? 0.4 : -0.07840015258789;
                }else {
                    motionY -= 0.08;
                    motionY *= 0.9800000190734863D;
                }
                predictedPos = predictedPos.add(motionX, motionY, motionZ);
            }
        } else {

            double distance = motion * ticks;

            double unitSlopeX = motionX / motion;
            double unitSlopeY = motionY / motion;
            double unitSlopeZ = motionZ / motion;

            double x = entity.prevX + unitSlopeX * distance;
            double y = entity.prevY + unitSlopeY * distance;
            double z = entity.prevZ + unitSlopeZ * distance;

            predictedPos = new Vec3d(x, y, z);
        }

        return predictedPos;
    }

    public static PlayerEntity getPredictedEntity(PlayerEntity currentTarget, int ticks, PredictMode mode) {
        OtherClientPlayerEntity entity = new OtherClientPlayerEntity(mc.world, new GameProfile(currentTarget.getUuid(), currentTarget.getName().getString()));

        Vec3d predictedPos = getPredictedPosition(currentTarget, ticks, mode);

        entity.copyPositionAndRotation(currentTarget);

        entity.setPos(predictedPos.x, predictedPos.y, predictedPos.z);

        entity.setHealth(currentTarget.getHealth());
        entity.setAbsorptionAmount(currentTarget.getAbsorptionAmount());
        entity.inventory.clone(currentTarget.inventory);

        return entity;
    }

    public enum PredictMode {
        Strafe,
        Line
    }
}
