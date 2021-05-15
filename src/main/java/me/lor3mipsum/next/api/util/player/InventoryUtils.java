package me.lor3mipsum.next.api.util.player;

import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

import java.util.function.Predicate;

public class InventoryUtils {
    private static MinecraftClient mc = MinecraftClient.getInstance();

    //Hands

    public static Hand getHand(Item item) {
        Hand hand = null;

        if (mc.player.getOffHandStack().getItem() == item) hand = Hand.OFF_HAND;
        else if (mc.player.getMainHandStack().getItem() == item) hand = Hand.MAIN_HAND;

        return hand;
    }


    public static Hand getHand(Item item, Hand preferredHand) {
        Hand hand = null;

        if (preferredHand == Hand.OFF_HAND && mc.player.getOffHandStack().getItem() == item) hand = Hand.OFF_HAND;
        else if (preferredHand == Hand.MAIN_HAND && mc.player.getMainHandStack().getItem() == item) hand = Hand.MAIN_HAND;
        else if (mc.player.getOffHandStack().getItem() == item) hand = Hand.OFF_HAND;
        else if (mc.player.getMainHandStack().getItem() == item) hand = Hand.MAIN_HAND;

        return hand;
    }

    public static Hand getHand(Predicate<ItemStack> valid) {
        Hand hand = null;

        if (valid.test(mc.player.getMainHandStack())) hand = Hand.MAIN_HAND;
        else if (valid.test(mc.player.getOffHandStack())) hand = Hand.OFF_HAND;

        return hand;
    }

    //Items

    public static class FindItemResult {
        public int slot, count;
    }

    public static FindItemResult findItemInHotbar(Item item) {
        return findItemIn(item, 0, 8);
    }

    public static FindItemResult findItemInHotbar(Predicate<ItemStack> valid) {
        return findItemIn(valid, 0, 8);
    }

    public static FindItemResult findItemInInventory(Predicate<ItemStack> valid) {
        return findItemIn(valid, 9, 35);
    }

    public static FindItemResult findItemInInventory(Item item) {
        return findItemIn(item, 9, 35);
    }

    public static FindItemResult findItemInAll(Item item) {
        return findItemIn(item, 0, mc.player.inventory.size() - 1);
    }

    public static FindItemResult findItemInAll(Predicate<ItemStack> valid) {
        return findItemIn(valid, 0, mc.player.inventory.size() - 1);
    }

    public static FindItemResult findItemIn(Item item, int startSlot, int endSlot) {
        FindItemResult result = new FindItemResult();

        result.slot = -1;
        result.count = 0;

        for (int i = startSlot; i <= endSlot; i++) {
            ItemStack itemStack = mc.player.inventory.getStack(i);

            if (itemStack.getItem() == item) {
                if (result.slot == -1) result.slot = i;
                result.count += itemStack.getCount();
            }
        }

        return result;
    }

    public static FindItemResult findItemIn(Predicate<ItemStack> valid, int startSlot, int endSlot) {
        FindItemResult result = new FindItemResult();

        result.slot = -1;
        result.count = 0;

        for (int i = startSlot; i <= endSlot; i++) {
            ItemStack itemStack = mc.player.inventory.getStack(i);

            if (valid.test(itemStack)) {
                if (result.slot == -1) result.slot = i;
                result.count += itemStack.getCount();
            }
        }

        return result;
    }
}
