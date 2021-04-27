package me.lor3mipsum.next.client.impl.modules.combat;

import me.lor3mipsum.next.client.event.EventTarget;
import me.lor3mipsum.next.client.impl.events.EntityRemovedEvent;
import me.lor3mipsum.next.client.impl.settings.BooleanSetting;
import me.lor3mipsum.next.client.impl.settings.KeybindSetting;
import me.lor3mipsum.next.client.impl.settings.ModeSetting;
import me.lor3mipsum.next.client.impl.settings.NumberSetting;
import me.lor3mipsum.next.client.module.Category;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.glfw.GLFW;
import me.lor3mipsum.next.client.module.Module;

import java.util.concurrent.ConcurrentHashMap;

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

    private final ConcurrentHashMap<EndCrystalEntity, Integer> attacked_crystals = new ConcurrentHashMap<>();

    private final Timer remove_visual_timer = new Timer();
    private final Timer chain_timer = new Timer();

    private String detail_name = null;
    private int detail_hp = 0;

    private BlockPos render_block_init;
    private BlockPos render_block_old;

    private double render_damage_value;

    private float yaw;
    private float pitch;

    private boolean already_attacking = false;
    private boolean place_timeout_flag = false;
    private boolean is_rotating;
    private boolean did_anything;
    private boolean outline;
    private boolean solid;

    private int chain_step = 0;
    private int current_chain_index = 0;
    private int place_timeout;
    private int break_timeout;
    private int break_delay_counter;
    private int place_delay_counter;

    public CrystalAura() {
        super("CrystalAura", "Yes", Category.COMBAT);
    }

    @EventTarget
    private void onEntityRemoved(EntityRemovedEvent event) {
        if(event.getEntity() instanceof EndCrystalEntity)
            attacked_crystals.remove(event.getEntity());
    }

    @Override
    public int getKeybind() {
        return keybind.getKey();
    }

    @Override
    public void setKeybind(int keybind) {
        this.keybind.setKey(keybind);
    }

    private class Timer {
        private long time;

        public Timer() {
            this.time = -1L;
        }

        public boolean passed(final long ms) {
            return this.getTime(System.nanoTime() - this.time) >= ms;
        }

        public void reset() {
            this.time = System.nanoTime();
        }

        public long getTime(final long time) {
            return time / 1000000L;
        }
    }
}
