package me.lor3mipsum.next.client.impl.modules.client;

import me.lor3mipsum.next.client.core.module.Category;
import me.lor3mipsum.next.client.core.module.Module;
import me.lor3mipsum.next.client.core.module.annotation.Mod;
import me.lor3mipsum.next.client.core.setting.SettingSeparator;
import me.lor3mipsum.next.client.impl.settings.BooleanSetting;

@Mod(name = "CustomFont", description = "What you are currently looking at", category = Category.CLIENT, enabled = true)
public class CustomFont extends Module {
    public SettingSeparator generalSep = new SettingSeparator("General");
    public BooleanSetting shadow = new BooleanSetting("Shadow", true);
}
