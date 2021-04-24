package me.lor3mipsum.next.client.impl.modules.hud;

import com.lukflug.panelstudio.Context;
import com.lukflug.panelstudio.Interface;
import com.lukflug.panelstudio.hud.HUDComponent;
import com.lukflug.panelstudio.theme.Theme;
import me.lor3mipsum.next.Next;
import me.lor3mipsum.next.client.impl.settings.NumberSetting;
import me.lor3mipsum.next.client.module.Category;
import me.lor3mipsum.next.client.module.HudModule;
import me.lor3mipsum.next.client.utils.FontUtils;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;

import java.awt.*;

public class PlayerModel extends HudModule {
    public NumberSetting scale = new NumberSetting("Scale", 1.5, 1, 5, 0.5);

    public PlayerModel() {
        super("PlayerViewModel", "It's just a smaller version of you", new Point(-12, 12), Category.HUD);
    }

    @Override
    public void populate(Theme theme) {
        component = new PlayerModelComponent(theme);
    }

    private class PlayerModelComponent extends HUDComponent {
        public PlayerModelComponent (Theme theme) {
            super(getName(), theme.getPanelRenderer(), PlayerModel.this.position);
        }

        @Override
        public void render (Context context) {
            super.render(context);
            PlayerEntity player = mc.player;
            float yaw = MathHelper.wrapDegrees(player.prevYaw + (player.yaw - player.prevYaw) * mc.getTickDelta());
            float pitch = player.pitch;
            InventoryScreen.drawEntity((this.position.x + (getWidth(Next.INSTANCE.clickGui.guiInterface) / 2)), (int) (this.position.y + (66 * scale.getNumber())), (int) (30 * scale.getNumber()), -yaw, -pitch, player);
        }

        @Override
        public int getWidth (Interface inter) {
            if ((int) (25 * scale.getNumber()) + 20 < FontUtils.getStringWidth("PlayerViewModel"))
                return FontUtils.getStringWidth("PlayerViewModel");
            return (int) (25 * scale.getNumber()) + 20;
        }

        @Override
        public void getHeight (Context context) {
            context.setHeight((int) (66 * scale.getNumber()));
        }
    }
}
