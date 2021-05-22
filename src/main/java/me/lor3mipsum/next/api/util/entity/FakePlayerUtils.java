package me.lor3mipsum.next.api.util.entity;

import java.util.ArrayList;
import java.util.List;

public class FakePlayerUtils {
    private static final List<FakePlayerEntity> fakePlayers = new ArrayList<>();

    public static void add(String name, float health, float absorption, boolean copyInv) {
        FakePlayerEntity fakePlayer = new FakePlayerEntity(name, health, absorption, copyInv);
        fakePlayers.add(fakePlayer);
    }

    public static void clear() {
        if (fakePlayers.isEmpty()) return;
        fakePlayers.forEach(FakePlayerEntity::despawn);
        fakePlayers.clear();
    }

    public static List<FakePlayerEntity> getPlayers() {
        return fakePlayers;
    }

    public static int size() {
        return fakePlayers.size();
    }
}
