package me.lor3mipsum.next.client.impl.modules.combat;

import me.lor3mipsum.next.client.event.EventTarget;
import me.lor3mipsum.next.client.impl.events.TickEvent;
import me.lor3mipsum.next.client.impl.settings.BooleanSetting;
import me.lor3mipsum.next.client.impl.settings.KeybindSetting;
import me.lor3mipsum.next.client.module.Category;
import me.lor3mipsum.next.client.module.Module;
import me.lor3mipsum.next.client.utils.ChatUtils;
import me.lor3mipsum.next.client.utils.player.InventoryUtils;
import me.lor3mipsum.next.client.utils.world.WorldUtils;
import net.minecraft.block.Blocks;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import org.lwjgl.glfw.GLFW;

public class AntiAnvil extends Module {
    public BooleanSetting airplace = new BooleanSetting("AirPlace", true);
    public BooleanSetting rotate = new BooleanSetting("Rotate", true);
    public KeybindSetting keybind = new KeybindSetting(GLFW.GLFW_KEY_UNKNOWN);

    public AntiAnvil() {
        super("AntiAnvil", "Prevents you being anviled", Category.COMBAT);
    }

    @Override
    public void onEnable() {
        if (InventoryUtils.findItemInHotbar(Items.OBSIDIAN) == -1)
            ChatUtils.moduleError(this, "No obsidian found in hotbar, you may wanna get some of that");
    }

    @EventTarget
    private void onTick(TickEvent.Post event) {
        for (int i = 2; i <= mc.interactionManager.getReachDistance() + 2; i++) {
            if (mc.world.getBlockState(mc.player.getBlockPos().add(0, i, 0)).getBlock() == Blocks.ANVIL && mc.world.getBlockState(mc.player.getBlockPos().add(0, i - 1, 0)).isAir()) {
                if (WorldUtils.place(
                        mc.player.getBlockPos().add(0, i - 2, 0),
                        Hand.MAIN_HAND,
                        InventoryUtils.findItemInHotbar(Items.OBSIDIAN),
                        rotate.isOn(),
                        15,
                        true,
                        airplace.isOn()
                )) break;
            }
        }
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
