package me.lor3mipsum.next.client.utils.world;

import me.lor3mipsum.next.client.event.EventManager;
import me.lor3mipsum.next.client.event.EventTarget;
import me.lor3mipsum.next.client.impl.events.GameJoinEvent;
import me.lor3mipsum.next.client.impl.events.RecivePacketEvent;
import me.lor3mipsum.next.client.utils.Utils;
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;

import java.util.Arrays;

public class TpsUtils {
    public static TpsUtils INSTANCE = new TpsUtils();

    private final float[] tickRates = new float[20];
    private int nextIndex = 0;
    private long timeLastTimeUpdate = -1;
    private long timeGameJoined;

    private TpsUtils() {
        EventManager.register(this);
    }

    @EventTarget
    private void onReceivePacket(RecivePacketEvent event) {
        if (event.getPacket() instanceof WorldTimeUpdateS2CPacket) {
            if (timeLastTimeUpdate != -1L) {
                float timeElapsed = (float) (System.currentTimeMillis() - timeLastTimeUpdate) / 1000.0F;
                tickRates[(nextIndex % tickRates.length)] = Utils.clamp(20.0f / timeElapsed, 0.0f, 20.0f);
                nextIndex += 1;
            }
            timeLastTimeUpdate = System.currentTimeMillis();
        }
    }

    @EventTarget
    private void onGameJoined(GameJoinEvent event) {
        Arrays.fill(tickRates, 0);
        nextIndex = 0;
        timeLastTimeUpdate = -1;
        timeGameJoined = System.currentTimeMillis();
    }

    public float getTickRate() {
        if (!Utils.canUpdate()) return 0;
        if (System.currentTimeMillis() - timeGameJoined < 4000) return 20;

        float numTicks = 0.0f;
        float sumTickRates = 0.0f;
        for (float tickRate : tickRates) {
            if (tickRate > 0.0f) {
                sumTickRates += tickRate;
                numTicks += 1.0f;
            }
        }
        return Utils.clamp(sumTickRates / numTicks, 0.0f, 20.0f);
    }

    public float getTimeSinceLastTick() {
        if (System.currentTimeMillis() - timeGameJoined < 4000) return 0;
        return (System.currentTimeMillis() - timeLastTimeUpdate) / 1000f;
    }
}
