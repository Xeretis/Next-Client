package me.lor3mipsum.next.client.impl.modules.client;

import me.lor3mipsum.next.Main;
import me.lor3mipsum.next.client.core.module.Category;
import me.lor3mipsum.next.client.core.module.Module;
import me.lor3mipsum.next.client.core.module.annotation.Mod;

@Mod(name = "HudEditor", description = "You can edit your hud here", category = Category.CLIENT)
public class HudEditor extends Module {
    @Override
    public void onEnable() {
        Main.clickGui.enterHUDEditor();
        setEnabled(false);
    }
}
