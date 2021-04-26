package me.lor3mipsum.next.client.impl.modules.player;

import net.minecraft.client.MinecraftClient;

import java.util.HashMap;
import java.util.Map;

public class FakePlayerUtils {
    private static final MinecraftClient mc = MinecraftClient.getInstance();

    private static final Map<FakePlayerEntity, Integer> players = new HashMap<>();
    public static int ID;

    public static void spawnFakePlayer() {
        FakePlayerEntity fakePlayer = new FakePlayerEntity("FakePlayer#" + ID, true, mc.player.getHealth());
        players.put(fakePlayer, ID);
        ID++;
    }

    public static void removeFakePlayer(int id) {
            if (players.isEmpty()) return;

            for (Map.Entry<FakePlayerEntity, Integer> player : players.entrySet()) {
                if (player.getValue() == id) {
                    player.getKey().despawn();
                }
            }
    }

    public static void clearFakePlayers() {
        for (Map.Entry<FakePlayerEntity, Integer> player : players.entrySet()) {
            player.getKey().despawn();
        }
        players.clear();
    }

    public static Map<FakePlayerEntity, Integer> getPlayers() {
        return players;
    }

    public static int getID(FakePlayerEntity entity) {
        return players.getOrDefault(entity, 0);
    }
}
