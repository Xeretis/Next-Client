package me.lor3mipsum.next.client.utils.player;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.DamageUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.explosion.Explosion;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CrystalUtil {

    private static MinecraftClient mc = MinecraftClient.getInstance();

    public static ArrayList<EndCrystalEntity> getCrystals(double distance) {
        ArrayList<EndCrystalEntity> list = new ArrayList<>();

        for (Entity entity : mc.world.getEntities()) {
            if (entity instanceof EndCrystalEntity) {
                if (entity.distanceTo(mc.player) <= distance) {
                    list.add((EndCrystalEntity) entity);
                }
            }
        }

        return list;
    }

    public static EndCrystalEntity getCrystalInPos(BlockPos pos) {
        for (Entity entity : mc.world.getOtherEntities(null, new Box(pos.add(0, 1, 0)))) {
            if (entity instanceof EndCrystalEntity && entity.isAlive()) {
                return (EndCrystalEntity) entity;
            }
        }

        return null;
    }

    public static float calculateDamage(Vec3d explosionPos, float power, LivingEntity target) {
        if (mc.world.getDifficulty() == Difficulty.PEACEFUL)
            return 0f;

        if (target.getPos().distanceTo(new Vec3d(explosionPos.x, explosionPos.y, explosionPos.z)) > 12) {
            return 0;
        }

        Explosion explosion = new Explosion(mc.world, null, explosionPos.x, explosionPos.y, explosionPos.z, power, false, Explosion.DestructionType.DESTROY);

        double maxDist = power * 2;
        if (!mc.world.getOtherEntities(null, new Box(
                MathHelper.floor(explosionPos.x - maxDist - 1.0),
                MathHelper.floor(explosionPos.y - maxDist - 1.0),
                MathHelper.floor(explosionPos.z - maxDist - 1.0),
                MathHelper.floor(explosionPos.x + maxDist + 1.0),
                MathHelper.floor(explosionPos.y + maxDist + 1.0),
                MathHelper.floor(explosionPos.z + maxDist + 1.0))).contains(target)) {
            return 0f;
        }

        if (!target.isImmuneToExplosion() && !target.isInvulnerable()) {
            double distExposure = MathHelper.sqrt(target.squaredDistanceTo(explosionPos)) / maxDist;
            if (distExposure <= 1.0) {
                double xDiff = target.getX() - explosionPos.x;
                double yDiff = target.getEyeY() - explosionPos.y;
                double zDiff = target.getZ() - explosionPos.z;
                double diff = MathHelper.sqrt(xDiff * xDiff + yDiff * yDiff + zDiff * zDiff);
                if (diff != 0.0) {
                    double exposure = Explosion.getExposure(explosionPos, target);
                    double finalExposure = (1.0 - distExposure) * exposure;

                    float toDamage = (float) Math.floor((finalExposure * finalExposure + finalExposure) / 2.0 * 7.0 * maxDist + 1.0);

                    if (target instanceof PlayerEntity) {
                        if (mc.world.getDifficulty() == Difficulty.EASY) {
                            toDamage = Math.min(toDamage / 2f + 1f, toDamage);
                        } else if (mc.world.getDifficulty() == Difficulty.HARD) {
                            toDamage = toDamage * 3f / 2f;
                        }
                    }

                    // Armor
                    toDamage = DamageUtil.getDamageLeft(toDamage, target.getArmor(),
                            (float) target.getAttributeInstance(EntityAttributes.GENERIC_ARMOR_TOUGHNESS).getValue());

                    // Enchantments
                    if (target.hasStatusEffect(StatusEffects.RESISTANCE)) {
                        int resistance = 25 - (target.getStatusEffect(StatusEffects.RESISTANCE).getAmplifier() + 1) * 5;
                        float resistance_1 = toDamage * resistance;
                        toDamage = Math.max(resistance_1 / 25f, 0f);
                    }

                    if (toDamage <= 0f) {
                        toDamage = 0f;
                    } else {
                        int protAmount = EnchantmentHelper.getProtectionAmount(target.getArmorItems(), explosion.getDamageSource());
                        if (protAmount > 0) {
                            toDamage = DamageUtil.getInflictedDamage(toDamage, protAmount);
                        }
                    }

                    return toDamage;
                }
            }
        }

        return 0f;
    }

    public static float calculateDamage(BlockPos pos, float power, LivingEntity entity) {
        return calculateDamage(new Vec3d(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5), power, entity);
    }

    public static boolean canPlaceCrystal(BlockPos pos) {
        BlockState baseState = mc.world.getBlockState(pos);

        if (baseState.getBlock() != Blocks.BEDROCK && baseState.getBlock() != Blocks.OBSIDIAN)
            return false;

        BlockPos placePos = pos.up();

        if (!mc.world.isAir(placePos))
            return false;

        ArrayList<Entity> entities = new ArrayList<>();

        entities.addAll(mc.world.getOtherEntities(null, new Box(pos.add(0, 1, 0))));
        for (Entity entity : entities) {
            if (entity.isAlive()) {
                return false;
            }
        }

        return true;
    }

    public static List<BlockPos> findCrystalBlocks(ClientPlayerEntity player, float range) {
        List<BlockPos> positions = new ArrayList<>();
        positions.addAll(getSphere(GetPlayerPosFloored(player), range, (int) range, false, true, 0)
                .stream().filter(CrystalUtil::canPlaceCrystal).collect(Collectors.toList()));
        return positions;
    }

    public static List<BlockPos> getSphere(BlockPos pos, float r, int h, boolean hollow, boolean sphere, int plusY) {
        List<BlockPos> circleblocks = new ArrayList<>();
        int cx = pos.getX();
        int cy = pos.getY();
        int cz = pos.getZ();
        for (int x = cx - (int) r; x <= cx + r; x++) {
            for (int z = cz - (int) r; z <= cz + r; z++) {
                for (int y = (sphere ? cy - (int) r : cy); y < (sphere ? cy + r : cy + h); y++) {
                    double dist = (cx - x) * (cx - x) + (cz - z) * (cz - z) + (sphere ? (cy - y) * (cy - y) : 0);
                    if (dist < r * r && !(hollow && dist < (r - 1) * (r - 1))) {
                        circleblocks.add(new BlockPos(x, y + plusY, z));
                    }
                }
            }
        }

        return circleblocks;
    }

    public static BlockPos GetPlayerPosFloored(ClientPlayerEntity player) {
        return new BlockPos(Math.floor(player.getX()), Math.floor(player.getY()), Math.floor(player.getZ()));
    }
}
