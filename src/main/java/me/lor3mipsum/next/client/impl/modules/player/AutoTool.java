package me.lor3mipsum.next.client.impl.modules.player;

import me.lor3mipsum.next.client.event.EventTarget;
import me.lor3mipsum.next.client.impl.events.StartBreakingBlockEvent;
import me.lor3mipsum.next.client.impl.events.TickEvent;
import me.lor3mipsum.next.client.impl.settings.BooleanSetting;
import me.lor3mipsum.next.client.impl.settings.KeybindSetting;
import me.lor3mipsum.next.client.impl.settings.ModeSetting;
import me.lor3mipsum.next.client.module.Category;
import me.lor3mipsum.next.client.module.Module;
import me.lor3mipsum.next.mixin.AxeItemAccessor;
import me.lor3mipsum.next.mixin.HoeItemAccessor;
import me.lor3mipsum.next.mixin.PickaxeItemAccessor;
import me.lor3mipsum.next.mixin.ShovelItemAccessor;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.*;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import org.lwjgl.glfw.GLFW;

import java.util.HashSet;
import java.util.Set;

public class AutoTool extends Module {

    public ModeSetting prefer = new ModeSetting("Prefer", "Fortune", "Fortune", "SilkTouch");
    public BooleanSetting antiBreak = new BooleanSetting("AntiBreak", true);
    public BooleanSetting switchBack = new BooleanSetting("SwitchBack", true);
    public KeybindSetting keybind = new KeybindSetting(GLFW.GLFW_KEY_UNKNOWN);

    private static final Set<Material> EMPTY_MATERIALS = new HashSet<>(0);
    private static final Set<Block> EMPTY_BLOCKS = new HashSet<>(0);

    private int prevSlot;
    private boolean wasPressed;

    public AutoTool() {
        super("AutoTool", "Automatically switches to the best tool.", Category.PLAYER);
    }

    @EventTarget
    private void onTick(TickEvent.Post event) {
        if (switchBack.isOn() && !mc.options.keyAttack.isPressed() && wasPressed && prevSlot != -1) {
            mc.player.inventory.selectedSlot = prevSlot;
            prevSlot = -1;
        }

        wasPressed = mc.options.keyAttack.isPressed();
    }

    @EventTarget
    private void onStartBreakingBlock(StartBreakingBlockEvent event) {
        BlockState blockState = mc.world.getBlockState(event.blockPos);
        int bestScore = -1;
        int score;
        int bestSlot = -1;

        if (blockState.getHardness(mc.world, event.blockPos) < 0 || blockState.isAir()) return;

        for (int i = 0; i < 9; i++) {
            score = 0;

            ItemStack itemStack = mc.player.inventory.getStack(i);

            if (!isEffectiveOn(itemStack.getItem(), blockState) || shouldStopUsing(itemStack) || !(itemStack.getItem() instanceof ToolItem)) continue;

            score += Math.round(itemStack.getMiningSpeedMultiplier(blockState));
            score += EnchantmentHelper.getLevel(Enchantments.UNBREAKING, itemStack);
            score += EnchantmentHelper.getLevel(Enchantments.EFFICIENCY, itemStack);

            if (prefer.getMode() == "Fortune") score += EnchantmentHelper.getLevel(Enchantments.FORTUNE, itemStack);
            if (prefer.getMode() == "SilkTouch") score += EnchantmentHelper.getLevel(Enchantments.SILK_TOUCH, itemStack);

            if (score > bestScore) {
                bestScore = score;
                bestSlot = i;
            }
        }

        if (bestSlot != -1) {
            if (prevSlot == -1) prevSlot = mc.player.inventory.selectedSlot;
            mc.player.inventory.selectedSlot = bestSlot;
            mc.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(bestSlot));
        }

        ItemStack currentStack = mc.player.inventory.getStack(mc.player.inventory.selectedSlot);

        if (shouldStopUsing(currentStack) && currentStack.getItem() instanceof ToolItem) {
            mc.options.keyAttack.setPressed(false);
            event.setCancelled(true);
        }
    }

    public boolean isEffectiveOn(Item item, BlockState blockState) {
        if (item.isEffectiveOn(blockState)) return true;

        Set<Material> effectiveMaterials;
        Set<Block> effectiveBlocks;

        if (item instanceof PickaxeItem) {
            effectiveMaterials = EMPTY_MATERIALS;
            effectiveBlocks = PickaxeItemAccessor.getEffectiveBlocks();
        } else if (item instanceof AxeItem) {
            effectiveMaterials = AxeItemAccessor.getEffectiveMaterials();
            effectiveBlocks = AxeItemAccessor.getEffectiveBlocks();
        } else if (item instanceof ShovelItem) {
            effectiveMaterials = EMPTY_MATERIALS;
            effectiveBlocks = ShovelItemAccessor.getEffectiveBlocks();
        } else if (item instanceof HoeItem) {
            effectiveMaterials = EMPTY_MATERIALS;
            effectiveBlocks = HoeItemAccessor.getEffectiveBlocks();
        } else if (item instanceof SwordItem) {
            effectiveMaterials = EMPTY_MATERIALS;
            effectiveBlocks = EMPTY_BLOCKS;
        } else {
            return false;
        }

        return effectiveMaterials.contains(blockState.getMaterial()) || effectiveBlocks.contains(blockState.getBlock());
    }

    private boolean shouldStopUsing(ItemStack itemStack) {
        return antiBreak.isOn() && itemStack.getMaxDamage() - itemStack.getDamage() < 2;
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
