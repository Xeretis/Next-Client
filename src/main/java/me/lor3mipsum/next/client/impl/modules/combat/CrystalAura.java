package me.lor3mipsum.next.client.impl.modules.combat;

import me.lor3mipsum.next.Main;
import me.lor3mipsum.next.api.event.NextEvent;
import me.lor3mipsum.next.api.event.client.TickEvent;
import me.lor3mipsum.next.api.event.network.PacketReceiveEvent;
import me.lor3mipsum.next.api.event.network.PacketSendEvent;
import me.lor3mipsum.next.api.event.network.PlaySoundPacketEvent;
import me.lor3mipsum.next.api.event.world.WorldRenderEvent;
import me.lor3mipsum.next.api.util.entity.DamageUtils;
import me.lor3mipsum.next.api.util.entity.EntityUtils;
import me.lor3mipsum.next.api.util.misc.NextColor;
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
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolItem;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.s2c.play.EntitiesDestroyS2CPacket;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

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

    private final List<Integer> attackedCrystals = new ArrayList<>();
    private final List<Entity> placedCrystals = new ArrayList<>();

    private PlayerEntity target;
    private BlockPos renderBlock;

    private boolean facePlacing;

    private int breakDelayCounter;
    private int placeDelayCounter;

    int oldSlot;

    @EventHandler
    private Listener<PacketSendEvent> onPacketSend = new Listener<>(event -> {





    }, EventPriority.HIGH);

    @EventHandler
    private Listener<PacketReceiveEvent> onPacketReceive = new Listener<>(event -> {



    }, EventPriority.HIGH);

    @EventHandler
    private Listener<PlaySoundPacketEvent> onPlaySound = new Listener<>(event -> {





    }, EventPriority.HIGH, event -> event.packet.getCategory().getName().equals(SoundCategory.BLOCKS.getName()) && event.packet.getSound().getId().getPath().equals("entity.generic.explode"));

    @EventHandler
    private Listener<TickEvent> onTick = new Listener<>(event -> {





    }, EventPriority.HIGH, event -> event.era == NextEvent.Era.POST);

    @EventHandler
    private Listener<WorldRenderEvent> onWorldRender = new Listener<>(event -> {
        if (renderBlock == null) return;

        RenderUtils.drawBoxBoth(renderBlock, QuadColor.single(sidesColor.getValue().getRGB()), QuadColor.single(linesColor.getValue().getRGB()), lineWidth.getValue().floatValue());
    });

    @Override
    public void onEnable() {
        breakDelayCounter = 0;
        placeDelayCounter = 0;

        attackedCrystals.clear();
        placedCrystals.clear();
    }

    @Override
    public void onDisable() {
        if (switchBack.getValue() && oldSlot != -1)
            InventoryUtils.select(oldSlot);

        if (resetRotate.getValue())
            RotationUtils.rotateToCam();
    }

    private void doCrystalAura() {

    }

    private DmgResult getPlaceDmg(BlockPos blockPos, PlayerEntity target) {
        DmgResult result = new DmgResult();

        PlayerEntity tempTarget = target;

        if (targetPredict.getValue())
            tempTarget = EntityUtils.getPredictedEntity(target, targetPredictTicks.getValue(), predictMode.getValue());

        PlayerEntity tempSelf = target;

        if (selfPredict.getValue())
            tempSelf = EntityUtils.getPredictedEntity(mc.player, selfPredictTicks.getValue(), predictMode.getValue());

        result.valid = true;

        result.targetDmg = DamageUtils.getExplosionDamage(CrystalUtils.getCrystalPos(blockPos), 6f, tempTarget);
        result.selfDmg = DamageUtils.getExplosionDamage(CrystalUtils.getCrystalPos(blockPos), 6f, tempSelf);

        if (target.getHealth() < facePlaceHp.getValue() && facePlace.getValue() && result.targetDmg > 2)
            return result;

        if (result.targetDmg < minHpPlace.getValue())
            result.valid = false;

        if (result.selfDmg > maxSelfDamage.getValue() && !ignoreSelfDamage.getValue())
            result.valid = false;

        return result;
    }

    private DmgResult getBreakDmg(EndCrystalEntity crystal, PlayerEntity target) {
        DmgResult result = new DmgResult();

        result.valid = true;

        result.targetDmg = DamageUtils.getExplosionDamage(crystal.getPos(), 6f, target);
        result.selfDmg = DamageUtils.getExplosionDamage(crystal.getPos(), 6f, mc.player);

        if (target.getHealth() < facePlaceHp.getValue() && facePlace.getValue() && result.targetDmg > 2)
            return result;

        if (result.targetDmg < minHpBreak.getValue())
            result.valid = false;

        if (result.selfDmg > maxSelfDamage.getValue() && !ignoreSelfDamage.getValue())
            result.valid = false;

        return result;
    }

    private void doSwitch() {
        if (mc.player != null && mc.player.getMainHandStack().getItem() != Items.END_CRYSTAL && mc.player.getOffHandStack().getItem() != Items.END_CRYSTAL) {
            int slot = InventoryUtils.findItemInHotbar(Items.END_CRYSTAL).slot;
            if (slot != -1 && slot < 9) {
                oldSlot = mc.player.inventory.selectedSlot;
                InventoryUtils.select(slot);
            }
        }
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

    private class DmgResult {
        public boolean valid;
        public float targetDmg;
        public float selfDmg;
    }

}
