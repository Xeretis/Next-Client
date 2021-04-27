package me.lor3mipsum.next.client.impl.modules.hud;

import com.lukflug.panelstudio.Context;
import com.lukflug.panelstudio.Interface;
import com.lukflug.panelstudio.hud.HUDComponent;
import com.lukflug.panelstudio.theme.Theme;
import me.lor3mipsum.next.client.impl.settings.NumberSetting;
import me.lor3mipsum.next.client.module.Category;
import me.lor3mipsum.next.client.module.HudModule;
import me.lor3mipsum.next.client.utils.render.RenderUtils;
import net.minecraft.item.ItemStack;

import java.awt.*;

public class InventoryViewer extends HudModule {

    public NumberSetting scale = new NumberSetting("Scale", 1.5, 0.1, 10, 0.1);

    public InventoryViewer() {
        super("InventoryViewer", "Renders the contents of your inventory on your hud", new Point(572, 452), Category.HUD);
    }

    @Override
    public void populate(Theme theme) {
        component = new InventoryViewerComponent(theme);
    }

    private class InventoryViewerComponent extends HUDComponent {
        public InventoryViewerComponent(Theme theme) {
            super(getName(), theme.getPanelRenderer(), InventoryViewer.this.position);
        }

        @Override
        public void render (Context context) {
            for (int row = 0; row < 3; row++) {
                for (int i = 0; i < 9; i++) {
                    ItemStack stack = getStack(9 + row * 9 + i);
                    if (stack == null) continue;

                    RenderUtils.drawItem(stack, (int) (this.position.x + (8 + i * 18) * scale.getNumber()), (int) (this.position.y + (7 + row * 18) * scale.getNumber()), scale.getNumber(), true);
                }
            }
        }

        private ItemStack getStack(int i) {
            return mc.player.inventory.getStack(i);
        }

        @Override
        public int getWidth (Interface inter) {
            return (int) (176 * scale.getNumber());
        }

        @Override
        public void getHeight (Context context) {
            context.setHeight((int) (67 * scale.getNumber()));
        }
    }
}
