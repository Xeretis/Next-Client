package me.lor3mipsum.next.client.impl.modules.combat;

import com.google.common.collect.Streams;
import me.lor3mipsum.next.client.event.EventTarget;
import me.lor3mipsum.next.client.impl.events.TickEvent;
import me.lor3mipsum.next.client.impl.events.WorldRenderEvent;
import me.lor3mipsum.next.client.impl.settings.BooleanSetting;
import me.lor3mipsum.next.client.impl.settings.KeybindSetting;
import me.lor3mipsum.next.client.impl.settings.NumberSetting;
import me.lor3mipsum.next.client.module.Category;
import me.lor3mipsum.next.client.social.SocialManager;
import me.lor3mipsum.next.client.utils.player.DamageUtils;
import me.lor3mipsum.next.client.utils.player.InventoryUtils;
import me.lor3mipsum.next.client.utils.player.PlayerUtils;
import me.lor3mipsum.next.client.utils.player.Rotations;
import me.lor3mipsum.next.client.utils.render.RenderUtils;
import me.lor3mipsum.next.client.utils.render.color.QuadColor;
import me.lor3mipsum.next.client.utils.world.WorldUtils;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.AmbientEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.WaterCreatureEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.SnowGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolItem;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;
import me.lor3mipsum.next.client.module.Module;

import java.util.*;
import java.util.stream.Collectors;

public class CrystalAura extends Module{

    public BooleanSetting place = new BooleanSetting("Place", true);
    public BooleanSetting breaks = new BooleanSetting("Break", true);
    public BooleanSetting keepPos = new BooleanSetting("KeepPos", false);
    //public NumberSetting breakAttempts = new NumberSetting("BreakAttempts", 2, 1, 6, 1);

    public BooleanSetting players = new BooleanSetting("Players", true);
    public BooleanSetting friends = new BooleanSetting("Friends", false);
    public BooleanSetting hostiles = new BooleanSetting("Hostiles", false);
    public BooleanSetting animals = new BooleanSetting("Animals", false);

    public NumberSetting hitRange = new NumberSetting("HitRange", 5.2, 1, 6, 0.1);
    public NumberSetting placeRange = new NumberSetting("PlaceRange", 5.2, 1, 6, 0.1);
    public NumberSetting wallRange = new NumberSetting("WallRange", 3, 1, 6, 0.1);
    public BooleanSetting raytrace = new BooleanSetting("RayTrace", false);
    public BooleanSetting antiWeakness = new BooleanSetting("AntiWeakness", true);

    public NumberSetting placeDelay = new NumberSetting("PlaceDelay", 0, 0, 10, 1);
    public NumberSetting breakDelay = new NumberSetting("BreakDelay", 2, 0, 10, 1);

    public NumberSetting minDmg = new NumberSetting("MinDmg", 8, 0, 36, 1);
    public NumberSetting safetyRatio = new NumberSetting("SafetyRatio", 2, 0.5, 6, 0.1);
    public NumberSetting maxSelfDmg = new NumberSetting("MaxSelfDmg", 6, 0, 36, 1);

    public BooleanSetting rotate = new BooleanSetting("Rotate", true);

    public BooleanSetting autoSwitch = new BooleanSetting("AutoSwitch", true);
    public BooleanSetting switchBack = new BooleanSetting("SwitchBack", true);
    //public BooleanSetting antiSuicide = new BooleanSetting("AntiSuicide", true);

    public BooleanSetting fastMode = new BooleanSetting("FastMode", true);

    public BooleanSetting newPlace = new BooleanSetting("NewPlace", true);

    //public BooleanSetting facePlace = new BooleanSetting("FacePlace", false);
    //public NumberSetting facePlaceHealth = new NumberSetting("FacePlaceHp", 8, 0, 36, 1);

    //public BooleanSetting armorBreaker = new BooleanSetting("ArmorBreaker", true);
    //public NumberSetting armorPct = new NumberSetting("ArmorPct", 10, 0, 100, 1);

    public BooleanSetting stopWhileMining = new BooleanSetting("StopWhileMining", false);
    public BooleanSetting stopWhileEating = new BooleanSetting("StopWhileEating", false);

    public KeybindSetting keybind = new KeybindSetting(GLFW.GLFW_KEY_UNKNOWN);

    private BlockPos render = null;
    private int oldSlot = -1;
    private int breakCooldown = 0;
    private int placeCooldown = 0;

    public CrystalAura() {
        super("CrystalAura", "Yes", Category.COMBAT);
    }

    @Override
    public void onDisable() {
        if (switchBack.isOn() && oldSlot != -1) mc.player.inventory.selectedSlot = oldSlot;
        render = null;
    }

    @EventTarget
    private void onTick(TickEvent.Post event) {
        breakCooldown = Math.max(0, breakCooldown - 1);
        placeCooldown = Math.max(0, placeCooldown - 1);

        if((mc.player.isUsingItem() && (mc.player.getMainHandStack().getItem().isFood() || mc.player.getOffHandStack().getItem().isFood()) && stopWhileEating.isOn()
        || (mc.interactionManager.isBreakingBlock() && stopWhileMining.isOn())))
            return;

        //Break
        List<EndCrystalEntity> nearestCrystals = Streams.stream(mc.world.getEntities())
                .filter(e -> (e instanceof EndCrystalEntity))
                .map(e -> (EndCrystalEntity) e)
                .sorted(Comparator.comparing(c -> mc.player.distanceTo(c)))
                .collect(Collectors.toList());

        if (this.breaks.isOn() && !nearestCrystals.isEmpty() && breakCooldown <= 0) {
            if (antiWeakness.isOn() && mc.player.hasStatusEffect(StatusEffects.WEAKNESS)) {
                doWeaknessSwitch();
            }

            boolean end = false;
            for (EndCrystalEntity c: nearestCrystals) {
                if (mc.player.distanceTo(c) > hitRange.getNumber()
                        || DamageUtils.willExplosionKill(c.getPos(), 6f, mc.player)
                        || (mc.player.getHealth() + mc.player.getAbsorptionAmount()) - DamageUtils.getExplosionDamage(c.getPos(), 6f, mc.player)
                        < maxSelfDmg.getNumber()
                        || mc.world.getOtherEntities(null,
                        new Box(c.getPos(), c.getPos()).expand(7),
                        e -> e instanceof LivingEntity && e != mc.player && e != mc.player.getVehicle()).isEmpty()) {
                    continue;
                }

                if (rotate.isOn()) {
                    float[] rotation = PlayerUtils.calculateAngle(c.getPos());
                    Rotations.INSTANCE.rotate(rotation[0], rotation[1], 30, () -> mc.interactionManager.attackEntity(mc.player, c));
                } else {
                    mc.interactionManager.attackEntity(mc.player, c);
                }
                mc.player.swingHand(Hand.MAIN_HAND);

                end = true;
                break;
            }

            breakCooldown = (int) this.breakDelay.getNumber() + 1;

            if (!fastMode.isOn() && end) {
                return;
            }
        }

        //Place
        if (place.isOn() && placeCooldown <= 0) {
            if (autoSwitch.isOn())
                doSwitch();

            Hand hand;

            if(mc.player.getOffHandStack().getItem() == Items.END_CRYSTAL)
                hand = Hand.OFF_HAND;
            else if (mc.player.getMainHandStack().getItem() == Items.END_CRYSTAL)
                hand = Hand.MAIN_HAND;
            else
                return;

            List<LivingEntity> targets = Streams.stream(mc.world.getEntities())
                    .filter(e -> !(e instanceof PlayerEntity && (friends.isOn() && SocialManager.isFriend(e.getName().getString())))
                            && e.isAlive()
                            && !e.getEntityName().equals(mc.getSession().getUsername())
                            && e != mc.player.getVehicle())
                    .filter(e -> (e instanceof PlayerEntity && players.isOn())
                            || (e instanceof MobEntity && hostiles.isOn())
                            || ((e instanceof AmbientEntity || e instanceof WaterCreatureEntity || e instanceof IronGolemEntity || e instanceof SnowGolemEntity || e instanceof PassiveEntity) && animals.isOn()))
                    .map(e -> (LivingEntity) e)
                    .collect(Collectors.toList());

            Map<BlockPos, Float> placeBlocks = new LinkedHashMap<>();

            for (Vec3d v: getCrystalPoses()) {
                float playerDmg = DamageUtils.getExplosionDamage(v, 6f, mc.player);

                if (DamageUtils.willExplosionKill(v, 6f, mc.player)) {
                    continue;
                }

                for (LivingEntity e: targets) {
                    float targetDmg = DamageUtils.getExplosionDamage(v, 6f, e);

                    if (targetDmg >= minDmg.getNumber()) {
                        float ratio = playerDmg == 0 ? targetDmg : targetDmg / playerDmg;

                        if (ratio > safetyRatio.getNumber()) {
                            placeBlocks.put(new BlockPos(v).down(), ratio);
                        }
                    }
                }
            }

            placeBlocks = placeBlocks.entrySet().stream()
                    .sorted((b1, b2) -> Float.compare(b2.getValue(), b1.getValue()))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (x, y) -> y, LinkedHashMap::new));

            for (Map.Entry<BlockPos, Float> e: placeBlocks.entrySet()) {
                BlockPos block = e.getKey();

                Vec3d eyeVec = new Vec3d(mc.player.getX(), mc.player.getEyeY(), mc.player.getZ());

                Vec3d vec = new Vec3d(block.getX(), block.getY() + 0.5, block.getZ());
                Direction dir = Direction.UP;
                for (Direction d: Direction.values()) {
                    Vec3d vd = WorldUtils.getLegitLookPos(block, d, true, 5);
                    if (vd != null && eyeVec.distanceTo(vd) <= eyeVec.distanceTo(vec)) {
                        vec = vd;
                    }
                }

                Vec3d finalVec = vec;

                if (rotate.isOn()) {
                    float[] rotation = PlayerUtils.calculateAngle(finalVec.add(0.5, 1.0, 0.5));
                    Rotations.INSTANCE.rotate(rotation[0], rotation[1], 25, () -> mc.interactionManager.interactBlock(mc.player, mc.world, hand, new BlockHitResult(finalVec, dir, block, false)));
                } else {
                    mc.interactionManager.interactBlock(mc.player, mc.world, hand, new BlockHitResult(finalVec, dir, block, false));
                }

                render = block;

                break;
            }

            placeCooldown = (int) placeDelay.getNumber() + 1;

        }
    }

    @EventTarget
    public void onRender(WorldRenderEvent.Post event) {
        RenderUtils.drawBoxBoth(render, QuadColor.single(255, 255, 255, 100), 2.5f);
    }

    public Set<Vec3d> getCrystalPoses() {
        Set<Vec3d> poses = new HashSet<>();

        int range = (int) Math.floor(placeRange.getNumber());
        for (int x = -range; x <= range; x++) {
            for (int y = -range; y <= range; y++) {
                for (int z = -range; z <= range; z++) {
                    BlockPos basePos = mc.player.getBlockPos().add(x, y, z);

                    if (!canPlace(basePos))
                        continue;


                    boolean throughWalls = true;
                    for (Direction d: Direction.values()) {
                        if (WorldUtils.getLegitLookPos(basePos, d, true, 5) != null) {
                            throughWalls = false;
                            break;
                        }
                    }

                    if (throughWalls && raytrace.isOn()) {
                        continue;
                    }


                    Vec3d pos = Vec3d.of(basePos).add(0.5, 1, 0.5);

                    if (mc.player.getPos().distanceTo(pos) <= placeRange.getNumber() + 0.25)
                        if (!throughWalls)
                            poses.add(Vec3d.of(basePos).add(0.5, 1, 0.5));
                        else if (mc.player.getPos().distanceTo(pos) <= wallRange.getNumber())
                            poses.add(Vec3d.of(basePos).add(0.5, 1, 0.5));
                }
            }
        }

        return poses;
    }

    private void doWeaknessSwitch() {
        if (mc.player != null && !(mc.player.getMainHandStack().getItem() instanceof ToolItem && mc.player.getOffHandStack().getItem() instanceof SwordItem)) {
            int slot = InventoryUtils.findItemInHotbar(itemStack -> itemStack.getItem() instanceof ToolItem || itemStack.getItem() instanceof SwordItem);
            if (slot != -1 && slot < 9) {
                oldSlot = mc.player.inventory.selectedSlot;
                mc.player.inventory.selectedSlot = slot;
                mc.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(slot));
            }
        }
    }

    private void doSwitch() {
        if (mc.player != null && mc.player.getMainHandStack().getItem() != Items.END_CRYSTAL && mc.player.getOffHandStack().getItem() != Items.END_CRYSTAL) {
            int slot = InventoryUtils.findItemInHotbar(Items.END_CRYSTAL);
            if (slot != -1 && slot < 9) {
                oldSlot = mc.player.inventory.selectedSlot;
                mc.player.inventory.selectedSlot = slot;
                mc.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(slot));
            }
        }
    }

    private boolean canPlace(BlockPos basePos) {
        BlockState baseState = mc.world.getBlockState(basePos);

        if (baseState.getBlock() != Blocks.BEDROCK && baseState.getBlock() != Blocks.OBSIDIAN)
            return false;

        boolean oldPlace = !newPlace.isOn();
        BlockPos placePos = basePos.up();
        if (!mc.world.isAir(placePos) || (oldPlace && !mc.world.isAir(placePos.up())))
            return false;

        if (keepPos.isOn()) {
            boolean onlyCrystal = true;
            for (Entity e : mc.world.getOtherEntities(null, new Box(placePos).stretch(0, oldPlace ? 2 : 1, 0)))
                if (!(e instanceof EndCrystalEntity))
                    onlyCrystal = false;
            return onlyCrystal;
        } else
            return mc.world.getOtherEntities(null, new Box(placePos).stretch(0, oldPlace ? 2 : 1, 0)).isEmpty();

    }

    @Override
    public int getKeybind() {
        return keybind.getKey();
    }

    @Override
    public void setKeybind(int keybind) {
        this.keybind.setKey(keybind);
    }

}
