package me.lor3mipsum.next.client.impl.modules.combat;

import me.lor3mipsum.next.client.event.EventTarget;
import me.lor3mipsum.next.client.impl.events.TickEvent;
import me.lor3mipsum.next.client.impl.settings.BooleanSetting;
import me.lor3mipsum.next.client.impl.settings.KeybindSetting;
import me.lor3mipsum.next.client.impl.settings.ModeSetting;
import me.lor3mipsum.next.client.impl.settings.NumberSetting;
import me.lor3mipsum.next.client.module.Category;
import me.lor3mipsum.next.client.module.Module;
import me.lor3mipsum.next.client.utils.player.InventoryUtils;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import org.lwjgl.glfw.GLFW;

public class Offhand extends Module {

    public NumberSetting health = new NumberSetting("Health", 14, 0, 36, 1);
    public ModeSetting mode = new ModeSetting("Mode", "Totem", "Totem", "Gap", "Crap", "Crystal");
    public BooleanSetting elytra = new BooleanSetting("DisableOnElytra", true);
    public BooleanSetting inInv = new BooleanSetting("InInv", false);
    public KeybindSetting keybind = new KeybindSetting(GLFW.GLFW_KEY_UNKNOWN);

    public Offhand() {
        super("Offhand", "Manages your offhand automatically", Category.COMBAT);
    }

    @EventTarget
    private void onTick(TickEvent.Pre event) {
        if (mc.player == null)
            return;

        if (mc.currentScreen instanceof InventoryScreen && !inInv.isOn()) return;

        InventoryUtils.FindItemResult selected = InventoryUtils.findItemWithCount(getItem());

        if (elytra.isOn() && mc.player.inventory.getArmorStack(2).getItem() == Items.ELYTRA)
            return;

        if (mc.player.getHealth() + mc.player.getAbsorptionAmount() > health.getNumber() && selected.count > 0)
            if (mc.player.getOffHandStack().getItem() != getItem())
                InventoryUtils.move().from(selected.slot).toOffhand();
    }

    private Item getItem() {
        Item item;
        switch (mode.getMode()) {
            case "Totem":
                item = Items.TOTEM_OF_UNDYING;
                break;
            case "Crystal":
                item = Items.END_CRYSTAL;
                break;
            case "Gap":
                item = Items.ENCHANTED_GOLDEN_APPLE;
                break;
            case "Crap":
                item = Items.GOLDEN_APPLE;
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + mode.getMode());
        }
        return item;
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
