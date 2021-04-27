package me.lor3mipsum.next.client.impl.modules.hud;

import com.lukflug.panelstudio.Context;
import com.lukflug.panelstudio.Interface;
import com.lukflug.panelstudio.hud.HUDComponent;
import com.lukflug.panelstudio.theme.Theme;
import com.mojang.blaze3d.systems.RenderSystem;
import me.lor3mipsum.next.client.impl.settings.BooleanSetting;
import me.lor3mipsum.next.client.impl.settings.ModeSetting;
import me.lor3mipsum.next.client.impl.settings.NumberSetting;
import me.lor3mipsum.next.client.module.Category;
import me.lor3mipsum.next.client.module.HudModule;
import me.lor3mipsum.next.client.utils.render.RenderUtils;
import net.minecraft.item.ItemStack;

import java.awt.*;

public class ArmorHud extends HudModule {
    public BooleanSetting flipOrder = new BooleanSetting("Flip", false);
    public ModeSetting orientation = new ModeSetting("Orientation", "Vertical", "Vertical", "Horizontal");
    public NumberSetting scale = new NumberSetting("Scale", 1.5, 1, 5, 0.5);

    public ArmorHud() {
        super("ArmorHud", "Renders the durability of your armor on your hud", new Point(927, 192), Category.HUD);
    }

    @Override
    public void populate(Theme theme) {
        component = new ArmorHudComponent(theme);
    }

    private class ArmorHudComponent extends HUDComponent {
        public ArmorHudComponent(Theme theme) {
            super(getName(), theme.getPanelRenderer(), ArmorHud.this.position);
        }

        @Override
        public void render (Context context) {
            double armorX;
            double armorY;

            int slot = flipOrder.isOn() ? 0 : 3;
            for (int position = 0; position < 4; position++) {
                ItemStack itemStack = getItem(slot);

                RenderSystem.pushMatrix();
                RenderSystem.scaled(scale.getNumber(), scale.getNumber(), 1);

                if (orientation.getMode() == "Vertical") {
                    armorX = this.position.x / scale.getNumber();
                    armorY = this.position.y / scale.getNumber() + position * 18;
                } else {
                    armorX = this.position.x / scale.getNumber() + position * 18;
                    armorY = this.position.y / scale.getNumber();
                }

                RenderUtils.drawItem(itemStack, (int) armorX, (int) armorY, (itemStack.isDamageable()));

                RenderSystem.popMatrix();

                if (flipOrder.isOn()) slot++;
                else slot--;
            }
        }

        private ItemStack getItem(int i) {
            return mc.player.inventory.getArmorStack(i);
        }

        @Override
        public int getWidth (Interface inter) {
            switch (orientation.getMode()) {
                case "Vertical":
                    return (int) (16 * scale.getNumber());
                case "Horizontal":
                    return (int) (16 * scale.getNumber() * 4 + 2 * 4);
                default:
                    return 0;
            }
        }

        @Override
        public void getHeight (Context context) {
            switch (orientation.getMode()) {
                case "Vertical":
                    context.setHeight((int) (16 * scale.getNumber() * 4 + 2 * 4));
                    break;
                case "Horizontal":
                    context.setHeight((int) (16 * scale.getNumber()));
                    break;
            }
        }
    }
}
