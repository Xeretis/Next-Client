package me.lor3mipsum.next.client.impl.modules.combat;

import me.lor3mipsum.next.Next;
import me.lor3mipsum.next.client.event.EventTarget;
import me.lor3mipsum.next.client.impl.events.*;
import me.lor3mipsum.next.client.impl.settings.*;
import me.lor3mipsum.next.client.module.Category;
import me.lor3mipsum.next.client.social.SocialManager;
import me.lor3mipsum.next.client.utils.player.*;
import me.lor3mipsum.next.client.utils.render.RenderUtils;
import me.lor3mipsum.next.client.utils.render.color.QuadColor;
import me.lor3mipsum.next.client.utils.world.WorldUtils;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.AmbientEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.mob.WaterCreatureEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.SnowGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolItem;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;
import me.lor3mipsum.next.client.module.Module;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.StreamSupport;

public class CrystalAura extends Module{

    public BooleanSetting place = new BooleanSetting("Place", true);
    public BooleanSetting breaks = new BooleanSetting("Break", true);

    public BooleanSetting players = new BooleanSetting("Players", true);
    public BooleanSetting friends = new BooleanSetting("Friends", false);
    public BooleanSetting hostiles = new BooleanSetting("Hostiles", false);
    public BooleanSetting animals = new BooleanSetting("Animals", false);

    public NumberSetting hitRange = new NumberSetting("HitRange", 5.2, 1, 6, 0.1);
    public NumberSetting placeRange = new NumberSetting("PlaceRange", 5.2, 1, 6, 0.1);
    public NumberSetting wallRange = new NumberSetting("WallRange", 3, 1, 6, 0.1);

    public NumberSetting facePlaceHp = new NumberSetting("FacePlaceHp", 10, 0, 36, 1);
    public BooleanSetting multiPlace = new BooleanSetting("MultiPlace", false);
    public BooleanSetting oldPlace = new BooleanSetting("OldPlace", false);

    public BooleanSetting antiWeakness = new BooleanSetting("AntiWeakness", true);
    public ModeSetting breakMode = new ModeSetting("BreakMode", "Always", "Always", "Smart");
    public BooleanSetting cancel = new BooleanSetting("SoundCancel", true);

    public NumberSetting minDmg = new NumberSetting("MinDmg", 8, 0, 36, 1);
    public NumberSetting maxSelfDmg = new NumberSetting("MaxSelfDmg", 6, 0, 36, 1);

    public NumberSetting delay = new NumberSetting("Delay", 0, 0, 10, 1);
    public BooleanSetting antiSuicide = new BooleanSetting("AntiSuicide", true);
    public BooleanSetting raytrace = new BooleanSetting("RayTrace", false);
    public BooleanSetting rotate = new BooleanSetting("Rotate", true);
    public BooleanSetting resetRotation = new BooleanSetting("ResetRotation", true);

    public BooleanSetting autoSwitch = new BooleanSetting("AutoSwitch", true);
    public BooleanSetting switchBack = new BooleanSetting("SwitchBack", true);

    public BooleanSetting stopWhileMining = new BooleanSetting("StopWhileMining", false);
    public BooleanSetting stopWhileEating = new BooleanSetting("StopWhileEating", false);

    public ColorSetting placeColor = new ColorSetting("PlaceSideColor", Color.GREEN);
    public ColorSetting placeLineColor = new ColorSetting("PlaceLineColor", Color.GREEN);
    public ColorSetting breakColor = new ColorSetting("BreakSideColor", Color.RED);
    public ColorSetting breakLineColor = new ColorSetting("BreakLineColor", Color.RED);
    public NumberSetting lineWidth = new NumberSetting("LineWidth", 2.5, 0, 5.0, 0.1);

    public KeybindSetting keybind = new KeybindSetting(GLFW.GLFW_KEY_UNKNOWN);

    public static List<BlockPos> placedCrystals = new CopyOnWriteArrayList<>();
    private ConcurrentHashMap<EndCrystalEntity, Integer> attackedCrystals = new ConcurrentHashMap<>();
    private List<BlockPos> placeLocations = new CopyOnWriteArrayList<>();
    private BlockPos placePos, breakPos;

    private me.lor3mipsum.next.client.utils.misc.Timer lastPlaceOrBreak = new me.lor3mipsum.next.client.utils.misc.Timer();
    public static me.lor3mipsum.next.client.utils.misc.Timer removeVisualTimer = new me.lor3mipsum.next.client.utils.misc.Timer();
    private int remainingTicks;

    private int oldSlot = -1;

    private LivingEntity lastTarget = null;

    public CrystalAura() {
        super("CrystalAura", "Yes", Category.COMBAT);
    }

    @Override
    public void onEnable() {
        placedCrystals.clear();
        remainingTicks = 0;
        lastPlaceOrBreak.reset();
    }

    @Override
    public void onDisable() {
        placePos = null;
        breakPos = null;
        if (switchBack.isOn() && oldSlot != -1) mc.player.inventory.selectedSlot = oldSlot;
        if (resetRotation.isOn()) {
            mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.LookOnly(mc.player.yaw, mc.player.pitch, mc.player.isOnGround()));
            Rotations.INSTANCE.setCamRotation(mc.player.yaw, mc.player.pitch);
        }
    }

    @EventTarget
    private void onEntityRemoved(EntityRemovedEvent event) {
        if (event.getEntity() instanceof EndCrystalEntity)
            attackedCrystals.remove((EndCrystalEntity) event.getEntity());
    }

    private boolean validateCrystal(EndCrystalEntity e) {
        if (e == null || !e.isAlive()) {
            return false;
        }

        if (attackedCrystals.containsKey(e) && attackedCrystals.get(e) > 5) {
            return false;
        }

        if (raytrace.isOn() && !mc.player.canSee(e))
            return false;

        if (e.distanceTo(mc.player) > (!mc.player.canSee(e) ? wallRange.getNumber() : hitRange.getNumber())) {
            return false;
        }

        if (breakMode.getMode() == "Smart") {
            float selfDamage = CrystalUtils.calculateDamage(new Vec3d(e.getX(), e.getY(), e.getZ()), 6f, mc.player);
            if (selfDamage > maxSelfDmg.getNumber() || (antiSuicide.isOn() && selfDamage >= mc.player.getHealth() + mc.player.getAbsorptionAmount())) {
                return false;
            }

            for (Entity player : mc.world.getEntities()) {
                if (!(player instanceof LivingEntity)) continue;

                LivingEntity entity = (LivingEntity) player;

                if (player == mc.player || mc.player.isDead() || (mc.player.getHealth() + mc.player.getAbsorptionAmount()) <= 0.0f) {
                    continue;
                }

                if (entity instanceof PlayerEntity && !players.isOn() || entity instanceof PlayerEntity && (!friends.isOn() && SocialManager.isFriend(entity.getName().getString())) || entity instanceof Monster && !hostiles.isOn()
                        || (entity instanceof AmbientEntity || entity instanceof WaterCreatureEntity || entity instanceof IronGolemEntity || entity instanceof SnowGolemEntity || entity instanceof PassiveEntity) && !animals.isOn()) {
                    continue;
                }

                double minDamage = minDmg.getNumber();

                if (entity.getHealth() + entity.getAbsorptionAmount() <= facePlaceHp.getNumber()) {
                    minDamage = 1f;
                }

                float calculatedDamage = CrystalUtils.calculateDamage(new Vec3d(e.getX(), e.getY(), e.getZ()), 6f, entity);
                if (calculatedDamage > minDamage) {
                    return true;
                }
            }
        }

        return true;
    }

    public EndCrystalEntity GetNearestCrystalTo(Entity entity) {
        return StreamSupport.stream(mc.world.getEntities().spliterator(), false).filter(e -> e instanceof EndCrystalEntity && validateCrystal((EndCrystalEntity) e)).map(e -> (EndCrystalEntity)e).min(Comparator.comparing(e -> entity.distanceTo(e))).orElse(null);
    }

    public void addAttackedCrystal(EndCrystalEntity crystal) {
        if (attackedCrystals.containsKey(crystal)) {
            int value = attackedCrystals.get(crystal);
            attackedCrystals.put(crystal, value + 1);
        } else {
            attackedCrystals.put(crystal, 1);
        }
    }

    public boolean VerifyCrystalBlocks(BlockPos pos) {
        if (mc.player.getPos().distanceTo(Vec3d.of(pos)) > placeRange.getNumber() * placeRange.getNumber()) {
            return false;
        }

        boolean throughWalls = true;
        for (Direction d: Direction.values()) {
            if (WorldUtils.getLegitLookPos(pos, d, true, 5) != null) {
                throughWalls = false;
                break;
            }
        }

        if (raytrace.isOn() && throughWalls)
            return false;

        if (wallRange.getNumber() > 0) {
            if (throughWalls && Vec3d.of(pos).distanceTo(new Vec3d(mc.player.getX(), mc.player.getY(), mc.player.getZ())) > wallRange.getNumber()) {
                return false;
            }
        }

        float selfDamage = CrystalUtils.calculateDamage(pos, 6f, mc.player);

        if (selfDamage > maxSelfDmg.getNumber()) {
            return false;
        }

        if (antiSuicide.isOn() && selfDamage >= mc.player.getHealth()+mc.player.getAbsorptionAmount()) {
            return false;
        }

        return true;
    }

    @EventTarget
    private void onTick(TickEvent.Post event) {
        if (mc.player == null) {
            return;
        }

        if (lastPlaceOrBreak.hasPassed(500)) {
            placePos = null;
            breakPos = null;
            if (resetRotation.isOn()) {
                mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.LookOnly(mc.player.yaw, mc.player.pitch, mc.player.isOnGround()));
                Rotations.INSTANCE.setCamRotation(mc.player.yaw, mc.player.pitch);
            }
        }

        if (removeVisualTimer.hasPassed(1000)) {
            removeVisualTimer.reset();
            if (!placedCrystals.isEmpty()) {
                placedCrystals.remove(0);
            }

            attackedCrystals.clear();
        }

        if (remainingTicks > 0) {
            remainingTicks--;
            return;
        }

        if (needPause()) {
            remainingTicks = 0;
            return;
        }

        remainingTicks = (int) delay.getNumber();

        final List<BlockPos> cachedCrystalBlocks = CrystalUtils.INSTANCE.crystalBlocks;

        LivingEntity target = null;

        if (!cachedCrystalBlocks.isEmpty()) {
            float damage = 0f;

            for (Entity entity2 : mc.world.getEntities()) {
                LivingEntity entity = null;
                try {
                    entity = (LivingEntity) entity2;
                } catch (ClassCastException ex) {
                    continue;
                }

                if (entity == mc.player || mc.player.isDead() || (mc.player.getHealth() + mc.player.getAbsorptionAmount()) <= 0.0f) {
                    continue;
                }

                if (entity instanceof PlayerEntity && !players.isOn() || entity instanceof PlayerEntity && (!friends.isOn() && SocialManager.isFriend(entity.getName().getString())) || entity instanceof Monster && !hostiles.isOn()
                        || (entity instanceof AmbientEntity || entity instanceof WaterCreatureEntity || entity instanceof IronGolemEntity || entity instanceof SnowGolemEntity || entity instanceof PassiveEntity) && !animals.isOn()) {
                    continue;
                }

                double minDamage = minDmg.getNumber();

                if (entity.getHealth() + entity.getAbsorptionAmount() <= facePlaceHp.getNumber()) {
                    minDamage = 1f;
                }

                for (BlockPos pos : cachedCrystalBlocks) {
                    float calculatedDamage = CrystalUtils.calculateDamage(pos, 6f, entity);

                    if (calculatedDamage >= minDamage && calculatedDamage > damage) {
                        damage = calculatedDamage;
                        if (!placeLocations.contains(pos)) {
                            placeLocations.add(pos);
                        }

                        target = entity;
                        lastTarget = entity;
                    }
                }
            }

            if (target != null) {
                if (target.isDead() || target.getHealth() <= 0.0f) {
                    return;
                }

                if (!placeLocations.isEmpty()) {
                    double minDamage = minDmg.getNumber();

                    if (target.getHealth() + target.getAbsorptionAmount() <= facePlaceHp.getNumber()) {
                        minDamage = 1f;
                    }

                    for (BlockPos pos : placeLocations) {
                        float calculatedDamage = CrystalUtils.calculateDamage(pos, 6f, target);

                        if (calculatedDamage < minDamage) {
                            placeLocations.remove(pos);
                        }
                    }

                    Collections.reverse(placeLocations);
                }
            }
        }

        EndCrystalEntity crystal = GetNearestCrystalTo(mc.player);
        boolean isValidCrystal = crystal != null ? mc.player.distanceTo(crystal) < hitRange.getNumber() : false;
        if (!isValidCrystal && (placeLocations.isEmpty() || !place.isOn())) {
            remainingTicks = 0;
            return;
        }

        if (isValidCrystal && breaks.isOn()) {
            if (antiWeakness.isOn() && mc.player.hasStatusEffect(StatusEffects.WEAKNESS) && !mc.player.hasStatusEffect(StatusEffects.STRENGTH))
                doWeaknessSwitch();

            breakPos = crystal.getBlockPos().add(0, -1, 0);
            placePos = null;
            lastPlaceOrBreak.reset();

            if (rotate.isOn()) {
                float[] rotation = PlayerUtils.calculateAngle(crystal.getPos());
                Rotations.INSTANCE.rotate(rotation[0], rotation[1], 30, () -> mc.interactionManager.attackEntity(mc.player, crystal));
            }
            else
                mc.interactionManager.attackEntity(mc.player, crystal);

            mc.player.swingHand(Hand.MAIN_HAND);
            addAttackedCrystal(crystal);

            if (!multiPlace.isOn()) {
                return;
            }
        }

        if (!placeLocations.isEmpty() && place.isOn()) {
            BlockPos selectedPos = null;
            for (BlockPos pos : placeLocations) {
                if (CrystalUtils.canPlaceCrystalAt(pos)) {
                    selectedPos = pos;
                    break;
                }
            }

            if (selectedPos == null) {
                remainingTicks = 0;
                return;
            }

            if (autoSwitch.isOn() && (mc.player.getOffHandStack().getItem() != Items.END_CRYSTAL || mc.player.getMainHandStack().getItem() != Items.END_CRYSTAL))
                doSwitch();

            Hand hand;

            if(mc.player.getOffHandStack().getItem() == Items.END_CRYSTAL)
                hand = Hand.OFF_HAND;
            else if (mc.player.getMainHandStack().getItem() == Items.END_CRYSTAL)
                hand = Hand.MAIN_HAND;
            else
                return;

            placePos = selectedPos;
            breakPos = null;
            lastPlaceOrBreak.reset();

            Vec3d vec = new Vec3d(selectedPos.getX(), selectedPos.getY() + 0.5, selectedPos.getZ());
            Direction dir = Direction.UP;

            if (rotate.isOn()) {
                float[] rotation = PlayerUtils.calculateAngle(Vec3d.of(selectedPos).add(0.5, 1.0, 0.5));

                Vec3d finalVec = vec;
                BlockPos finalSelectedPos = selectedPos;

                Rotations.INSTANCE.rotate(rotation[0], rotation[1], 25, () -> mc.interactionManager.interactBlock(mc.player, mc.world, hand, new BlockHitResult(finalVec, dir, finalSelectedPos, false)));
            } else {
                mc.interactionManager.interactBlock(mc.player, mc.world, hand, new BlockHitResult(vec, dir, selectedPos, false));
            }


            //If placedcrystals already contains this position, remove it because we need to have it at the back of the list
            if (placedCrystals.contains(selectedPos)) {
                placedCrystals.remove(selectedPos);
            }

            //Adds the selectedPos to the back of the placed crystals list
            placedCrystals.add(selectedPos);

            //Reset the placed location, we just placed there
            placeLocations.clear();
        }
    }

    @EventTarget
    private void onPlaySound(PlaySoundEvent event) {

        if (mc.world == null) {
            return;
        }

        if(event.sound.getCategory().getName().equals(SoundCategory.BLOCKS.getName()) && event.sound.getId().getPath().equals("entity.generic.explode") && cancel.isOn()) {
            for (Entity e : mc.world.getEntities()) {
                if (e instanceof EndCrystalEntity) {
                    if (e.getPos().distanceTo(new Vec3d(event.sound.getX(), event.sound.getY(), event.sound.getZ())) <= 6.0) {
                        mc.world.removeEntity(e.getEntityId());
                    }
                }

                placedCrystals.removeIf(p_Pos -> Vec3d.of(p_Pos).distanceTo(new Vec3d(event.sound.getX(), event.sound.getY(), event.sound.getZ())) <= 6.0);
            }
        }
    }

    @EventTarget
    public void onRender(WorldRenderEvent.Post event) {
        BlockPos pos = placePos;
        Color sideColor = placeColor.getValue();
        Color lineColor = placeLineColor.getValue();

        if (pos == null) {
            pos = breakPos;
            sideColor = breakColor.getValue();
            lineColor = breakLineColor.getValue();
        }

        if (pos != null)
            RenderUtils.drawBoxBoth(pos, QuadColor.single(sideColor.getRGB()), QuadColor.single(lineColor.getRGB()), (float) lineWidth.getNumber());
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

    public boolean needPause() {
        if((mc.player.isUsingItem() && (mc.player.getMainHandStack().getItem().isFood() || mc.player.getOffHandStack().getItem().isFood()) && stopWhileEating.isOn()
                || (mc.interactionManager.isBreakingBlock() && stopWhileMining.isOn())))
            return true;

        if (Next.INSTANCE.moduleManager.getModule(Surround.class).isOn() && !Next.INSTANCE.moduleManager.getModule(Surround.class).isSurrounded())
            return true;

        return false;
    }

    @Override
    public int getKeybind() {
        return keybind.getKey();
    }

    @Override
    public void setKeybind(int keybind) {
        this.keybind.setKey(keybind);
    }

    @Override
    public String getHudInfo() {
        if (lastTarget != null)
            return "[" + Formatting.WHITE + lastTarget.getName().getString() + Formatting.GRAY + "]";
        else
            return "";
    }
}
