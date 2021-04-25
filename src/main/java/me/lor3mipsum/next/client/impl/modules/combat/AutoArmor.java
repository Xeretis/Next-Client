package me.lor3mipsum.next.client.impl.modules.combat;

import me.lor3mipsum.next.client.event.EventTarget;
import me.lor3mipsum.next.client.impl.events.TickEvent;
import me.lor3mipsum.next.client.impl.settings.BooleanSetting;
import me.lor3mipsum.next.client.impl.settings.KeybindSetting;
import me.lor3mipsum.next.client.impl.settings.NumberSetting;
import me.lor3mipsum.next.client.module.Category;
import me.lor3mipsum.next.client.module.Module;
import me.lor3mipsum.next.client.utils.player.InventoryUtils;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.lwjgl.glfw.GLFW;

public class AutoArmor extends Module {
    public BooleanSetting antiBreak = new BooleanSetting("AntiBreak", true);
    public NumberSetting switchDurability = new NumberSetting("SwitchDurability", 10, 1, 99, 1);
    public BooleanSetting ignoreElytra = new BooleanSetting("IgnoreElytra", true);
    public NumberSetting delay = new NumberSetting("Delay", 1, 0, 20, 1);
    public KeybindSetting keybind = new KeybindSetting(GLFW.GLFW_KEY_UNKNOWN);

    private int delayLeft = (int) delay.getNumber();
    private boolean didSkip = false;
    private float currentToughness = 0;
    private int currentBest, currentProt, currentBlast, currentFire, currentProj, currentArmour, currentUnbreaking, currentMending = 0;
    private boolean shouldSwitch = false;

    public AutoArmor() {
        super("AutoArmor", "Automatically equips armor", Category.COMBAT);
    }

    @EventTarget
    private void onTick(TickEvent.Post event) {
        if (mc.player.abilities.creativeMode) return;
        if (delayLeft > 0) {
            delayLeft --;
            return;
        } else {
            delayLeft = (int) delay.getNumber();
        }

        if (didSkip) {
            didSkip = false;
        }
        ItemStack itemStack;
        for (int a = 0; a < 4; a++) {
            itemStack = mc.player.inventory.getArmorStack(a);
            currentBest = 0;
            currentProt = 0;
            currentBlast = 0;
            currentFire = 0;
            currentProj = 0;
            currentArmour = 0;
            currentToughness = 0;
            currentUnbreaking = 0;
            currentMending = 0;
            if ((ignoreElytra.isOn()) && itemStack.getItem() == Items.ELYTRA) continue;
            if (itemStack.getItem() instanceof ArmorItem) {
                getCurrentScore(itemStack, a);
            }
            int bestSlot = -1;
            int bestScore = 0;
            for (int i = 0; i < 36; i++) {
                ItemStack stack = mc.player.inventory.getStack(i);
                if (stack.getItem() instanceof ArmorItem
                        && (((ArmorItem) stack.getItem()).getSlotType().getEntitySlotId() == a)) {
                    int temp = getItemScore(stack, a);
                    if (bestScore < temp || (shouldSwitch && temp != 0)) {
                        bestScore = temp;
                        bestSlot = i;
                    }
                }
            }
            if (bestSlot > -1) {
                InventoryUtils.move().from(bestSlot).toArmor(a);
                if (delay.getNumber() != 0) break;
            }
        }
    }

    private int getItemScore(ItemStack itemStack, int a){
        int score = 0;
        if (antiBreak.isOn() && (itemStack.getMaxDamage() - itemStack.getDamage()) <= switchDurability.getNumber()) return 0;
        score += 4 * (EnchantmentHelper.getLevel(a == 1 ? Enchantments.BLAST_PROTECTION : Enchantments.PROTECTION, itemStack) - currentBest);
        score += 2 * (EnchantmentHelper.getLevel(Enchantments.PROTECTION, itemStack) - currentProt);
        score += 2 * (EnchantmentHelper.getLevel(Enchantments.BLAST_PROTECTION, itemStack) - currentBlast);
        score += 2 * (EnchantmentHelper.getLevel(Enchantments.FIRE_PROTECTION, itemStack) - currentFire);
        score += 2 * (EnchantmentHelper.getLevel(Enchantments.PROJECTILE_PROTECTION, itemStack) - currentProj);
        score += 2 * (((ArmorItem) itemStack.getItem()).getProtection() - currentArmour);
        score += 2 * (((ArmorItem) itemStack.getItem()).method_26353() - currentToughness);
        score += EnchantmentHelper.getLevel(Enchantments.UNBREAKING, itemStack) - currentUnbreaking;
        score += EnchantmentHelper.getLevel(Enchantments.MENDING, itemStack) - currentMending;
        return score;
    }

    private void getCurrentScore(ItemStack itemStack, int a) {
        shouldSwitch = false;
        if (antiBreak.isOn() && (itemStack.getMaxDamage() - itemStack.getDamage()) <= switchDurability.getNumber()) shouldSwitch = true;
        currentBest = EnchantmentHelper.getLevel(a == 1 ? Enchantments.BLAST_PROTECTION : Enchantments.PROTECTION, itemStack);
        currentProt = EnchantmentHelper.getLevel(Enchantments.PROTECTION, itemStack);
        currentBlast = EnchantmentHelper.getLevel(Enchantments.BLAST_PROTECTION, itemStack);
        currentFire = EnchantmentHelper.getLevel(Enchantments.FIRE_PROTECTION, itemStack);
        currentProj = EnchantmentHelper.getLevel(Enchantments.PROJECTILE_PROTECTION, itemStack);
        currentArmour = ((ArmorItem) itemStack.getItem()).getProtection();
        currentToughness = ((ArmorItem) itemStack.getItem()).method_26353();
        currentUnbreaking = EnchantmentHelper.getLevel(Enchantments.UNBREAKING, itemStack);
        currentMending = EnchantmentHelper.getLevel(Enchantments.MENDING, itemStack);
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
