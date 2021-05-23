package me.lor3mipsum.next.client.impl.modules.combat;

import me.lor3mipsum.next.api.event.NextEvent;
import me.lor3mipsum.next.api.event.client.TickEvent;
import me.lor3mipsum.next.api.util.player.ChatUtils;
import me.lor3mipsum.next.api.util.player.InventoryUtils;
import me.lor3mipsum.next.api.util.player.PlaceUtils;
import me.lor3mipsum.next.client.core.module.Category;
import me.lor3mipsum.next.client.core.module.Module;
import me.lor3mipsum.next.client.core.module.annotation.Mod;
import me.lor3mipsum.next.client.core.setting.SettingSeparator;
import me.lor3mipsum.next.client.impl.settings.BooleanSetting;
import me.lor3mipsum.next.client.impl.settings.IntegerSetting;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

@Mod(name = "Surround", description = "Surrounds you with obsidian", category = Category.COMBAT)
public class Surround extends Module {

    SettingSeparator placementSep = new SettingSeparator("Placement");

    public IntegerSetting bpt = new IntegerSetting("BPT", 4, 1, 20);
    public BooleanSetting keep = new BooleanSetting("Stay On", true);
    public BooleanSetting echest = new BooleanSetting("EChest", false);
    public BooleanSetting airplace = new BooleanSetting("AirPlace", true);
    public BooleanSetting blockunder = new BooleanSetting("Block Under", true);

    SettingSeparator movementSep = new SettingSeparator("Movement");

    public BooleanSetting center = new BooleanSetting("Center", true);
    public BooleanSetting jump = new BooleanSetting("Disable On Jump", true);
    public BooleanSetting yChange = new BooleanSetting("Y Change Disable", false);

    SettingSeparator rotationSep = new SettingSeparator("Rotation");

    public BooleanSetting rotate = new BooleanSetting("Rotate", true);
    public BooleanSetting keepFacing = new BooleanSetting("Keep Facing", false);

    SettingSeparator otherSep = new SettingSeparator("Other");

    public BooleanSetting swing = new BooleanSetting("Swing", true);
    public BooleanSetting switchBack = new BooleanSetting("Switch Back", true);

    private int preSlot = -1;

    @Override
    public void onEnable() {
        int obby = findSlot();

        if (obby == -1) {
            ChatUtils.moduleError(this, "No obsidian" + (echest.getValue() ? "/echest" : "") + " found in hotbar");
            setEnabled(false);
            return;
        }

        if (center.getValue()) {
            Vec3d centerPos = Vec3d.of(mc.player.getBlockPos()).add(0.5, 0, 0.5);
            mc.player.updatePosition(centerPos.x, centerPos.y, centerPos.z);
            mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionOnly(centerPos.x, centerPos.y, centerPos.z, mc.player.isOnGround()));
        }

        if (mc.player.inventory.selectedSlot != obby)
            preSlot = mc.player.inventory.selectedSlot;

        InventoryUtils.select(obby);

        if (airplace.getValue())
            airPlaceTick(obby);
        else
            placeTick(obby);

        if (switchBack.getValue() && isSurrounded() && preSlot != -1) {
            InventoryUtils.select(preSlot);
            preSlot = -1;
        }
    }

    @EventHandler
    private Listener<TickEvent> onTick = new Listener<>(event -> {
        if ((jump.getValue() && (mc.options.keyJump.isPressed() || mc.player.input.jumping)) || (yChange.getValue() && mc.player.prevY < mc.player.getY())) {
            setEnabled(false);
            return;
        }

        int obby = findSlot();

        if (obby == -1) {
            ChatUtils.moduleError(this, "No obsidian" + (echest.getValue() ? "/echest " : "") + " found in hotbar");
            setEnabled(false);
            return;
        }

        if (mc.player.inventory.selectedSlot != obby)
            preSlot = mc.player.inventory.selectedSlot;

        InventoryUtils.select(obby);

        if (airplace.getValue())
            airPlaceTick(obby);
        else
            placeTick(obby);

        if (switchBack.getValue() && isSurrounded() && preSlot != -1) {
            InventoryUtils.select(preSlot);
            preSlot = -1;
        }
    }, event -> event.era == NextEvent.Era.POST);

    private int findSlot() {

        InventoryUtils.FindItemResult result = InventoryUtils.findItemInHotbar(item -> item.getItem() == Items.OBSIDIAN || (item.getItem() == Items.ENDER_CHEST && echest.getValue()));

        return result.slot;
    }

    private void placeTick(int obsidian) {
        int cap = 0;

        float yaw = mc.player.yaw;
        float pitch = mc.player.pitch;

        for (BlockPos b : new BlockPos[] {
                mc.player.getBlockPos().north(), mc.player.getBlockPos().east(),
                mc.player.getBlockPos().south(), mc.player.getBlockPos().west() }) {

            if (cap >= bpt.getValue()) {
                return;
            }

            if (!PlaceUtils.canPlace(b, true, false)) {
                if (PlaceUtils.canPlace(b.down(), true, false)) {
                    PlaceUtils.placeBlock(b.down(), Hand.MAIN_HAND, false, swing.getValue(), false, rotate.getValue(), 100);
                    cap++;

                    if (cap >= bpt.getValue()) {
                        return;
                    }
                }
            }

            if (PlaceUtils.placeBlock(b, Hand.MAIN_HAND, false, swing.getValue(), false, rotate.getValue(), 100)) {
                cap++;
            }
        }

        if (keepFacing.getValue()) {
            mc.player.yaw = yaw;
            mc.player.pitch = pitch;
        }

        if (!keep.getValue()) {
            setEnabled(false);
        }
    }

    private void airPlaceTick(int obsidian) {
        int cap = 0;

        float yaw = mc.player.yaw;
        float pitch = mc.player.pitch;

        if (blockunder.getValue()) {
            if (mc.world.getBlockState(mc.player.getBlockPos().down()).getMaterial().isReplaceable()) {
                PlaceUtils.placeBlock(mc.player.getBlockPos().down(), Hand.MAIN_HAND, true, swing.getValue(), false, rotate.getValue(), 100);
                cap++;
            }

        }

        for (BlockPos b : new BlockPos[] {
                mc.player.getBlockPos().north(), mc.player.getBlockPos().east(),
                mc.player.getBlockPos().south(), mc.player.getBlockPos().west() }) {

            if (cap >= bpt.getValue()) {
                return;
            }

            if (PlaceUtils.placeBlock(b, Hand.MAIN_HAND, true, swing.getValue(), false, rotate.getValue(), 100)) {
                cap++;
            }
        }

        if (keepFacing.getValue()) {
            mc.player.yaw = yaw;
            mc.player.pitch = pitch;
        }

        if (!keep.getValue()) {
            setEnabled(false);
        }
    }

    public boolean isSurrounded() {
        List<BlockPos> surroundBlocks = new ArrayList<>();
        surroundBlocks.add(mc.player.getBlockPos().north());
        surroundBlocks.add(mc.player.getBlockPos().south());
        surroundBlocks.add(mc.player.getBlockPos().east());
        surroundBlocks.add(mc.player.getBlockPos().west());

        for (BlockPos l_Pos : surroundBlocks)
        {
            if (mc.world.getBlockState(l_Pos).getMaterial().isReplaceable())
            {
                return false;
            }
        }

        return true;
    }

}
