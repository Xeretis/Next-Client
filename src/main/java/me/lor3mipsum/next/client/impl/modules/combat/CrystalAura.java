package me.lor3mipsum.next.client.impl.modules.combat;

import com.google.common.collect.Streams;
import me.lor3mipsum.next.Next;
import me.lor3mipsum.next.client.event.EventTarget;
import me.lor3mipsum.next.client.impl.events.*;
import me.lor3mipsum.next.client.impl.settings.BooleanSetting;
import me.lor3mipsum.next.client.impl.settings.KeybindSetting;
import me.lor3mipsum.next.client.impl.settings.ModeSetting;
import me.lor3mipsum.next.client.impl.settings.NumberSetting;
import me.lor3mipsum.next.client.module.Category;
import me.lor3mipsum.next.client.social.SocialManager;
import me.lor3mipsum.next.client.utils.player.*;
import me.lor3mipsum.next.client.utils.render.RenderUtils;
import me.lor3mipsum.next.client.utils.render.color.QuadColor;
import me.lor3mipsum.next.client.utils.world.WorldUtils;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.AmbientEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.mob.WaterCreatureEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.SnowGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolItem;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.sound.SoundCategory;
import net.minecraft.stat.Stat;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;
import me.lor3mipsum.next.client.module.Module;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
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
    public BooleanSetting raytrace = new BooleanSetting("RayTrace", false);
    public BooleanSetting antiWeakness = new BooleanSetting("AntiWeakness", true);
    public ModeSetting breakMode = new ModeSetting("BreakMode", "Always", "Always", "Smart");
    public NumberSetting facePlaceHp = new NumberSetting("FacePlaceHp", 10, 0, 36, 1);
    public BooleanSetting predict = new BooleanSetting("SelfPredict", true);
    public BooleanSetting multiplace = new BooleanSetting("Multiplace", false);
    public BooleanSetting cancel = new BooleanSetting("SoundCancel", true);

    public NumberSetting delay = new NumberSetting("Delay", 0, 0, 10, 1);

    public NumberSetting minDmg = new NumberSetting("MinDmg", 8, 0, 36, 1);
    public NumberSetting maxSelfDmg = new NumberSetting("MaxSelfDmg", 6, 0, 36, 1);

    public BooleanSetting rotate = new BooleanSetting("Rotate", true);

    public BooleanSetting autoSwitch = new BooleanSetting("AutoSwitch", true);
    public BooleanSetting switchBack = new BooleanSetting("SwitchBack", true);
    public BooleanSetting antiSuicide = new BooleanSetting("AntiSuicide", true);

    public BooleanSetting stopWhileMining = new BooleanSetting("StopWhileMining", false);
    public BooleanSetting stopWhileEating = new BooleanSetting("StopWhileEating", false);

    public KeybindSetting keybind = new KeybindSetting(GLFW.GLFW_KEY_UNKNOWN);

    public static me.lor3mipsum.next.client.utils.misc.Timer removeVisualTimer = new me.lor3mipsum.next.client.utils.misc.Timer();
    public static List<BlockPos> placedCrystals = new CopyOnWriteArrayList<>();
    private ConcurrentHashMap<EndCrystalEntity, Integer> attackedCrystals = new ConcurrentHashMap<>();
    private List<BlockPos> placeLocations = new CopyOnWriteArrayList<>();
    private int remainingTicks;
    private BlockPos placePos, breakPos;
    private me.lor3mipsum.next.client.utils.misc.Timer lastPlaceOrBreak = new me.lor3mipsum.next.client.utils.misc.Timer();
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
            float selfDamage = CrystalUtil.calculateDamage(new Vec3d(e.getX(), e.getY(), e.getZ()), 6f, mc.player);
            if (selfDamage > maxSelfDmg.getNumber() || (antiSuicide.isOn() && selfDamage >= mc.player.getHealth() + mc.player.getAbsorptionAmount())) {
                return false;
            }

            //Finds the best position for most damage
            for (AbstractClientPlayerEntity player : mc.world.getPlayers()) {
                //Ignore if the player is us, a friend, dead, or has no health (the dead variable is sometimes delayed)
                if (player == mc.player ||(!friends.isOn() && SocialManager.isFriend(player.getName().getString())) || mc.player.isDead() || (mc.player.getHealth() + mc.player.getAbsorptionAmount()) <= 0.0f) {
                    continue;
                }

                //Store this as a variable for faceplace per player
                double minDamage = minDmg.getNumber();

                //Check if players health + gap health is less than or equal to faceplace, then we activate faceplacing
                if (player.getHealth() + player.getAbsorptionAmount() <= facePlaceHp.getNumber()) {
                    minDamage = 1f;
                }

                float calculatedDamage = CrystalUtil.calculateDamage(new Vec3d(e.getX(), e.getY(), e.getZ()), 6f, player);
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

    private boolean VerifyCrystalBlocks(BlockPos pos) {
        //Check distance
        if (mc.player.getPos().distanceTo(Vec3d.of(pos)) > placeRange.getNumber() * placeRange.getNumber()) {
            return false;
        }

        //Check walls range
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

        //Check self damage
        float selfDamage = CrystalUtil.calculateDamage(pos, 6f, mc.player);

        //Make sure self damage is not greater than maxselfdamage
        if (selfDamage > maxSelfDmg.getNumber()) {
            return false;
        }

        //No suicide, verify self damage won't kill us
        if (antiSuicide.isOn() && selfDamage >= mc.player.getHealth()+mc.player.getAbsorptionAmount()) {
            return false;
        }

        //Its an ok position.
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

        final List<BlockPos> cachedCrystalBlocks = CrystalUtil.findCrystalBlocks(mc.player, (float)placeRange.getNumber(), predict.isOn() ? mc.player : null).stream().filter(pos -> VerifyCrystalBlocks(pos)).collect(Collectors.toList());

        LivingEntity target = null;

        if (!cachedCrystalBlocks.isEmpty()) {
            float damage = 0f;

            //Iterate through all entities, and crystal positions to find the best position for most damage
            for (Entity entity2 : mc.world.getEntities()) {
                LivingEntity entity = null;
                try {
                    entity = (LivingEntity) entity2;
                } catch (ClassCastException ex) {
                    continue;
                }

                //Ignore if the player is us, dead, or has no health (the dead variable is sometimes delayed)
                if (entity == mc.player || mc.player.isDead() || (mc.player.getHealth() + mc.player.getAbsorptionAmount()) <= 0.0f) {
                    continue;
                }

                //continue if the entity isnt accepted type
                if (entity instanceof PlayerEntity && !players.isOn() || entity instanceof PlayerEntity && (!friends.isOn() && SocialManager.isFriend(entity.getName().getString())) || entity instanceof Monster && !hostiles.isOn()
                        || (entity instanceof AmbientEntity || entity instanceof WaterCreatureEntity || entity instanceof IronGolemEntity || entity instanceof SnowGolemEntity || entity instanceof PassiveEntity) && !animals.isOn()) {
                    continue;
                }

                //Store this as a variable for faceplace per playerolemEntity || e instanceof SnowGolemEntity || e instanceof PassiveEntity) && animals.isOn())
                double minDamage = minDmg.getNumber();

                //Check if players health + gap health is less than or equal to faceplace, then we activate faceplacing
                if (entity.getHealth() + entity.getAbsorptionAmount() <= facePlaceHp.getNumber()) {
                    minDamage = 1f;
                }

                //Iterate through all valid crystal blocks for this player, and calculate the damages.
                for (BlockPos pos : cachedCrystalBlocks) {
                    float calculatedDamage = CrystalUtil.calculateDamage(pos, 6f, entity);

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
                //The player could have died during this code run, wait till next tick for doing more calculations.
                if (target.isDead() || target.getHealth() <= 0.0f) {
                    return;
                }

                //Ensure we have place locations
                if (!placeLocations.isEmpty()) {
                    //Store this as a variable for faceplace per player
                    double minDamage = minDmg.getNumber();

                    //Check if players health + gap health is less than or equal to faceplace, then we activate faceplacing
                    if (target.getHealth() + target.getAbsorptionAmount() <= facePlaceHp.getNumber()) {
                        minDamage = 1f;
                    }

                    //Iterate this again, we need to remove some values that are useless, since we iterated all players
                    for (BlockPos pos : placeLocations) {
                        //Make sure the position will still deal enough damage to the player
                        float calculatedDamage = CrystalUtil.calculateDamage(pos, 6f, target);

                        //Remove if this doesnt
                        if (calculatedDamage < minDamage) {
                            placeLocations.remove(pos);
                        }
                    }

                    //At this point, the place locations list is in asc order, we need to reverse it to get to desc
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

            //Rotate to crystal
            if (rotate.isOn()) {
                float[] rotation = PlayerUtils.calculateAngle(crystal.getPos());
                Rotations.INSTANCE.rotate(rotation[0], rotation[1], 30, () -> mc.interactionManager.attackEntity(mc.player, crystal));
            }
            else
                mc.interactionManager.attackEntity(mc.player, crystal);

            mc.player.swingHand(Hand.MAIN_HAND);
            addAttackedCrystal(crystal);

            //If we are not multiplacing return here, we have something to do for this tick.
            if (!multiplace.isOn()) {
                return;
            }
        }

        //Verify the placeTimer is ready, selectedPosition is not 0,0,0 and the event isn't already cancelled
        if (!placeLocations.isEmpty() && place.isOn()) {
            //Iterate through available place locations
            BlockPos selectedPos = null;
            for (BlockPos pos : placeLocations) {
                // verify we can still place crystals at this location, if we can't we try next location
                if (CrystalUtil.canPlaceCrystal(pos)) {
                    selectedPos = pos;
                    break;
                }
            }

            //Nothing found... this is bad, wait for next tick to correct it
            if (selectedPos == null) {
                remainingTicks = 0;
                return;
            }

            if (predict.isOn()) {

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
                float[] rotation = PlayerUtils.calculateAngle(vec.add(0.5, 1.0, 0.5));

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

        if (pos == null)
            pos = breakPos;

        if (pos != null)
            RenderUtils.drawBoxBoth(pos, QuadColor.single(255, 255, 255, 100), 2.5f);
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
        return "[" + Formatting.WHITE + lastTarget.getName().getString() + Formatting.GRAY + "]";
    }
}
