package me.lor3mipsum.next.client.impl.modules.combat;

import me.lor3mipsum.next.Next;
import me.lor3mipsum.next.client.event.EventTarget;
import me.lor3mipsum.next.client.impl.events.TickEvent;
import me.lor3mipsum.next.client.impl.modules.player.Freecam;
import me.lor3mipsum.next.client.impl.settings.BooleanSetting;
import me.lor3mipsum.next.client.impl.settings.KeybindSetting;
import me.lor3mipsum.next.client.impl.settings.ModeSetting;
import me.lor3mipsum.next.client.impl.settings.NumberSetting;
import me.lor3mipsum.next.client.module.Category;
import me.lor3mipsum.next.client.module.Module;
import me.lor3mipsum.next.client.social.SocialManager;
import me.lor3mipsum.next.client.utils.player.InventoryUtils;
import me.lor3mipsum.next.client.utils.player.PlayerUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.mob.AmbientEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.mob.WaterCreatureEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.SnowGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.SwordItem;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class KillAura extends Module {
    public NumberSetting range = new NumberSetting("Range", 4, 0, 6, 0.1);
    public BooleanSetting players = new BooleanSetting("Players", true);
    public BooleanSetting friends = new BooleanSetting("Friends", false);
    public BooleanSetting hostiles = new BooleanSetting("Hostiles", false);
    public BooleanSetting animals = new BooleanSetting("Animals", false);
    public BooleanSetting ignoreWalls = new BooleanSetting("IgnoreWalls", true);
    public ModeSetting priority = new ModeSetting("Priority", "Closest", "Closest", "LowestHp");
    public BooleanSetting onlySword = new BooleanSetting("OnlySword", false);
    public BooleanSetting autoSwitch = new BooleanSetting("AutoSwitch", false);
    public NumberSetting delay = new NumberSetting("Delay", 0, 0, 60, 1);
    public BooleanSetting autoDelay = new BooleanSetting("AutoDelay", true);
    public BooleanSetting multiTarget = new BooleanSetting("MultiTarget", false);
    public BooleanSetting swing = new BooleanSetting("SwingHand", true);
    public KeybindSetting keybind = new KeybindSetting(GLFW.GLFW_KEY_UNKNOWN);

    private int hitDelayTimer;
    private boolean canAttack;

    private final List<Entity> entityList = new ArrayList<>();

    public KillAura() {
        super("KillAura", "Makes your aura kill or sth", Category.COMBAT);
    }

    @Override
    public void onDisable() {
        hitDelayTimer = 0;
        entityList.clear();
    }


    @EventTarget
    private void onTick(TickEvent.Post event) {

        entityList.clear();

        if (mc.player.isDead() || !mc.player.isAlive() || !itemInHand()) return;

        getEntities(entity -> {
            if (entity.distanceTo(mc.player) > range.getNumber()) return false;
            if (!players.isOn() && entity instanceof PlayerEntity) return false;
            if (!hostiles.isOn() && entity instanceof Monster) return false;
            if (!animals.isOn() && (entity instanceof AmbientEntity || entity instanceof WaterCreatureEntity || entity instanceof IronGolemEntity || entity instanceof SnowGolemEntity || entity instanceof PassiveEntity)) return false;
            if (!(entity instanceof LivingEntity) || entity instanceof ArmorStandEntity) return false;
            if ((entity instanceof LivingEntity && ((LivingEntity) entity).isDead()) || !entity.isAlive()) return false;
            if (entity == mc.player || entity == mc.cameraEntity || (Next.INSTANCE.moduleManager.getModule(Freecam.class).isOn() && entity == Next.INSTANCE.moduleManager.getModule(Freecam.class).dummy)) return false;
            if (!ignoreWalls.isOn() && !PlayerUtils.canSeeEntity(entity)) return false;
            if (entity instanceof PlayerEntity) {
                if (((PlayerEntity) entity).isCreative()) return false;
                if (!friends.isOn() && SocialManager.isFriend(entity.getEntityName())) return false;
            }
            return true;
        }, priority.getMode().equals("Closest"), entityList);

        if (!multiTarget.isOn() && !entityList.isEmpty())
            entityList.subList(1, entityList.size()).clear();

        if (autoDelay.isOn() && mc.player.getAttackCooldownProgress(0.5f) < 1) {
            return;
        }

        if (hitDelayTimer >= 0) {
            hitDelayTimer--;
            return;
        }
        else {
            hitDelayTimer = (int) delay.getNumber();
        }

        for (Entity target : entityList) {
            if (attack(target) && (canAttack)) {
                hitEntity(target);
            }
        }
    }

    private boolean attack(Entity target) {
        canAttack = false;

        hitEntity(target);

        canAttack = true;
        return true;
    }

    private void hitEntity(Entity target) {
        int slot = InventoryUtils.findItemInHotbar(itemStack -> {
                    Item item = itemStack.getItem();
                    return ((item instanceof SwordItem) && autoSwitch.isOn());
                }
        );
        if (autoSwitch.isOn() && slot != -1) mc.player.inventory.selectedSlot = slot;
        mc.interactionManager.attackEntity(mc.player, target);
        if (swing.isOn()) mc.player.swingHand(Hand.MAIN_HAND);
    }

    private boolean itemInHand() {
        return !onlySword.isOn() || mc.player.getMainHandStack().getItem() instanceof SwordItem;
    }

    private void getEntities(Predicate<Entity> isGood, boolean  closest, List<Entity> target) {
        for (Entity entity : mc.world.getEntities()) {
            if (isGood.test(entity)) target.add(entity);
        }

        target.sort((e1, e2) -> {
            if (closest)
                return Double.compare(e1.distanceTo(mc.player), e2.distanceTo(mc.player));

            boolean e1l = e1 instanceof LivingEntity;
            boolean e2l = e2 instanceof LivingEntity;

            if (!e1l && !e2l) return 0;
            else if (e1l && !e2l) return 1;
            else if (!e1l) return -1;

            return Float.compare(((LivingEntity) e1).getHealth(), ((LivingEntity) e2).getHealth());
        });
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
        if (!entityList.isEmpty()) {
            Entity targetFirst = entityList.get(0);
            if (targetFirst instanceof PlayerEntity) return "[" + Formatting.WHITE + targetFirst.getEntityName() + Formatting.GRAY + "]";
            return "[" + Formatting.WHITE + targetFirst.getType().getName().getString() + Formatting.GRAY + "]";
        }
        return "";
    }
}
