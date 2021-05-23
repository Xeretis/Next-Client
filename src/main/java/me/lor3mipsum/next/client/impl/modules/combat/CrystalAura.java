package me.lor3mipsum.next.client.impl.modules.combat;

import me.lor3mipsum.next.api.util.misc.NextColor;
import me.lor3mipsum.next.client.core.module.Category;
import me.lor3mipsum.next.client.core.module.Module;
import me.lor3mipsum.next.client.core.module.annotation.Mod;
import me.lor3mipsum.next.client.core.setting.SettingSeparator;
import me.lor3mipsum.next.client.impl.settings.*;

@Mod(name = "CrystalAura", description = "Crystals go brrr", category = Category.COMBAT)
public class CrystalAura extends Module {

    public SettingSeparator actionsSep = new SettingSeparator("Actions");

    public BooleanSetting cPlace = new BooleanSetting("Place", true);
    public BooleanSetting cBreak = new BooleanSetting("Break", true);

    public SettingSeparator generalSep = new SettingSeparator("General");

    public EnumSetting<CancelMode> cancelMode = new EnumSetting<>("Cancel Mode", CancelMode.Instant);
    public BooleanSetting antiSuicide = new BooleanSetting("Anti Suicide", true);
    public BooleanSetting antiPop = new BooleanSetting("Anti Pop", false);
    public BooleanSetting oldPlace = new BooleanSetting("Old Place", false);
    public BooleanSetting crystalCheck = new BooleanSetting("Crystal Check", false);

    public SettingSeparator delaysSep = new SettingSeparator("Delays");

    public IntegerSetting breakDelay = new IntegerSetting("Break Delay", 2, 0, 20);
    public IntegerSetting placeDelay = new IntegerSetting("Place Delay", 2, 0, 20);

    public SettingSeparator rangesSep = new SettingSeparator("Ranges");

    public DoubleSetting breakRange = new DoubleSetting("Break Range", 5.0, 0.0, 6.0);
    public DoubleSetting placeRange = new DoubleSetting("Place Range", 5.0, 0.0, 6.0);
    public DoubleSetting wallsBreakRange = new DoubleSetting("Walls Break Range", 3.0, 0.0, 6.0);
    public DoubleSetting wallsPlaceRange = new DoubleSetting("Walls Place Range", 3.0, 0.0, 6.0);

    public SettingSeparator damagesSep = new SettingSeparator("Damages");

    public IntegerSetting minHpPlace = new IntegerSetting("Min Place Dmg", 9, 0, 36);
    public IntegerSetting minHpBreak = new IntegerSetting("Min Break Dmg", 8, 0, 36);
    public IntegerSetting maxSelfDamage = new IntegerSetting("Max Self Dmg", 5, 0, 36);
    public BooleanSetting ignoreSelfDamage = new BooleanSetting("Ignore Self Dmg", false);

    public SettingSeparator rotationsSep = new SettingSeparator("Rotations");

    public BooleanSetting rotate = new BooleanSetting("Rotate", true);
    public BooleanSetting raytrace = new BooleanSetting("Raytrace", false);
    public BooleanSetting resetRotate = new BooleanSetting("Reset Rotations", true);

    public SettingSeparator predictionsSep = new SettingSeparator("Predictions");

    public BooleanSetting targetPredict = new BooleanSetting("Target Predict", true);
    public BooleanSetting selfPredict = new BooleanSetting("Self Predict", false);
    public IntegerSetting targetPredictTicks = new IntegerSetting("Target Predict Ticks", 2, 0, 20);
    public IntegerSetting selfPredictTicks = new IntegerSetting("Self Predict Ticks", 2, 0, 20);

    public SettingSeparator switchingSep = new SettingSeparator("Switching");

    public BooleanSetting autoSwitch = new BooleanSetting("Auto Switch", true);
    public BooleanSetting switchBack = new BooleanSetting("Switch Back", true);
    public BooleanSetting antiWeakness = new BooleanSetting("Anti Weakness", true);

    public SettingSeparator stoppingSep = new SettingSeparator("Stopping");

    public BooleanSetting stopWhileMining = new BooleanSetting("StopWhileMining", false);
    public BooleanSetting stopWhileEating = new BooleanSetting("StopWhileEating", false);

    public SettingSeparator facePlaceSep = new SettingSeparator("FacePlace");

    public BooleanSetting facePlace = new BooleanSetting("FacePlace", true);
    public IntegerSetting facePlaceHp = new IntegerSetting("FacePlace Hp", 10, 0, 36);
    public IntegerSetting facePlaceDelay = new IntegerSetting("FacePlace Delay", 5, 0, 20);

    public SettingSeparator armorBreakerSep = new SettingSeparator("ArmorBreaker");

    public BooleanSetting armorBreaker = new BooleanSetting("ArmorBreaker", true);
    public IntegerSetting armorBreakerPct = new IntegerSetting("ArmorBreaker Pct", 10, 1, 100);

    public SettingSeparator renderSep = new SettingSeparator("Render");

    public ColorSetting sidesColor = new ColorSetting("Sides Color", false, new NextColor(255, 255, 255));
    public ColorSetting linesColor = new ColorSetting("Lines Color", false, new NextColor(255, 255, 255));
    public DoubleSetting lineWidth = new DoubleSetting("Line Width", 2.5, 0.1, 5);
    public BooleanSetting swing = new BooleanSetting("Swing", true);

    public enum CancelMode {
        Sound,
        Instant,
        None
    }

}
