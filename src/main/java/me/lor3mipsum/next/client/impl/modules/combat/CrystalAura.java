package me.lor3mipsum.next.client.impl.modules.combat;

import me.lor3mipsum.next.Main;
import me.lor3mipsum.next.api.util.entity.DamageUtils;
import me.lor3mipsum.next.api.util.entity.EntityUtils;
import me.lor3mipsum.next.api.util.misc.NextColor;
import me.lor3mipsum.next.api.util.player.InventoryUtils;
import me.lor3mipsum.next.api.util.player.RotationUtils;
import me.lor3mipsum.next.api.util.world.CrystalUtils;
import me.lor3mipsum.next.client.core.module.Category;
import me.lor3mipsum.next.client.core.module.Module;
import me.lor3mipsum.next.client.core.module.annotation.Mod;
import me.lor3mipsum.next.client.core.setting.SettingSeparator;
import me.lor3mipsum.next.client.impl.settings.*;
import net.minecraft.entity.DamageUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolItem;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

@Mod(name = "CrystalAura", description = "Crystals go brrr", category = Category.COMBAT)
public class CrystalAura extends Module {

    public SettingSeparator actionsSep = new SettingSeparator("Actions");

    public BooleanSetting cPlace = new BooleanSetting("Place", true);
    public BooleanSetting cBreak = new BooleanSetting("Break", true);

    public SettingSeparator generalSep = new SettingSeparator("General");

    public EnumSetting<CancelMode> cancelMode = new EnumSetting<>("Cancel Mode", CancelMode.Instant);
    public BooleanSetting antiSuicide = new BooleanSetting("Anti Suicide", true);
    public BooleanSetting antiPop = new BooleanSetting("Anti Pop", false);
    public BooleanSetting oldPlace = new BooleanSetting("Old Place", false);
    public BooleanSetting crystalCheck = new BooleanSetting("Crystal Check", false);

    public SettingSeparator delaysSep = new SettingSeparator("Delays");

    public IntegerSetting breakDelay = new IntegerSetting("Break Delay", 2, 0, 20);
    public IntegerSetting placeDelay = new IntegerSetting("Place Delay", 2, 0, 20);

    public SettingSeparator rangesSep = new SettingSeparator("Ranges");

    public DoubleSetting breakRange = new DoubleSetting("Break Range", 5.0, 0.0, 6.0);
    public DoubleSetting placeRange = new DoubleSetting("Place Range", 5.0, 0.0, 6.0);
    public DoubleSetting wallsBreakRange = new DoubleSetting("Walls Break Range", 3.0, 0.0, 6.0);
    public DoubleSetting wallsPlaceRange = new DoubleSetting("Walls Place Range", 3.0, 0.0, 6.0);

    public SettingSeparator damagesSep = new SettingSeparator("Damages");

    public IntegerSetting minHpPlace = new IntegerSetting("Min Place Dmg", 9, 0, 36);
    public IntegerSetting minHpBreak = new IntegerSetting("Min Break Dmg", 8, 0, 36);
    public IntegerSetting maxSelfDamage = new IntegerSetting("Max Self Dmg", 5, 0, 36);
    public BooleanSetting ignoreSelfDamage = new BooleanSetting("Ignore Self Dmg", false);

    public SettingSeparator rotationsSep = new SettingSeparator("Rotations");

    public BooleanSetting rotate = new BooleanSetting("Rotate", true);
    public BooleanSetting raytrace = new BooleanSetting("Raytrace", false);
    public BooleanSetting resetRotate = new BooleanSetting("Reset Rotations", true);

    public SettingSeparator predictionsSep = new SettingSeparator("Predictions");

    public EnumSetting<EntityUtils.PredictMode> predictMode = new EnumSetting<>("Predict Mode", EntityUtils.PredictMode.Line);
    public BooleanSetting targetPredict = new BooleanSetting("Target Predict", true);
    public BooleanSetting selfPredict = new BooleanSetting("Self Predict", false);
    public IntegerSetting targetPredictTicks = new IntegerSetting("Target Predict Ticks", 2, 0, 20);
    public IntegerSetting selfPredictTicks = new IntegerSetting("Self Predict Ticks", 2, 0, 20);

    public SettingSeparator switchingSep = new SettingSeparator("Switching");

    public BooleanSetting autoSwitch = new BooleanSetting("Auto Switch", true);
    public BooleanSetting switchBack = new BooleanSetting("Switch Back", true);
    public BooleanSetting antiWeakness = new BooleanSetting("Anti Weakness", true);

    public SettingSeparator stoppingSep = new SettingSeparator("Stopping");

    public BooleanSetting stopWhileMining = new BooleanSetting("StopWhileMining", false);
    public BooleanSetting stopWhileEating = new BooleanSetting("StopWhileEating", false);

    public SettingSeparator facePlaceSep = new SettingSeparator("FacePlace");

    public BooleanSetting facePlace = new BooleanSetting("FacePlace", true);
    public IntegerSetting facePlaceHp = new IntegerSetting("FacePlace Hp", 10, 0, 36);
    public IntegerSetting facePlaceDelay = new IntegerSetting("FacePlace Delay", 5, 0, 20);

    public SettingSeparator armorBreakerSep = new SettingSeparator("ArmorBreaker");

    public BooleanSetting armorBreaker = new BooleanSetting("ArmorBreaker", true);
    public IntegerSetting armorBreakerPct = new IntegerSetting("ArmorBreaker Pct", 10, 1, 100);

    public SettingSeparator renderSep = new SettingSeparator("Render");

    public ColorSetting sidesColor = new ColorSetting("Sides Color", false, new NextColor(255, 255, 255));
    public ColorSetting linesColor = new ColorSetting("Lines Color", false, new NextColor(255, 255, 255));
    public DoubleSetting lineWidth = new DoubleSetting("Line Width", 2.5, 0.1, 5);
    public BooleanSetting swing = new BooleanSetting("Swing", true);

    public enum CancelMode {
        Sound,
        Instant,
        None
    }

    private final List<EndCrystalEntity> attemptedCrystals = new ArrayList<>();

    private PlayerEntity target;
    private BlockPos renderBlock;

    private boolean alreadyAttacking;
    private boolean placeTimeoutFlag;
    private boolean hasPacketBroke;
    private boolean isRotating;
    private boolean didAnything;
    private boolean facePlacing;

    private int placeTimeout;
    private int breakTimeout;
    private int breakDelayCounter;
    private int placeDelayCounter;
    private int facePlaceDelayCounter;

    int oldSlot;

    private void breakCrystal() {
        EndCrystalEntity crystal = getBestCrystal();

        if (crystal == null) return;

        if (antiWeakness.getValue() &&  mc.player.hasStatusEffect(StatusEffects.WEAKNESS))
            if (!mc.player.hasStatusEffect(StatusEffects.STRENGTH))
                doWeaknessSwitch();


        didAnything = true;

        if (rotate.getValue())
            RotationUtils.INSTANCE.rotate(RotationUtils.getYaw(crystal), RotationUtils.getPitch(crystal), 30, () -> mc.interactionManager.attackEntity(mc.player, crystal));
        else
            mc.interactionManager.attackEntity(mc.player, crystal);

        if (swing.getValue())
            mc.player.swingHand(Hand.MAIN_HAND);
        else
            mc.getNetworkHandler().sendPacket(new HandSwingC2SPacket(Hand.MAIN_HAND));

        breakDelayCounter = 0;
    }

    private EndCrystalEntity getBestCrystal() {
        double bestDamage = 0;
        EndCrystalEntity bestCrystal = null;

        for (Entity e : mc.world.getEntities()) {

            if (!(e instanceof EndCrystalEntity)) continue;

            EndCrystalEntity crystal = (EndCrystalEntity) e;

            for (PlayerEntity target : mc.world.getPlayers()) {
                double targetDamage = getFinalDamage(crystal, target);
                if (targetDamage == 0) continue;

                if (targetDamage > bestDamage) {
                    bestDamage = targetDamage;
                    this.target = target;
                    bestCrystal = crystal;
                }
            }
        }

        return bestCrystal;
    }

    private double getFinalDamage(EndCrystalEntity crystal, PlayerEntity target) {
        if (this.isPlayerValid(target)) {
            if (mc.player.canSee(crystal))
                if (mc.player.distanceTo(crystal) > breakRange.getValue())
                    return 0;
            else
                if (mc.player.distanceTo(crystal) > wallsBreakRange.getValue())
                    return 0;

            if (!crystal.isAlive()) return 0;

            if (attemptedCrystals.contains(crystal)) return 0;

            double miniumDamage;
            if (DamageUtils.getExplosionDamage(crystal.getPos(), 6f, target) >= minHpPlace.getValue()) {
                facePlacing = false;
                miniumDamage = this.minHpBreak.getValue();
            } else if ((target.getHealth() <= facePlaceHp.getValue() && facePlace.getValue()) || (CrystalUtils.getArmorBreaker(target, armorBreakerPct.getValue()) && armorBreaker.getValue())) {
                miniumDamage = 0.5;
                facePlacing = true;
            } else {
                facePlacing = false;
                miniumDamage = this.minHpBreak.getValue();
            }

            double targetDamage = DamageUtils.getExplosionDamage(crystal.getPos(), 6f, target);
            if (targetDamage < miniumDamage && target.getHealth() - targetDamage > 0) return 0;

            double selfDamage = 0;
            if(!ignoreSelfDamage.getValue()) {
                PlayerEntity selfDmgTarget = mc.player;

                if (selfPredict.getValue())
                    selfDmgTarget = EntityUtils.getPredictedEntity(mc.player, selfPredictTicks.getValue(), predictMode.getValue());

                selfDamage = DamageUtils.getExplosionDamage(crystal.getPos(), 6f, selfDmgTarget);
            }

            if (selfDamage > maxSelfDamage.getValue()) return 0;
            if (DamageUtils.willExplosionKill(crystal.getPos(), 6f, mc.player) && antiSuicide.getValue()) return 0;
            if (DamageUtils.willExplosionPop(crystal.getPos(), 6f, mc.player) && antiPop.getValue()) return 0;

            return targetDamage;
        }

        return 0;
    }

    private boolean isPlayerValid(PlayerEntity player) {
        if (mc.player.isDead() || player == mc.player) return false;
        if (Main.socialManager.isFriend(player.getName().getString())) return false;
        if (player.distanceTo(mc.player) > 15) return false;

        return true;
    }

    private void doWeaknessSwitch() {
        if (mc.player != null && !(mc.player.getMainHandStack().getItem() instanceof ToolItem && mc.player.getOffHandStack().getItem() instanceof SwordItem)) {
            int slot = InventoryUtils.findItemInHotbar(itemStack -> itemStack.getItem() instanceof ToolItem || itemStack.getItem() instanceof SwordItem).slot;
            if (slot != -1 && slot < 9) {
                oldSlot = mc.player.inventory.selectedSlot;
                InventoryUtils.select(slot);
            }
        }
    }

}
