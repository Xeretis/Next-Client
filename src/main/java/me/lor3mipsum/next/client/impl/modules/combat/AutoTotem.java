package me.lor3mipsum.next.client.impl.modules.combat;

import me.lor3mipsum.next.Next;
import me.lor3mipsum.next.client.event.EventTarget;
import me.lor3mipsum.next.client.impl.events.TickEvent;
import me.lor3mipsum.next.client.impl.settings.BooleanSetting;
import me.lor3mipsum.next.client.impl.settings.KeybindSetting;
import me.lor3mipsum.next.client.module.Category;
import me.lor3mipsum.next.client.module.Module;
import me.lor3mipsum.next.client.utils.player.InventoryUtils;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import org.lwjgl.glfw.GLFW;

public class AutoTotem extends Module {

    public BooleanSetting inInv = new BooleanSetting("InInv", false);
    public KeybindSetting keybind = new KeybindSetting(GLFW.GLFW_KEY_UNKNOWN);

    public Offhand offhand = Next.INSTANCE.moduleManager.getModule(Offhand.class);

    public AutoTotem() {
        super("AutoTotem", "no.", Category.COMBAT);
    }

    @EventTarget
    private void onTick(TickEvent.Pre event) {
        if (mc.player == null)
            return;

        if (mc.currentScreen instanceof InventoryScreen && !inInv.isOn()) return;

        InventoryUtils.FindItemResult selected = InventoryUtils.findItemWithCount(Items.TOTEM_OF_UNDYING);

        if (offhand.elytra.isOn() && mc.player.inventory.getArmorStack(2).getItem() == Items.ELYTRA)
            if (mc.player.getOffHandStack().getItem() != Items.TOTEM_OF_UNDYING)
                InventoryUtils.move().from(selected.slot).toOffhand();

        if ((!offhand.isOn() || (mc.player.getHealth() + mc.player.getAbsorptionAmount() < offhand.health.getNumber())) && selected.count > 0)
            if (mc.player.getOffHandStack().getItem() != Items.TOTEM_OF_UNDYING)
                InventoryUtils.move().from(selected.slot).toOffhand();
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
