package me.lor3mipsum.next.api.util.player;

import me.lor3mipsum.next.mixin.accessor.HorseScreenHandlerAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.mob.SkeletonHorseEntity;
import net.minecraft.entity.mob.ZombieHorseEntity;
import net.minecraft.entity.passive.AbstractDonkeyEntity;
import net.minecraft.entity.passive.HorseBaseEntity;
import net.minecraft.entity.passive.HorseEntity;
import net.minecraft.entity.passive.LlamaEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.screen.*;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;

import java.util.function.Predicate;

public class InventoryUtils {

    public static final int HOTBAR_START = 0;
    public static final int HOTBAR_END = 8;

    public static final int OFFHAND = 45;

    public static final int INVENTORY_START = 9;
    public static final int INVENTORY_END = 35;

    public static final int ARMOR_START = 36;
    public static final int ARMOR_END = 39;

    private static final MinecraftClient mc = MinecraftClient.getInstance();

    private static final InventoryAction ACTION = new InventoryAction();

    //Selection

    public static void select(int slot) {
        mc.player.inventory.selectedSlot = slot;
        mc.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(slot));
    }

    //Actions

    public static InventoryAction move() {
        ACTION.type = SlotActionType.PICKUP;
        ACTION.two = true;
        return ACTION;
    }

    public static InventoryAction click() {
        ACTION.type = SlotActionType.PICKUP;
        return ACTION;
    }

    public static InventoryAction quickMove() {
        ACTION.type = SlotActionType.QUICK_MOVE;
        return ACTION;
    }

    public static InventoryAction drop() {
        ACTION.type = SlotActionType.THROW;
        ACTION.data = 1;
        return ACTION;
    }

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

    public static class InventoryAction {
        private SlotActionType type = null;

        private boolean two = false;

        private int from = -1;
        private int to = -1;

        private int data = 0;

        private boolean isRecursive = false;

        private InventoryAction() {}

        // From

        public InventoryAction fromId(int id) {
            from = id;
            return this;
        }

        public InventoryAction from(int index) {
            return fromId(indexToId(index));
        }

        public InventoryAction fromHotbar(int i) {
            return from(HOTBAR_START + i);
        }

        public InventoryAction fromOffhand() {
            return from(OFFHAND);
        }

        public InventoryAction fromMain(int i) {
            return from(INVENTORY_START + i);
        }

        public InventoryAction fromArmor(int i) {
            return from(ARMOR_START + (3 - i));
        }

        // To

        public void toId(int id) {
            to = id;
            run();
        }

        public void to(int index) {
            toId(indexToId(index));
        }

        public void toHotbar(int i) {
            to(HOTBAR_START + i);
        }

        public void toOffhand() {
            to(OFFHAND);
        }

        public void toMain(int i) {
            to(INVENTORY_START + i);
        }

        public void toArmor(int i) {
            to(ARMOR_START + (3 - i));
        }

        // Slot

        public void slotId(int id) {
            from = to = id;
            run();
        }

        public void slot(int index) {
            slotId(indexToId(index));
        }

        public void slotHotbar(int i) {
            slot(HOTBAR_START + i);
        }

        public void slotOffhand() {
            slot(OFFHAND);
        }

        public void slotMain(int i) {
            slot(INVENTORY_START + i);
        }

        public void slotArmor(int i) {
            slot(ARMOR_START + (3 - i));
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

    //Slots

    public static int indexToId(int i) {
        if (mc.player == null) return -1;
        ScreenHandler handler = mc.player.currentScreenHandler;

        if (handler instanceof PlayerScreenHandler) return survivalInventory(i);
        else if (handler instanceof GenericContainerScreenHandler) return genericContainer(i, ((GenericContainerScreenHandler) handler).getRows());
        else if (handler instanceof CraftingScreenHandler) return craftingTable(i);
        else if (handler instanceof FurnaceScreenHandler) return furnace(i);
        else if (handler instanceof BlastFurnaceScreenHandler) return furnace(i);
        else if (handler instanceof SmokerScreenHandler) return furnace(i);
        else if (handler instanceof Generic3x3ContainerScreenHandler) return generic3x3(i);
        else if (handler instanceof EnchantmentScreenHandler) return enchantmentTable(i);
        else if (handler instanceof BrewingStandScreenHandler) return brewingStand(i);
        else if (handler instanceof MerchantScreenHandler) return villager(i);
        else if (handler instanceof BeaconScreenHandler) return beacon(i);
        else if (handler instanceof AnvilScreenHandler) return anvil(i);
        else if (handler instanceof HopperScreenHandler) return hopper(i);
        else if (handler instanceof ShulkerBoxScreenHandler) return genericContainer(i, 3);
        else if (handler instanceof HorseScreenHandler) return horse(handler, i);
        else if (handler instanceof CartographyTableScreenHandler) return cartographyTable(i);
        else if (handler instanceof GrindstoneScreenHandler) return grindStone(i);
        else if (handler instanceof LecternScreenHandler) return lectern();
        else if (handler instanceof LoomScreenHandler) return loom(i);
        else if (handler instanceof StonecutterScreenHandler) return stoneCutter(i);

        return -1;
    }

    private static int survivalInventory(int i) {
        if (isHotbar(i)) return 36 + i;
        if (isArmor(i)) return 5 + (i - 36);
        return i;
    }

    private static int genericContainer(int i, int rows) {
        if (isHotbar(i)) return (rows + 3) * 9 + i;
        if (isInventory(i)) return rows * 9 + (i - 9);
        return -1;
    }

    private static int craftingTable(int i) {
        if (isHotbar(i)) return 37 + i;
        if (isInventory(i)) return i + 1;
        return -1;
    }

    private static int furnace(int i) {
        if (isHotbar(i)) return 30 + i;
        if (isInventory(i)) return 3 + (i - 9);
        return -1;
    }

    private static int generic3x3(int i) {
        if (isHotbar(i)) return 36 + i;
        if (isInventory(i)) return i;
        return -1;
    }

    private static int enchantmentTable(int i) {
        if (isHotbar(i)) return 29 + i;
        if (isInventory(i)) return 2 + (i - 9);
        return -1;
    }

    private static int brewingStand(int i) {
        if (isHotbar(i)) return 32 + i;
        if (isInventory(i)) return 5 + (i - 9);
        return -1;
    }

    private static int villager(int i) {
        if (isHotbar(i)) return 30 + i;
        if (isInventory(i)) return 3 + (i - 9);
        return -1;
    }

    private static int beacon(int i) {
        if (isHotbar(i)) return 28 + i;
        if (isInventory(i)) return 1 + (i - 9);
        return -1;
    }

    private static int anvil(int i) {
        if (isHotbar(i)) return 30 + i;
        if (isInventory(i)) return 3 + (i - 9);
        return -1;
    }

    private static int hopper(int i) {
        if (isHotbar(i)) return 32 + i;
        if (isInventory(i)) return 5 + (i - 9);
        return -1;
    }

    private static int horse(ScreenHandler handler, int i) {
        HorseBaseEntity entity = ((HorseScreenHandlerAccessor) handler).getEntity();

        if (entity instanceof LlamaEntity) {
            int strength = ((LlamaEntity) entity).getStrength();
            if (isHotbar(i)) return (2 + 3 * strength) + 28 + i;
            if (isInventory(i)) return (2 + 3 * strength) + 1 + (i - 9);
        }
        else if (entity instanceof HorseEntity || entity instanceof SkeletonHorseEntity || entity instanceof ZombieHorseEntity) {
            if (isHotbar(i)) return 29 + i;
            if (isInventory(i)) return 2 + (i - 9);
        }
        else if (entity instanceof AbstractDonkeyEntity) {
            boolean chest = ((AbstractDonkeyEntity) entity).hasChest();
            if (isHotbar(i)) return (chest ? 44 : 29) + i;
            if (isInventory(i)) return (chest ? 17 : 2) + (i - 9);
        }

        return -1;
    }

    private static int cartographyTable(int i) {
        if (isHotbar(i)) return 30 + i;
        if (isInventory(i)) return 3 + (i - 9);
        return -1;
    }

    private static int grindStone(int i) {
        if (isHotbar(i)) return 30 + i;
        if (isInventory(i)) return 3 + (i - 9);
        return -1;
    }

    private static int lectern() {
        return -1;
    }

    private static int loom(int i) {
        if (isHotbar(i)) return 31 + i;
        if (isInventory(i)) return 4 + (i - 9);
        return -1;
    }

    private static int stoneCutter(int i) {
        if (isHotbar(i)) return 29 + i;
        if (isInventory(i)) return 2 + (i - 9);
        return -1;
    }


    private static boolean isHotbar(int i) {
        return i >= HOTBAR_START && i <= HOTBAR_END;
    }

    private static boolean isInventory(int i) {
        return i >= INVENTORY_START && i <= INVENTORY_END;
    }

    private static boolean isArmor(int i) {
        return i >= ARMOR_START && i <= ARMOR_END;
    }
}
