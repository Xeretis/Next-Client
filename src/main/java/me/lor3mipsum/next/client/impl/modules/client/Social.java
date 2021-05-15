package me.lor3mipsum.next.client.impl.modules.client;

import me.lor3mipsum.next.client.core.module.Category;
import me.lor3mipsum.next.client.core.module.Module;
import me.lor3mipsum.next.client.core.module.annotation.Mod;
import me.lor3mipsum.next.client.core.setting.SettingSeparator;
import me.lor3mipsum.next.client.impl.settings.BooleanSetting;

@Mod(name = "Social", description = "Allows you to change your social settings/ turn it off completely", category = Category.CLIENT, enabled = true)
public class Social extends Module {
    public SettingSeparator typeSep = new SettingSeparator("Types");

    public BooleanSetting friends = new BooleanSetting("Friends", true);
    public BooleanSetting enemies = new BooleanSetting("Enemies", true);

    public SettingSeparator levelSep = new SettingSeparator("Levels");

    public BooleanSetting ignoreFriendLevel = new BooleanSetting("Ignore Friend Level", false);
    public BooleanSetting ignoreEnemyLevel = new BooleanSetting("Ignore Enemy Level", false);
}
