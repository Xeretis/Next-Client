package me.lor3mipsum.next.client.impl.modules.combat;

import me.lor3mipsum.next.client.event.EventTarget;
import me.lor3mipsum.next.client.impl.events.TickEvent;
import me.lor3mipsum.next.client.impl.settings.BooleanSetting;
import me.lor3mipsum.next.client.impl.settings.KeybindSetting;
import me.lor3mipsum.next.client.impl.settings.ModeSetting;
import me.lor3mipsum.next.client.impl.settings.NumberSetting;
import me.lor3mipsum.next.client.module.Category;
import me.lor3mipsum.next.client.module.Module;
import me.lor3mipsum.next.client.utils.ChatUtils;
import me.lor3mipsum.next.client.utils.world.WorldUtils;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;

public class Surround extends Module {
    public NumberSetting bpt = new NumberSetting("BPT", 4, 1, 20, 1);
    public BooleanSetting keep = new BooleanSetting("StayOn", true);
    public BooleanSetting center = new BooleanSetting("Center", true);
    public BooleanSetting echest = new BooleanSetting("EChest", false);
    public BooleanSetting jump = new BooleanSetting("DisableOnJump", true);
    public BooleanSetting airplace = new BooleanSetting("AirPlace", true);
    public BooleanSetting blockunder = new BooleanSetting("BlockUnder", true);
    public BooleanSetting rotate = new BooleanSetting("Rotate", true);
    public BooleanSetting keepFacing = new BooleanSetting("KeepFacing", false);
    public KeybindSetting keybind = new KeybindSetting(GLFW.GLFW_KEY_UNKNOWN);

    public Surround() {
        super("Surround", "Surrounds you with obsidian", Category.COMBAT);
    }

    @Override
    public void onEnable() {
        int obby = findSlot();

        if (obby == -1) {
            ChatUtils.moduleError(this, "No obsidian" + (echest.isOn() ? "/echest " : "") + "found in hotbar");
            setState(false);
            return;
        }

        if (center.isOn()) {
            Vec3d centerPos = Vec3d.of(mc.player.getBlockPos()).add(0.5, 0, 0.5);
            mc.player.updatePosition(centerPos.x, centerPos.y, centerPos.z);
            mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionOnly(centerPos.x, centerPos.y, centerPos.z, mc.player.isOnGround()));
        }

        if (airplace.isOn())
            airPlaceTick(obby);
        else
            placeTick(obby);
    }

    @EventTarget
    private void onTick(TickEvent.Post event) {
        if (jump.isOn() && mc.options.keyJump.isPressed()) {
            setState(false);
            return;
        }

        int obby = findSlot();

        if (obby == -1) {
            ChatUtils.moduleError(this, "No obsidian" + (echest.isOn() ? "/echest " : "") + "found in hotbar");
            setState(false);
            return;
        }

        if (airplace.isOn())
            airPlaceTick(obby);
        else
            placeTick(obby);
    }

    private int findSlot() {
        for (int i = 0; i < 9; i++) {
            Item item = mc.player.inventory.getStack(i).getItem();

            if (!(item instanceof BlockItem)) continue;

            if (item == Items.OBSIDIAN || (item == Items.ENDER_CHEST && echest.isOn())) {
                return i;
            }
        }

        return -1;
    }

    private void placeTick(int obsidian) {
        int cap = 0;

        float yaw = mc.player.yaw;
        float pitch = mc.player.pitch;

            for (BlockPos b : new BlockPos[] {
                    mc.player.getBlockPos().north(), mc.player.getBlockPos().east(),
                    mc.player.getBlockPos().south(), mc.player.getBlockPos().west() }) {

                if (cap >= (int) bpt.getNumber()) {
                    return;
                }

                if (!WorldUtils.canPlace(b, true, false)) {
                    if (WorldUtils.canPlace(b.down(), true, false)) {
                        WorldUtils.place(b.down(), Hand.MAIN_HAND, obsidian, rotate.isOn(), 100, true, false);
                        cap++;

                        if (cap >= (int) bpt.getNumber()) {
                            return;
                        }
                    }
                }

                if (WorldUtils.place(b, Hand.MAIN_HAND, obsidian, rotate.isOn(), 100, true, false)) {
                    cap++;
                }
            }

            if (keepFacing.isOn()) {
                mc.player.yaw = yaw;
                mc.player.pitch = pitch;
            }

        if (!keep.isOn()) {
            setState(false);
        }
    }

    private void airPlaceTick(int obsidian) {
        int cap = 0;

        float yaw = mc.player.yaw;
        float pitch = mc.player.pitch;

        if (blockunder.isOn()) {
            if (mc.world.getBlockState(mc.player.getBlockPos().down()).getMaterial().isReplaceable()) {
                WorldUtils.place(mc.player.getBlockPos().down(), Hand.MAIN_HAND, obsidian, rotate.isOn(), 100, true, true);
                cap++;
            }

        }

        for (BlockPos b : new BlockPos[] {
                mc.player.getBlockPos().north(), mc.player.getBlockPos().east(),
                mc.player.getBlockPos().south(), mc.player.getBlockPos().west() }) {

            if (cap >= (int) bpt.getNumber()) {
                return;
            }

            if (WorldUtils.place(b, Hand.MAIN_HAND, obsidian, rotate.isOn(), 100, true, true)) {
                cap++;
            }
        }

        if (keepFacing.isOn()) {
            mc.player.yaw = yaw;
            mc.player.pitch = pitch;
        }

        if (!keep.isOn()) {
            setState(false);
        }
    }

    public boolean isSurrounded() {
        ArrayList<BlockPos> surroundBlocks = new ArrayList<>();
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

    @Override
    public int getKeybind() {
        return keybind.getKey();
    }

    @Override
    public void setKeybind(int keybind) {
        this.keybind.setKey(keybind);
    }
}
