package me.lor3mipsum.next.api.util.entity;

import com.mojang.authlib.GameProfile;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.OtherClientPlayerEntity;
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
        return entity instanceof WolfEntity && ((WolfEntity) entity).isAttacking() && !doesPlayerOwn(entity);
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
