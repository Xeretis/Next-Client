package me.lor3mipsum.next.api.util.world;

import me.lor3mipsum.next.Main;
import me.lor3mipsum.next.api.event.game.GameJoinedEvent;
import me.lor3mipsum.next.api.event.network.PacketReciveEvent;
import me.lor3mipsum.next.api.util.misc.MathUtils;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listenable;
import me.zero.alpine.listener.Listener;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;

import java.util.Arrays;

public class TpsUtils implements Listenable {
    public static TpsUtils INSTANCE = new TpsUtils();

    private final float[] tickRates = new float[20];
    private int nextIndex = 0;
    private long timeLastTimeUpdate = -1;
    private long timeGameJoined;

    private TpsUtils() {
        Main.EVENT_BUS.subscribe(this);
    }

    @EventHandler
    private Listener<PacketReciveEvent> onPacketReceive = new Listener<>(event -> {
        if (event.packet instanceof WorldTimeUpdateS2CPacket) {
            if (timeLastTimeUpdate != -1L) {
                float timeElapsed = (float) (System.currentTimeMillis() - timeLastTimeUpdate) / 1000.0F;
                tickRates[(nextIndex % tickRates.length)] = MathUtils.clamp(20.0f / timeElapsed, 0.0f, 20.0f);
                nextIndex += 1;
            }
            timeLastTimeUpdate = System.currentTimeMillis();
        }
    });

    @EventHandler
    private Listener<GameJoinedEvent> onGameJoined = new Listener<>(event -> {
        Arrays.fill(tickRates, 0);
        nextIndex = 0;
        timeLastTimeUpdate = -1;
        timeGameJoined = System.currentTimeMillis();
    });

    public float getTickRate() {
        if (!(MinecraftClient.getInstance() != null && MinecraftClient.getInstance().world != null && MinecraftClient.getInstance().player != null)) return 0;
        if (System.currentTimeMillis() - timeGameJoined < 4000) return 20;

        float numTicks = 0.0f;
        float sumTickRates = 0.0f;
        for (float tickRate : tickRates) {
            if (tickRate > 0.0f) {
                sumTickRates += tickRate;
                numTicks += 1.0f;
            }
        }
        return MathUtils.clamp(sumTickRates / numTicks, 0.0f, 20.0f);
    }

    public float getTimeSinceLastTick() {
        if (System.currentTimeMillis() - timeGameJoined < 4000) return 0;
        return (System.currentTimeMillis() - timeLastTimeUpdate) / 1000f;
    }
}
