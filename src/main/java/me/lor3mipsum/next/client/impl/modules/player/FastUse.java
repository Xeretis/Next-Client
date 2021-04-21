package me.lor3mipsum.next.client.impl.modules.player;

import me.lor3mipsum.next.client.event.EventTarget;
import me.lor3mipsum.next.client.impl.events.TickEvent;
import me.lor3mipsum.next.client.impl.settings.KeybindSetting;
import me.lor3mipsum.next.client.impl.settings.ModeSetting;
import me.lor3mipsum.next.client.module.Category;
import me.lor3mipsum.next.client.module.Module;
import me.lor3mipsum.next.mixin.MinecraftClientAccessor;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Items;
import org.lwjgl.glfw.GLFW;

public class FastUse extends Module {
    public KeybindSetting keybind = new KeybindSetting(GLFW.GLFW_KEY_UNKNOWN);
    public ModeSetting mode = new ModeSetting("Mode", "Exp", "Exp", "Blocks", "Both", "All");

    public FastUse() {
        super("FastUse", "You can use stuff faster with this", Category.PLAYER);
    }

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }

    @EventTarget
    public void onTick(TickEvent.Post event) {
        switch (mode.getMode()) {
            case "Exp":
                if(mc.player.getMainHandStack().getItem() == Items.EXPERIENCE_BOTTLE || mc.player.getOffHandStack().getItem() == Items.EXPERIENCE_BOTTLE)
                    ((MinecraftClientAccessor) mc).setItemUseCooldown(0);
                break;
            case "Blocks":
                if(mc.player.getMainHandStack().getItem() instanceof BlockItem || mc.player.getOffHandStack().getItem() instanceof BlockItem)
                    ((MinecraftClientAccessor) mc).setItemUseCooldown(0);
                break;
            case "Both":
                if (((mc.player.getMainHandStack().getItem() == Items.EXPERIENCE_BOTTLE || mc.player.getOffHandStack().getItem() == Items.EXPERIENCE_BOTTLE))
                        || (mc.player.getMainHandStack().getItem() instanceof BlockItem || mc.player.getOffHandStack().getItem() instanceof BlockItem))
                    ((MinecraftClientAccessor) mc).setItemUseCooldown(0);
            case "All":
                ((MinecraftClientAccessor) mc).setItemUseCooldown(0);
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
