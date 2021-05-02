package me.lor3mipsum.next.client.utils.player;

import me.lor3mipsum.next.Next;
import me.lor3mipsum.next.client.event.EventManager;
import me.lor3mipsum.next.client.event.EventTarget;
import me.lor3mipsum.next.client.impl.events.EntityRemovedEvent;
import me.lor3mipsum.next.client.impl.events.SendMovementPacketsEvent;
import me.lor3mipsum.next.client.impl.events.SetBlockStateEvent;
import me.lor3mipsum.next.client.impl.events.WorldRenderEvent;
import me.lor3mipsum.next.client.impl.modules.combat.CrystalAura;
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
import java.util.concurrent.CopyOnWriteArrayList;

public class CrystalUtils {
    private static MinecraftClient mc = MinecraftClient.getInstance();

    public List<BlockPos> obbyRock = new CopyOnWriteArrayList<>();
    public List<BlockPos> crystalBlocks = new CopyOnWriteArrayList<>();

    public static CrystalUtils INSTANCE = new CrystalUtils();

    public CrystalUtils() {
        EventManager.register(this);
    }

    public static boolean canPlaceCrystalAt(BlockPos blockpos)
    {
        BlockState baseState = mc.world.getBlockState(blockpos);

        if (baseState.getBlock() != Blocks.BEDROCK && baseState.getBlock() != Blocks.OBSIDIAN)
            return false;

        BlockPos placePos = blockpos.up();

        boolean oldPlace = Next.INSTANCE.moduleManager.getModule(CrystalAura.class).oldPlace.isOn();

        if (!mc.world.isAir(placePos) || (oldPlace && !mc.world.isAir(placePos.up())))
            return false;

        ArrayList<Entity> entities = new ArrayList<>();

        entities.addAll(mc.world.getOtherEntities(null, new Box(blockpos).stretch(0, oldPlace ? 2 : 1, 0)));
        for (Entity entity : entities) {
            if (entity.isAlive()) {
                return false;
            }
        }

        return true;
    }

    @EventTarget
    public void onSetBlockState(SetBlockStateEvent event)
    {
        if (event.newState.getBlock() == Blocks.OBSIDIAN || event.newState.getBlock() == Blocks.BEDROCK)
        {
            if (!obbyRock.contains(event.pos))
                obbyRock.add(event.pos);
        }
        else
        {
            obbyRock.remove(event.pos);
            if (crystalBlocks.contains(event.pos))
                crystalBlocks.remove(event.pos);
        }
    }

    @EventTarget
    public void loadWorld(WorldRenderEvent.Post event)
    {
        crystalBlocks.clear();
    }

    @EventTarget
    public void onUpdate(SendMovementPacketsEvent.Pre event)
    {
        if (mc.player == null)
            return;

        if (Next.INSTANCE.moduleManager.getModule(CrystalAura.class) == null || !Next.INSTANCE.moduleManager.getModule(CrystalAura.class).isOn())
            return;

        for (BlockPos pos : obbyRock)
        {
            BlockState state = mc.world.getBlockState(pos);

            boolean alreadyContains = crystalBlocks.contains(pos);

            if (state.getBlock() != Blocks.OBSIDIAN && state.getBlock() != Blocks.BEDROCK)
            {
                crystalBlocks.remove(pos);
                obbyRock.remove(pos);
                continue;
            }

            float dist = (float) mc.player.getPos().distanceTo(new Vec3d(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5));
            if (dist > Next.INSTANCE.moduleManager.getModule(CrystalAura.class).placeRange.getNumber())
            {
                if (alreadyContains)
                    crystalBlocks.remove(pos);
                obbyRock.remove(pos);
                continue;
            }

            if (!alreadyContains && canPlaceCrystalAt(pos))
            {
                if (Next.INSTANCE.moduleManager.getModule(CrystalAura.class).VerifyCrystalBlocks(pos))
                    crystalBlocks.add(pos);
                continue;
            }

            if (alreadyContains && (!canPlaceCrystalAt(pos) || !Next.INSTANCE.moduleManager.getModule(CrystalAura.class).VerifyCrystalBlocks(pos)))
                crystalBlocks.remove(pos);
        }

        int flooredRadius = MathHelper.floor(Next.INSTANCE.moduleManager.getModule(CrystalAura.class).placeRange.getNumber()) + 1;
        BlockPos playerPosFloored = GetPlayerPosFloored(mc.player);

        for (int x = playerPosFloored.getX() - flooredRadius; x <= playerPosFloored.getX() + flooredRadius; ++x)
            for (int y = playerPosFloored.getY() - flooredRadius; y <= playerPosFloored.getY() + flooredRadius; ++y)
                for (int z = playerPosFloored.getZ() - flooredRadius; z <= playerPosFloored.getZ() + flooredRadius; ++z)
                {
                    BlockPos pos = new BlockPos(x, y, z);

                    if (obbyRock.contains(pos))
                        continue;

                    float dist = (float) mc.player.getPos().distanceTo(new Vec3d(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5));
                    if (dist > Next.INSTANCE.moduleManager.getModule(CrystalAura.class).placeRange.getNumber())
                        continue;

                    BlockState state = mc.world.getBlockState(pos);

                    if (state.getBlock() == Blocks.OBSIDIAN || state.getBlock() == Blocks.BEDROCK)
                        obbyRock.add(pos);
                }
    }

    @EventTarget
    public void onEntityRemoved(EntityRemovedEvent event)
    {
        if (event.getEntity() instanceof EndCrystalEntity && mc.player.distanceTo(event.getEntity()) <= Next.INSTANCE.moduleManager.getModule(CrystalAura.class).placeRange.getNumber())
        {
            final BlockPos pos = event.getEntity().getBlockPos().down();
            BlockState state = mc.world.getBlockState(pos);

            if (state.getBlock() == Blocks.OBSIDIAN || state.getBlock() == Blocks.BEDROCK)
                crystalBlocks.add(pos);
        }
    }

    public BlockPos GetPlayerPosFloored(ClientPlayerEntity player) {
        if (player == null)
            return BlockPos.ORIGIN;

        return new BlockPos(Math.floor(player.getX()), Math.floor(player.getY()), Math.floor(player.getZ()));
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
}
