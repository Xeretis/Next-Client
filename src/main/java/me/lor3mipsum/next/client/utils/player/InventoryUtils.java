package me.lor3mipsum.next.client.utils.player;

import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;

import java.util.Comparator;
import java.util.function.Predicate;
import java.util.stream.IntStream;

public class InventoryUtils {
    private static final Action ACTION = new Action();

    private static final FindItemResult findItemResult = new FindItemResult();

    private static final MinecraftClient mc = MinecraftClient.getInstance();

    // Interactions

    public static Action move() {
        ACTION.type = SlotActionType.PICKUP;
        ACTION.two = true;
        return ACTION;
    }

    public static Action click() {
        ACTION.type = SlotActionType.PICKUP;
        return ACTION;
    }

    public static Action quickMove() {
        ACTION.type = SlotActionType.QUICK_MOVE;
        return ACTION;
    }

    public static Action drop() {
        ACTION.type = SlotActionType.THROW;
        ACTION.data = 1;
        return ACTION;
    }

    // Hand

    public static Hand getHand(Item item) {
        Hand hand = Hand.MAIN_HAND;
        if (mc.player.getOffHandStack().getItem() == item) hand = Hand.OFF_HAND;
        return hand;
    }

    public static Hand getHand(Predicate<ItemStack> isGood) {
        Hand hand = null;
        if (isGood.test(mc.player.getMainHandStack())) hand = Hand.MAIN_HAND;
        else if (isGood.test(mc.player.getOffHandStack())) hand = Hand.OFF_HAND;

        return hand;
    }

    // Find item

    public static FindItemResult findItemWithCount(Item item) {
        findItemResult.slot = -1;
        findItemResult.count = 0;

        for (int i = 0; i < mc.player.inventory.size(); i++) {
            ItemStack itemStack = mc.player.inventory.getStack(i);

            if (itemStack.getItem() == item) {
                if (!findItemResult.found()) findItemResult.slot = i;
                findItemResult.count += itemStack.getCount();
            }
        }

        return findItemResult;
    }

    //Whole
    public static int findItemInAll(Item item) {
        return findItemInHotbar(item, itemStack -> true);
    }

    public static int findItemInAll(Item item, Predicate<ItemStack> isGood) {
        return findItem(item, isGood, mc.player.inventory.size());
    }

    public static int findItemInAll(Predicate<ItemStack> isGood) {
        return findItem(null, isGood, mc.player.inventory.size());
    }

    //Hotbar
    public static int findItemInHotbar(Item item) {
        return findItemInHotbar(item, itemStack -> true);
    }

    public static int findItemInHotbar(Item item, Predicate<ItemStack> isGood) {
        return findItem(item, isGood, 9);
    }

    public static int findItemInHotbar(Predicate<ItemStack> isGood) {
        return findItem(null, isGood, 9);
    }

    //Main
    public static int findItemInMain(Item item) {
        return findItemInHotbar(item, itemStack -> true);
    }

    public static int findItemInMain(Item item, Predicate<ItemStack> isGood) {
        return findItem(item, isGood, mc.player.inventory.main.size());
    }

    public static int findItemInMain(Predicate<ItemStack> isGood) {
        return findItem(null, isGood, mc.player.inventory.main.size());
    }

    private static int findItem(Item item, Predicate<ItemStack> isGood, int size) {
        for (int i = 0; i < size; i++) {
            ItemStack itemStack = mc.player.inventory.getStack(i);
            if ((item == null || itemStack.getItem() == item) && isGood.test(itemStack)) return i;
        }
        return -1;
    }

    public static class FindItemResult {
        public int slot, count;

        public boolean found() {
            return slot != -1;
        }
    }

    public static class Action {
        private SlotActionType type = null;
        private boolean two = false;
        private int from = -1;
        private int to = -1;
        private int data = 0;

        private boolean isRecursive = false;

        private Action() {}

        // From

        public Action fromId(int id) {
            from = id;
            return this;
        }

        public Action from(int index) {
            return fromId(SlotUtils.indexToId(index));
        }

        public Action fromHotbar(int i) {
            return from(SlotUtils.HOTBAR_START + i);
        }

        public Action fromOffhand() {
            return from(SlotUtils.OFFHAND);
        }

        public Action fromMain(int i) {
            return from(SlotUtils.MAIN_START + i);
        }

        public Action fromArmor(int i) {
            return from(SlotUtils.ARMOR_START + (3 - i));
        }

        // To

        public void toId(int id) {
            to = id;
            run();
        }

        public void to(int index) {
            toId(SlotUtils.indexToId(index));
        }

        public void toHotbar(int i) {
            to(SlotUtils.HOTBAR_START + i);
        }

        public void toOffhand() {
            to(SlotUtils.OFFHAND);
        }

        public void toMain(int i) {
            to(SlotUtils.MAIN_START + i);
        }

        public void toArmor(int i) {
            to(SlotUtils.ARMOR_START + (3 - i));
        }

        // Slot

        public void slotId(int id) {
            from = to = id;
            run();
        }

        public void slot(int index) {
            slotId(SlotUtils.indexToId(index));
        }

        public void slotHotbar(int i) {
            slot(SlotUtils.HOTBAR_START + i);
        }

        public void slotOffhand() {
            slot(SlotUtils.OFFHAND);
        }

        public void slotMain(int i) {
            slot(SlotUtils.MAIN_START + i);
        }

        public void slotArmor(int i) {
            slot(SlotUtils.ARMOR_START + (3 - i));
        }

        // Other

        private void run() {
            boolean hadEmptyCursor = mc.player.inventory.getCursorStack().isEmpty();

            if (type != null && from != -1 && to != -1) {
                click(from);
                if (two) click(to);
            }

            SlotActionType preType = type;
            boolean preTwo = two;
            int preFrom = from;
            int preTo = to;

            type = null;
            two = false;
            from = -1;
            to = -1;
            data = 0;

            if (!isRecursive && hadEmptyCursor && preType == SlotActionType.PICKUP && preTwo && (preFrom != -1 && preTo != -1) && !mc.player.inventory.getCursorStack().isEmpty()) {
                isRecursive = true;
                InventoryUtils.click().slotId(preFrom);
                isRecursive = false;
            }
        }

        private void click(int id) {
            mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, id, data, type, mc.player);
        }
    }

    /** Selects the slot with the lowest comparator value and returns the hand it selected **/
    public static Hand selectSlot(boolean offhand, boolean reverse, Comparator<Integer> comparator) {
        return selectSlot(getSlot(offhand, reverse, comparator));
    }

    /** Returns the slot with the lowest comparator value **/
    public static int getSlot(boolean offhand, boolean reverse, Comparator<Integer> comparator) {
        return IntStream.of(getInventorySlots(offhand))
                .boxed()
                .min(comparator.reversed()).orElse(-1);
    }

    /** Returns the first slot that matches the Predicate **/
    public static int getSlot(boolean offhand, Predicate<Integer> filter) {
        return IntStream.of(getInventorySlots(offhand))
                .boxed()
                .filter(filter)
                .findFirst().orElse(-1);
    }

    /** Selects the first slot that matches the Predicate and returns the hand it selected **/
    public static Hand selectSlot(boolean offhand, Predicate<Integer> filter) {
        return selectSlot(getSlot(offhand, filter));
    }

    public static Hand selectSlot(int slot) {
        if (slot >= 0 && slot <= 36) {
            if (slot < 9) {
                if (slot != mc.player.inventory.selectedSlot) {
                    mc.player.inventory.selectedSlot = slot;
                    mc.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(slot));
                }

                return Hand.MAIN_HAND;
            } else if (mc.player.playerScreenHandler == mc.player.currentScreenHandler) {
                if (mc.player.inventory.getMainHandStack().isEmpty()) {
                    mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, slot, 0, SlotActionType.PICKUP, mc.player);
                    mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, 36 + mc.player.inventory.selectedSlot, 0, SlotActionType.PICKUP, mc.player);
                    return Hand.MAIN_HAND;
                }

                for (int i = 0; i <= 8; i++) {
                    if (mc.player.inventory.getStack(i).isEmpty()) {
                        mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, slot, 0, SlotActionType.PICKUP, mc.player);
                        mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, 36 + i, 0, SlotActionType.PICKUP, mc.player);
                        mc.player.inventory.selectedSlot = i;
                        mc.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(slot));
                        return Hand.MAIN_HAND;
                    }
                }

                mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, slot, 0, SlotActionType.PICKUP, mc.player);
                mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, 36 + mc.player.inventory.selectedSlot, 0, SlotActionType.PICKUP, mc.player);
                mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, slot, 0, SlotActionType.PICKUP, mc.player);
                return Hand.MAIN_HAND;
            }
        } else if (slot == 40) {
            return Hand.OFF_HAND;
        }

        return null;
    }

    private static int[] getInventorySlots(boolean offhand) {
        int[] i = new int[offhand ? 38 : 37];

        // Add hand slots first
        i[0] = mc.player.inventory.selectedSlot;
        i[1] = 40;

        for (int j = 0; j < 36; j++) {
            if (j != mc.player.inventory.selectedSlot) {
                i[offhand ? j + 2 : j + 1] = j;
            }
        }

        return i;
    }
}
