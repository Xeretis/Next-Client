package me.lor3mipsum.next.api.util.entity;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.*;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import net.minecraft.world.RaycastContext;

import java.util.UUID;

public class EntityUtils {
    private static final MinecraftClient mc = MinecraftClient.getInstance();

    public static boolean isHostile(LivingEntity entity) {
        return entity instanceof Monster;
    }

    public static boolean isAnimal(LivingEntity entity) {
        return (entity instanceof AmbientEntity || entity instanceof WaterCreatureEntity || entity instanceof IronGolemEntity || entity instanceof SnowGolemEntity || entity instanceof PassiveEntity);
    }

    public boolean canBeBreed(AnimalEntity entity) {
        return !entity.isBaby() && entity.canEat() && entity.isBreedingItem(mc.player.getMainHandStack());
    }

    public static boolean canBeAttacked(EntityType<?> type) {
        return type != EntityType.AREA_EFFECT_CLOUD && type != EntityType.ARROW && type != EntityType.FALLING_BLOCK && type != EntityType.FIREWORK_ROCKET && type != EntityType.ITEM && type != EntityType.LLAMA_SPIT && type != EntityType.SPECTRAL_ARROW && type != EntityType.ENDER_PEARL && type != EntityType.EXPERIENCE_BOTTLE && type != EntityType.POTION && type != EntityType.TRIDENT && type != EntityType.LIGHTNING_BOLT && type != EntityType.FISHING_BOBBER && type != EntityType.EXPERIENCE_ORB && type != EntityType.EGG;
    }

    public boolean canSeeBlock(Entity entity, BlockPos blockPos) {
        Vec3d vec3d = new Vec3d(entity.getX(), entity.getEyeY(), entity.getZ());
        Vec3d vec3d2 = new Vec3d(blockPos.getX(), blockPos.getY() + 0.5f, blockPos.getZ());
        return mc.world.raycast(new RaycastContext(vec3d, vec3d2, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, entity)).getType() == HitResult.Type.MISS;
    }

    public boolean isAngryAtPlayer(Entity entity) {
        if (entity instanceof BeeEntity && (((BeeEntity) entity).getAngryAt() == mc.player.getUuid() || (((BeeEntity) entity).getAngryAt() == null && ((BeeEntity) entity).isAttacking())))
            return true;
        if (entity instanceof PiglinEntity && (((PiglinEntity) entity).isAngryAt(mc.player)))
            return true;
        if (entity instanceof ZombifiedPiglinEntity && ((ZombifiedPiglinEntity) entity).shouldAngerAt(mc.player))
            return true;
        if (entity instanceof PandaEntity && ((PandaEntity) entity).isAttacking())
            return true;
        if (entity instanceof PolarBearEntity && (((PolarBearEntity) entity).getAngryAt() == mc.player.getUuid() || (((PolarBearEntity) entity).getAngryAt() == null && ((PolarBearEntity) entity).isAttacking())))
            return true;
        if (entity instanceof EndermanEntity && (((EndermanEntity) entity).getAngryAt() == mc.player.getUuid() || (((EndermanEntity) entity).getAngryAt() == null && ((EndermanEntity) entity).isAngry())))
            return true;
        if (entity instanceof IronGolemEntity && (((IronGolemEntity) entity).getAngryAt() == mc.player.getUuid() || (((IronGolemEntity) entity).getAngryAt() == null && ((IronGolemEntity) entity).isAttacking())))
            return true;
        if (entity instanceof WolfEntity && ((WolfEntity) entity).isAttacking() && !doesPlayerOwn(entity))
            return true;
        return false;
    }

    public boolean doesPlayerOwn(Entity entity) {
        return doesPlayerOwn(entity, mc.player);
    }

    public boolean doesPlayerOwn(Entity entity, PlayerEntity playerEntity) {
        if (entity instanceof LivingEntity)
            return getOwnerUUID((LivingEntity)entity) != null && getOwnerUUID((LivingEntity)entity).toString().equals(playerEntity.getUuid().toString());
        return false;
    }

    public UUID getOwnerUUID(LivingEntity livingEntity) {
        if (livingEntity instanceof TameableEntity) {
            TameableEntity tameableEntity = (TameableEntity) livingEntity;
            if (tameableEntity.isTamed()) {
                return tameableEntity.getOwnerUuid();
            }
        }
        if (livingEntity instanceof HorseBaseEntity) {
            HorseBaseEntity horseBaseEntity = (HorseBaseEntity) livingEntity;
            return horseBaseEntity.getOwnerUuid();
        }
        return null;
    }

    public static boolean isInRenderDistance(Entity entity) {
        if (entity == null) return false;
        return isInRenderDistance(entity.getX(), entity.getZ());
    }

    public static boolean isInRenderDistance(BlockEntity entity) {
        if (entity == null) return false;
        return isInRenderDistance(entity.getPos().getX(), entity.getPos().getZ());
    }

    public static boolean isInRenderDistance(double posX, double posZ) {
        double x = Math.abs(mc.gameRenderer.getCamera().getPos().x - posX);
        double z = Math.abs(mc.gameRenderer.getCamera().getPos().z - posZ);
        double d = (mc.options.viewDistance + 1) * 16;

        return x < d && z < d;
    }

    public static int getPing(PlayerEntity player) {
        if (mc.getNetworkHandler() == null) return 0;

        PlayerListEntry playerListEntry = mc.getNetworkHandler().getPlayerListEntry(player.getUuid());
        if (playerListEntry == null) return 0;
        return playerListEntry.getLatency();
    }

    public static GameMode getGameMode(PlayerEntity player) {
        PlayerListEntry playerListEntry = mc.getNetworkHandler().getPlayerListEntry(player.getUuid());
        if (playerListEntry == null) return null;
        return playerListEntry.getGameMode();
    }
}
