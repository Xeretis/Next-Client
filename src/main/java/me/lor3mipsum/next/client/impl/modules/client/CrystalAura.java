package me.lor3mipsum.next.client.impl.modules.client;

import me.lor3mipsum.next.client.impl.settings.BooleanSetting;
import me.lor3mipsum.next.client.impl.settings.KeybindSetting;
import me.lor3mipsum.next.client.impl.settings.ModeSetting;
import me.lor3mipsum.next.client.impl.settings.NumberSetting;
import me.lor3mipsum.next.client.module.Category;
import org.lwjgl.glfw.GLFW;
import me.lor3mipsum.next.client.module.Module;

public class CrystalAura extends Module{

    public BooleanSetting place = new BooleanSetting("Place", true);
    public BooleanSetting breaks = new BooleanSetting("Break", true);
    public NumberSetting breakAttempts = new NumberSetting("BreakAttempts", 2, 1, 6, 1);

    public NumberSetting hitRange = new NumberSetting("HitRange", 5.2, 1, 6, 0.1);
    public NumberSetting placeRange = new NumberSetting("PlaceRange", 5.2, 1, 6, 0.1);
    public NumberSetting wallRange = new NumberSetting("WallRange", 3, 1, 6, 0.1);

    public NumberSetting placeDelay = new NumberSetting("PlaceDelay", 0, 0, 10, 1);
    public NumberSetting breakDelay = new NumberSetting("BreakDelay", 2, 0, 10, 1);

    public NumberSetting minPlaceDmg = new NumberSetting("MinPlaceDmg", 8, 0, 36, 1);
    public NumberSetting minBreakDmg = new NumberSetting("MinBreakDmg", 6, 0, 36, 1);
    public NumberSetting maxSelfDmg = new NumberSetting("MaxSelfDmg", 6, 0, 36, 1);

    public ModeSetting rotate = new ModeSetting("Rotate", "Server", "Server", "Client", "None");

    public BooleanSetting autoSwitch = new BooleanSetting("AutoSwitch", true);
    public BooleanSetting antiSuicide = new BooleanSetting("AntiSuicide", true);

    public BooleanSetting fastMode = new BooleanSetting("FastMode", true);

    public BooleanSetting newPlace = new BooleanSetting("NewPlace", true);

    public BooleanSetting facePlace = new BooleanSetting("FacePlace", false);
    public NumberSetting facePlaceHealth = new NumberSetting("FacePlaceHp", 8, 0, 36, 1);

    public BooleanSetting armorBreaker = new BooleanSetting("ArmorBreaker", true);
    public NumberSetting armorPct = new NumberSetting("ArmorPct", 10, 0, 100, 1);

    public BooleanSetting stopWhileMining = new BooleanSetting("StopWhileMining", false);

    public KeybindSetting keybind = new KeybindSetting(GLFW.GLFW_KEY_UNKNOWN);

    public CrystalAura() {
        super("CrystalAura", "Yes", Category.COMBAT);
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
