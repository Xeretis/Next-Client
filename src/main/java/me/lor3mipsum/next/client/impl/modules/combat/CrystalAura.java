package me.lor3mipsum.next.client.impl.modules.combat;

import me.lor3mipsum.next.Main;
import me.lor3mipsum.next.api.event.NextEvent;
import me.lor3mipsum.next.api.event.client.TickEvent;
import me.lor3mipsum.next.api.event.entity.EntityAddedEvent;
import me.lor3mipsum.next.api.event.entity.EntityRemovedEvent;
import me.lor3mipsum.next.api.event.network.PacketSentEvent;
import me.lor3mipsum.next.api.event.player.SendMovementPacketsEvent;
import me.lor3mipsum.next.api.event.world.PlaySoundEvent;
import me.lor3mipsum.next.api.event.world.WorldRenderEvent;
import me.lor3mipsum.next.api.util.entity.DamageUtils;
import me.lor3mipsum.next.api.util.entity.EntityUtils;
import me.lor3mipsum.next.api.util.misc.MathUtils;
import me.lor3mipsum.next.api.util.misc.NextColor;
import me.lor3mipsum.next.api.util.misc.Timer;
import me.lor3mipsum.next.api.util.player.InventoryUtils;
import me.lor3mipsum.next.api.util.player.RotationUtils;
import me.lor3mipsum.next.api.util.render.RenderUtils;
import me.lor3mipsum.next.api.util.render.color.QuadColor;
import me.lor3mipsum.next.api.util.world.CrystalUtils;
import me.lor3mipsum.next.client.core.module.Category;
import me.lor3mipsum.next.client.core.module.Module;
import me.lor3mipsum.next.client.core.module.annotation.Mod;
import me.lor3mipsum.next.client.core.setting.SettingSeparator;
import me.lor3mipsum.next.client.impl.settings.*;
import me.zero.alpine.event.EventPriority;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.item.ToolItem;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.*;

import java.util.ArrayList;
import java.util.List;

@Mod(name = "CrystalAura", description = "Crystals go brrr", category = Category.COMBAT)
public class CrystalAura extends Module {

    public SettingSeparator actionsSep = new SettingSeparator("Actions");

    public BooleanSetting cPlace = new BooleanSetting("Place", true);
    public BooleanSetting cBreak = new BooleanSetting("Break", true);

    public SettingSeparator generalSep = new SettingSeparator("General");

    public EnumSetting<CaLogic> logic = new EnumSetting<>("Logic", CaLogic.WorldTick);
    public EnumSetting<TargetingMode> targetingMode = new EnumSetting<>("Targeting", TargetingMode.All);
    public EnumSetting<RaytraceMode> raytraceMode = new EnumSetting<>("Raytrace Mode", RaytraceMode.Full);
    public BooleanSetting terrainIgnore = new BooleanSetting("Terrain Ignore", false);
    public BooleanSetting antiSuicide = new BooleanSetting("Anti Suicide", true);
    public BooleanSetting antiPop = new BooleanSetting("Anti Pop", false);

    public SettingSeparator placingSep = new SettingSeparator("Placing");

    public BooleanSetting fastPlace = new BooleanSetting("Fast Place", true);
    public BooleanSetting sameTick = new BooleanSetting("Same Tick Place", true);
    public BooleanSetting oldPlace = new BooleanSetting("Old Place", false);
    public BooleanSetting crystalCheck = new BooleanSetting("Crystal Check", false);

    public SettingSeparator breakingSep = new SettingSeparator("Breaking");

    public BooleanSetting fastBreak = new BooleanSetting("Fast Break", true);
    public EnumSetting<CancelMode> cancelMode = new EnumSetting<>("Cancel Mode", CancelMode.Instant);

    public SettingSeparator delaysSep = new SettingSeparator("Delays");

    public IntegerSetting breakDelay = new IntegerSetting("Break Delay", 2, 0, 20);
    public IntegerSetting placeDelay = new IntegerSetting("Place Delay", 2, 0, 20);

    public SettingSeparator rangesSep = new SettingSeparator("Ranges");

    public DoubleSetting breakRange = new DoubleSetting("Break Range", 5.0, 0.0, 6.0);
    public DoubleSetting placeRange = new DoubleSetting("Place Range", 5.0, 0.0, 6.0);
    public DoubleSetting wallsBreakRange = new DoubleSetting("Walls Break Range", 3.0, 0.0, 6.0);
    public DoubleSetting wallsPlaceRange = new DoubleSetting("Walls Place Range", 3.0, 0.0, 6.0);

    public SettingSeparator damagesSep = new SettingSeparator("Damages");

    public IntegerSetting minHpPlace = new IntegerSetting("Min Place Dmg", 8, 0, 36);
    public IntegerSetting minHpBreak = new IntegerSetting("Min Break Dmg", 8, 0, 36);
    public IntegerSetting maxSelfDamage = new IntegerSetting("Max Self Dmg", 5, 0, 36);
    public BooleanSetting ignoreSelfDamage = new BooleanSetting("Ignore Self Dmg", false);
    public IntegerSetting lethalMultiplier = new IntegerSetting("Lethal Multiplier", 1, 1, 5);
    public BooleanSetting useSafety = new BooleanSetting("Use Safety", true);
    public IntegerSetting safety = new IntegerSetting("Safety Pct", 26, 0, 100);

    public SettingSeparator rotationsSep = new SettingSeparator("Rotations");

    public BooleanSetting rotate = new BooleanSetting("Rotate", true);
    public EnumSetting<YawStepMode> yawStepMode = new EnumSetting<>("YawStep Mode", YawStepMode.None);
    public IntegerSetting yawSteps = new IntegerSetting("YawSteps", 30, 1, 180);
    public BooleanSetting raytrace = new BooleanSetting("Raytrace", false);
    public BooleanSetting resetRotate = new BooleanSetting("Reset Rotations", true);
    public IntegerSetting resetRotateDelay = new IntegerSetting("Reset Rotations Delay", 500, 500, 5000);

    public SettingSeparator predictionsSep = new SettingSeparator("Predictions");

    public EnumSetting<EntityUtils.PredictMode> predictMode = new EnumSetting<>("Predict Mode", EntityUtils.PredictMode.Line);
    public BooleanSetting targetPredict = new BooleanSetting("Target Predict", true);
    public BooleanSetting selfPredict = new BooleanSetting("Self Predict", false);
    public IntegerSetting targetPredictTicks = new IntegerSetting("Target Predict Ticks", 2, 0, 20);
    public IntegerSetting selfPredictTicks = new IntegerSetting("Self Predict Ticks", 2, 0, 20);

    public SettingSeparator switchingSep = new SettingSeparator("Switching");

    public BooleanSetting autoSwitch = new BooleanSetting("Auto Switch", true);
    public BooleanSetting switchBack = new BooleanSetting("Switch Back", true);
    public IntegerSetting switchBackDelay = new IntegerSetting("Switch Back Delay", 150, 50, 500);
    public BooleanSetting antiWeakness = new BooleanSetting("Anti Weakness", true);

    public SettingSeparator stoppingSep = new SettingSeparator("Stopping");

    public BooleanSetting stopWhileMining = new BooleanSetting("StopWhileMining", false);
    public BooleanSetting stopWhileEating = new BooleanSetting("StopWhileEating", false);

    public SettingSeparator facePlaceSep = new SettingSeparator("FacePlace");

    public BooleanSetting facePlace = new BooleanSetting("FacePlace", true);
    public IntegerSetting facePlaceHp = new IntegerSetting("FacePlace Hp", 10, 0, 36);

    public SettingSeparator armorBreakerSep = new SettingSeparator("ArmorBreaker");

    public BooleanSetting armorBreaker = new BooleanSetting("ArmorBreaker", true);
    public IntegerSetting armorBreakerPct = new IntegerSetting("ArmorBreaker Pct", 10, 1, 100);

    public SettingSeparator renderSep = new SettingSeparator("Render");

    public ColorSetting sidesColor = new ColorSetting("Sides Color", false, new NextColor(255, 255, 255));
    public ColorSetting linesColor = new ColorSetting("Lines Color", false, new NextColor(255, 255, 255));
    public DoubleSetting lineWidth = new DoubleSetting("Line Width", 2.5, 0.1, 5);
    public BooleanSetting swing = new BooleanSetting("Swing", true);

    public enum TargetingMode {
        Closest,
        All
    }

    public enum CaLogic {
        WorldTick,
        PlayerTick
    }

    public enum RaytraceMode {
        Full,
        Line
    }

    public enum CancelMode {
        Sound,
        Instant,
        None
    }

    public enum YawStepMode {
        Break,
        Place,
        All,
        None
    }

    private final List<Integer> attackedCrystals = new ArrayList<>();

    private PlayerEntity target;
    private BlockPos renderBlock;

    private int breakDelayCounter;
    private int placeDelayCounter;

    private double serverYaw;

    private boolean canBreak;
    private boolean canPlace;

    private final Timer lastPlaceOrBreak = new Timer();

    private int oldSlot;

    @EventHandler
    private Listener<PlaySoundEvent> onPlaySound = new Listener<>(event -> {

        if (cancelMode.getValue() == CancelMode.Sound) {
            attackedCrystals.forEach(id -> mc.world.removeEntity(id));
            attackedCrystals.clear();
        }

    }, EventPriority.HIGH, event -> event.sound.getCategory().getName().equals(SoundCategory.BLOCKS.getName()) && event.sound.getId().getPath().equals("entity.generic.explode"));

    @EventHandler
    private Listener<TickEvent> onTick = new Listener<>(event -> {
        if (mc.player == null || mc.world == null)
            return;

        if (needsPause()) {
            lastPlaceOrBreak.reset(); //to delay resetting the rotations after it's no longer paused
            return;
        }

        if (lastPlaceOrBreak.passed(switchBackDelay.getValue()))
            if (switchBack.getValue() && oldSlot != -1) {
                InventoryUtils.select(oldSlot);
                oldSlot = -1;
            }

        if (lastPlaceOrBreak.passed(resetRotateDelay.getValue()))
            if (resetRotate.getValue())
                RotationUtils.rotateToCam();

        if (logic.getValue() == CaLogic.WorldTick)
            doCrystalAura();

    }, EventPriority.HIGH, event -> event.era == NextEvent.Era.POST);

    @EventHandler
    private Listener<SendMovementPacketsEvent> onSendMovementPackets = new Listener<>(event -> {

        if (logic.getValue() == CaLogic.PlayerTick)
            doCrystalAura();

    }, EventPriority.HIGH, event -> event.era == NextEvent.Era.PRE);

    @EventHandler
    private Listener<TickEvent> onPreTick = new Listener<>(event -> {
        if (mc.player == null || mc.world == null)
            return;

        if (cancelMode.getValue() == CancelMode.Instant) {
            attackedCrystals.forEach(id -> mc.world.removeEntity(id));
            attackedCrystals.clear();
        }

    }, EventPriority.HIGH, event -> event.era == NextEvent.Era.PRE);

    @EventHandler
    private Listener<WorldRenderEvent> onWorldRender = new Listener<>(event -> {
        if (renderBlock == null) return;

        RenderUtils.drawBoxBoth(renderBlock, QuadColor.single(sidesColor.getValue().getRGB()), QuadColor.single(linesColor.getValue().getRGB()), lineWidth.getValue().floatValue());
    });

    @EventHandler
    private Listener<PacketSentEvent> onPacketSent = new Listener<>(event -> serverYaw = ((PlayerMoveC2SPacket) event.packet).getYaw((float) serverYaw), event -> event.packet instanceof PlayerMoveC2SPacket);

    @EventHandler
    private Listener<EntityAddedEvent> onEntityAdded = new Listener<>(event -> {

        if (fastBreak.getValue() && target != null && canBreak && !needsPause()) {
            DmgResult res = getBreakDmg((EndCrystalEntity) event.entity, target);

            if ((DamageUtils.willExplosionPop(event.entity.getPos(), 6f, mc.player) && antiPop.getValue()) ||
                    (DamageUtils.willExplosionKill(event.entity.getPos(), 6f, mc.player) && antiSuicide.getValue()))
                return;

            if (res.valid)
                breakCrystal((EndCrystalEntity) event.entity);
        }

    }, EventPriority.HIGHEST, event -> event.entity instanceof EndCrystalEntity);

    @EventHandler
    private Listener<EntityRemovedEvent> onEntityRemoved = new Listener<>(event -> {

        if (fastPlace.getValue() && canPlace && target != null && !needsPause()) {

            PlaceResult placeRes = new PlaceResult();
            BlockPos pos = event.entity.getBlockPos().down();

            boolean throughWalls = true;

            placeRes.pos = pos;
            placeRes.dir = Direction.UP;

            if (mc.player.getPos().distanceTo(Vec3d.of(pos)) > placeRange.getValue())
                return;

            for (Direction d: Direction.values())
                if (CrystalUtils.getFullRayTrace(pos, d)) {
                    throughWalls = false;
                    placeRes.dir = d;
                    break;
                }

            if (throughWalls)
                if (raytrace.getValue() || mc.player.getPos().squaredDistanceTo(Vec3d.of(pos)) > wallsPlaceRange.getValue() * wallsPlaceRange.getValue())
                    return;

            if ((DamageUtils.willExplosionPop(pos, 6f, mc.player) && antiPop.getValue()) ||
                    (DamageUtils.willExplosionKill(pos, 6f, mc.player) && antiSuicide.getValue()))
                return;

            DmgResult dmgRes = getPlaceDmg(pos, target);

            if (dmgRes.valid)
                placeCrystal(placeRes);
        }

    }, EventPriority.HIGHEST, event -> event.entity instanceof EndCrystalEntity);

    @Override
    public void onEnable() {
        if (mc.player == null || mc.world == null) {
            setEnabled(false);
            return;
        }

        breakDelayCounter = 0;
        placeDelayCounter = 0;

        oldSlot = -1;

        canBreak = true;
        canPlace = true;

        serverYaw = mc.player.yaw;

        attackedCrystals.clear();

        lastPlaceOrBreak.reset();
    }

    @Override
    public void onDisable() {
        if (mc.player == null || mc.world == null)
            return;

        if (switchBack.getValue() && oldSlot != -1)
            InventoryUtils.select(oldSlot);

        if (resetRotate.getValue())
            RotationUtils.rotateToCam();
    }

    private void doCrystalAura() {
        if (mc.player == null || mc.world == null)
            return;

        canBreak = true;
        canPlace = true;

        if (cBreak.getValue() && breakDelayCounter > breakDelay.getValue() && canBreak)
            breakCrystal();

        if (cPlace.getValue() && placeDelayCounter > placeDelay.getValue() && canPlace)
            placeCrystal();

        placeDelayCounter++;
        breakDelayCounter++;
    }

    private void placeCrystal() {
        placeCrystal(getPlace());
    }

    private void placeCrystal(PlaceResult placeResult) {
        BlockPos targetBlock = placeResult.pos;
        Direction targetDir = placeResult.dir;

        if (targetBlock == null || targetDir == null) {
            renderBlock = null;
            return;
        }

        if (autoSwitch.getValue())
            doSwitch();

        Hand hand;

        if(mc.player.getOffHandStack().getItem() == Items.END_CRYSTAL)
            hand = Hand.OFF_HAND;
        else if (mc.player.getMainHandStack().getItem() == Items.END_CRYSTAL)
            hand = Hand.MAIN_HAND;
        else
            return;

        renderBlock = targetBlock;

        placeDelayCounter = 0;

        Vec3d vec = new Vec3d(targetBlock.getX(), targetBlock.getY() + 0.5, targetBlock.getZ());

        if (rotate.getValue()) {

            if (yawStepMode.getValue() == YawStepMode.All || yawStepMode.getValue() == YawStepMode.Place) {
                if (doYawStep(RotationUtils.getYaw(Vec3d.of(targetBlock).add(0.5, 1.0, 0.5)), RotationUtils.getPitch(Vec3d.of(targetBlock).add(0.5, 1.0, 0.5))))
                    RotationUtils.INSTANCE.rotate(RotationUtils.getYaw(Vec3d.of(targetBlock).add(0.5, 1.0, 0.5)), RotationUtils.getPitch(Vec3d.of(targetBlock).add(0.5, 1.0, 0.5)), 25, () -> mc.interactionManager.interactBlock(mc.player, mc.world, hand, new BlockHitResult(vec, targetDir, targetBlock, false)));
            } else
                RotationUtils.INSTANCE.rotate(RotationUtils.getYaw(Vec3d.of(targetBlock).add(0.5, 1.0, 0.5)), RotationUtils.getPitch(Vec3d.of(targetBlock).add(0.5, 1.0, 0.5)), 25, () -> mc.interactionManager.interactBlock(mc.player, mc.world, hand, new BlockHitResult(vec, targetDir, targetBlock, false)));
        } else
            mc.interactionManager.interactBlock(mc.player, mc.world, hand, new BlockHitResult(vec, targetDir, targetBlock, false));

        canPlace = false;

        lastPlaceOrBreak.reset();
    }

    private void breakCrystal() {
        breakCrystal(getBreak());
    }

    private void breakCrystal(EndCrystalEntity targetEntity) {
        if (targetEntity == null)
            return;

        if (antiWeakness.getValue() && mc.player.hasStatusEffect(StatusEffects.WEAKNESS) && !mc.player.hasStatusEffect(StatusEffects.STRENGTH))
            doWeaknessSwitch();

        if (rotate.getValue()) {
            if (yawStepMode.getValue() == YawStepMode.All || yawStepMode.getValue() == YawStepMode.Break) {
                if (doYawStep(RotationUtils.getYaw(targetEntity), RotationUtils.getPitch(targetEntity)))
                    RotationUtils.INSTANCE.rotate(RotationUtils.getYaw(targetEntity), RotationUtils.getPitch(targetEntity), 30, () -> mc.interactionManager.attackEntity(mc.player, targetEntity));
            } else
                RotationUtils.INSTANCE.rotate(RotationUtils.getYaw(targetEntity), RotationUtils.getPitch(targetEntity), 30, () -> mc.interactionManager.attackEntity(mc.player, targetEntity));
        }
        else
            mc.interactionManager.attackEntity(mc.player, targetEntity);

        if (swing.getValue())
            mc.player.swingHand(Hand.MAIN_HAND);
        else
            mc.getNetworkHandler().sendPacket(new HandSwingC2SPacket(Hand.MAIN_HAND));

        attackedCrystals.add(targetEntity.getEntityId());

        canBreak = false;

        if (!sameTick.getValue())
            canPlace = false;

        lastPlaceOrBreak.reset();
    }

    private PlaceResult getPlace() {
        List<BlockPos> possibleLocations = CrystalUtils.getPlacePositions(placeRange.getValue(), oldPlace.getValue(), !crystalCheck.getValue());

        float bestDmg = 0;
        PlaceResult result = new PlaceResult();

        for (BlockPos pos : possibleLocations) {

            result.dir = Direction.UP;

            boolean throughWalls = true;
            for (Direction d: Direction.values())
                if (CrystalUtils.getFullRayTrace(pos, d)) {
                    throughWalls = false;
                    result.dir = d;
                    break;
                }

            if (raytraceMode.getValue() == RaytraceMode.Full ? throughWalls : CrystalUtils.getLineRayTrace(pos))
                if (raytrace.getValue() || mc.player.getPos().squaredDistanceTo(Vec3d.of(pos)) > wallsPlaceRange.getValue() * wallsPlaceRange.getValue())
                    continue;

            if (antiPop.getValue() && (DamageUtils.willExplosionPop(pos, 6f, mc.player)) ||
                    (antiSuicide.getValue() && DamageUtils.willExplosionKill(pos, 6f, mc.player)))
                continue;

            if (targetingMode.getValue() == TargetingMode.All)
                for (Entity entity : mc.world.getEntities()) {
                    if (!(entity instanceof PlayerEntity) || entity == mc.player || Main.socialManager.isFriend(entity.getName().getString()) || mc.player.isDead() || mc.player.squaredDistanceTo(entity) > 13 * 13)
                        continue;

                    DmgResult res = getPlaceDmg(pos, (PlayerEntity) entity);

                    if (!res.valid)
                        continue;

                    if (res.targetDmg > bestDmg) {
                        bestDmg = res.targetDmg;
                        result.pos = pos;
                        target = (PlayerEntity) entity;
                    }
                }
            else {
                PlayerEntity nearest = getNearestTarget();

                if (nearest == null)
                    continue;

                DmgResult res = getPlaceDmg(pos, nearest);

                if (!res.valid)
                    continue;

                if (res.targetDmg > bestDmg) {
                    bestDmg = res.targetDmg;
                    result.pos = pos;
                    target = nearest;
                }
            }
        }

        return result;

    }

    private EndCrystalEntity getBreak() {
        float bestDmg = 0;
        EndCrystalEntity bestCrystal = null;

        for (Entity crystal : mc.world.getEntities()) {
            if (!(crystal instanceof EndCrystalEntity) || mc.player.isDead() || mc.player.distanceTo(crystal) > breakRange.getValue())
                continue;

            if((antiPop.getValue() && DamageUtils.willExplosionPop(crystal.getPos(), 6f, mc.player)) ||
                    (antiSuicide.getValue() && DamageUtils.willExplosionKill(crystal.getPos(), 6f, mc.player)))
                continue;

            boolean throughWalls = true;

            if (raytraceMode.getValue() == RaytraceMode.Full)
                for (Direction d: Direction.values())
                    if (CrystalUtils.getFullRayTrace(crystal.getBoundingBox(), d, -0.001)) {
                        throughWalls = false;
                        break;
                    }
            else
                throughWalls = !mc.player.canSee(crystal);

            if (throughWalls)
                if (raytrace.getValue() || mc.player.squaredDistanceTo(crystal) > wallsBreakRange.getValue() * wallsBreakRange.getValue())
                    continue;

            if (targetingMode.getValue() == TargetingMode.All)
                for (Entity entity : mc.world.getEntities()) {
                    if (!(entity instanceof PlayerEntity) || entity == mc.player || Main.socialManager.isFriend(entity.getName().getString()) || mc.player.isDead() || mc.player.squaredDistanceTo(entity) > 13 * 13)
                        continue;

                    DmgResult res = getBreakDmg( (EndCrystalEntity) crystal, (PlayerEntity) entity);

                    if (!res.valid)
                        continue;

                    if (res.targetDmg > bestDmg) {
                        bestDmg = res.targetDmg;
                        bestCrystal = (EndCrystalEntity) crystal;
                        target = (PlayerEntity) entity;
                    }
                }
            else {
                PlayerEntity nearest = getNearestTarget();

                if (nearest == null)
                    continue;

                DmgResult res = getBreakDmg( (EndCrystalEntity) crystal, nearest);

                if (!res.valid)
                    continue;

                if (res.targetDmg > bestDmg) {
                    bestDmg = res.targetDmg;
                    bestCrystal = (EndCrystalEntity) crystal;
                    target = nearest;
                }
            }
        }

        return bestCrystal;
    }

    private DmgResult getPlaceDmg(BlockPos blockPos, PlayerEntity target) {
        DmgResult result = new DmgResult();

        PlayerEntity tempTarget = target;

        if (targetPredict.getValue())
            tempTarget = EntityUtils.getPredictedEntity(target, targetPredictTicks.getValue(), predictMode.getValue());

        PlayerEntity tempSelf = mc.player;

        if (selfPredict.getValue())
            tempSelf = EntityUtils.getPredictedEntity(mc.player, selfPredictTicks.getValue(), predictMode.getValue());

        result.valid = true;

        result.targetDmg = DamageUtils.getExplosionDamage(blockPos, 6f, tempTarget, terrainIgnore.getValue());
        result.selfDmg = DamageUtils.getExplosionDamage(blockPos, 6f, tempSelf, terrainIgnore.getValue());

        if (!ignoreSelfDamage.getValue() && result.selfDmg > maxSelfDamage.getValue())
            result.valid = false;

        if ((facePlace.getValue() && target.getHealth() < facePlaceHp.getValue() && result.targetDmg > 2) || (armorBreaker.getValue() && CrystalUtils.getArmorBreaker(target, armorBreakerPct.getValue()) && result.targetDmg > 0.5))
            return result;

        if ((result.targetDmg < minHpPlace.getValue() && result.targetDmg * lethalMultiplier.getValue() < target.getHealth()) || (useSafety.getValue() && result.selfDmg / result.targetDmg * 100 > safety.getValue()))
            result.valid = false;

        return result;
    }

    private DmgResult getBreakDmg(EndCrystalEntity crystal, PlayerEntity target) {
        DmgResult result = new DmgResult();

        result.valid = true;

        result.targetDmg = DamageUtils.getExplosionDamage(crystal.getPos(), 6f, target, terrainIgnore.getValue());
        result.selfDmg = DamageUtils.getExplosionDamage(crystal.getPos(), 6f, mc.player, terrainIgnore.getValue());

        if (!ignoreSelfDamage.getValue() && result.selfDmg > maxSelfDamage.getValue())
            result.valid = false;

        if ((facePlace.getValue() && target.getHealth() < facePlaceHp.getValue() && result.targetDmg > 2) || (armorBreaker.getValue() && CrystalUtils.getArmorBreaker(target, armorBreakerPct.getValue()) && result.targetDmg > 0.5))
            return result;

        if ((result.targetDmg < minHpBreak.getValue() && result.targetDmg * lethalMultiplier.getValue() < target.getHealth()) || (useSafety.getValue() && result.selfDmg / result.targetDmg * 100 > safety.getValue()))
            result.valid = false;

        return result;
    }

    public boolean doYawStep(double targetYaw, double targetPitch) {
        targetYaw = MathHelper.wrapDegrees(targetYaw) + 180;
        double serverYaw = MathHelper.wrapDegrees(this.serverYaw) + 180;

        if (MathUtils.distanceBetweenAngles(serverYaw, targetYaw) <= yawSteps.getValue()) return true;

        double delta = Math.abs(targetYaw - serverYaw);
        double yaw = this.serverYaw;

        if (serverYaw < targetYaw) {
            if (delta < 180) yaw += yawSteps.getValue();
            else yaw -= yawSteps.getValue();
        }
        else {
            if (delta < 180) yaw -= yawSteps.getValue();
            else yaw += yawSteps.getValue();
        }

        RotationUtils.INSTANCE.rotate(yaw, targetPitch, -100, null);

        return false;
    }

    private PlayerEntity getNearestTarget() {
        PlayerEntity nearestTarget = null;
        double nearestDistance = Double.MAX_VALUE;

        for (Entity target : mc.world.getEntities()) {
            if (!(target instanceof PlayerEntity) || target == mc.player || mc.player.isDead() || mc.player.squaredDistanceTo(target) > 13 * 13)
                continue;

            double distance = target.squaredDistanceTo(mc.player);

            if (distance < nearestDistance) {
                nearestTarget = (PlayerEntity) target;
                nearestDistance = distance;
            }
        }

        return nearestTarget;
    }

    private void doSwitch() {
        if (mc.player != null && mc.player.getMainHandStack().getItem() != Items.END_CRYSTAL && mc.player.getOffHandStack().getItem() != Items.END_CRYSTAL) {
            System.out.println("here");
            int slot = InventoryUtils.findItemInHotbar(Items.END_CRYSTAL).slot;
            if (slot != -1 && slot < 9) {
                oldSlot = mc.player.inventory.selectedSlot;
                InventoryUtils.select(slot);
            }
        }
    }

    private void doWeaknessSwitch() {
        if (mc.player != null && !(mc.player.getMainHandStack().getItem() instanceof ToolItem)) {
            int slot = InventoryUtils.findItemInHotbar(itemStack -> itemStack.getItem() instanceof ToolItem).slot;
            if (slot != -1 && slot < 9) {
                oldSlot = mc.player.inventory.selectedSlot;
                InventoryUtils.select(slot);
            }
        }
    }

    private boolean needsPause() {
        if ((mc.player.isUsingItem() && (mc.player.getMainHandStack().getItem().isFood() || mc.player.getOffHandStack().getItem().isFood()) && stopWhileEating.getValue())
                || (mc.interactionManager.isBreakingBlock() && stopWhileMining.getValue()))
            return true;

        return Main.moduleManager.getModule(Surround.class).getEnabled() && !Main.moduleManager.getModule(Surround.class).isSurrounded();
    }

    @Override
    public String getHudInfo() {
        if (target != null)
            return "[" + Formatting.WHITE + target.getName().getString() + Formatting.GRAY + "]";
        else
            return "";
    }

    private static class DmgResult {
        public boolean valid;
        public float targetDmg;
        public float selfDmg;
    }

    private static class PlaceResult {
        public BlockPos pos;
        public Direction dir;
    }
}
