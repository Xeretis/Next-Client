package me.lor3mipsum.next.api.util.entity;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.GameMode;

@SuppressWarnings("ALL")
public class FakePlayerEntity extends OtherClientPlayerEntity {

    private static MinecraftClient mc = MinecraftClient.getInstance();

    public FakePlayerEntity(String name, float health, float absorption, boolean copyInv) {
        super(mc.world, new GameProfile(mc.player.getUuid(), name));

        copyPositionAndRotation(mc.player);

        headYaw = mc.player.headYaw;
        bodyYaw = mc.player.bodyYaw;

        Byte playerModel = mc.player.getDataTracker().get(PlayerEntity.PLAYER_MODEL_PARTS);
        dataTracker.set(PlayerEntity.PLAYER_MODEL_PARTS, playerModel);

        getAttributes().setFrom(mc.player.getAttributes());

        capeX = getX();
        capeY = getY();
        capeZ = getZ();

        setHealth(health);
        setAbsorptionAmount(absorption);

        if (copyInv) inventory.clone(mc.player.inventory);

        spawn();
    }
    private void spawn() {
        removed = false;
        mc.world.addEntity(getEntityId(), this);
    }

    public void despawn() {
        mc.world.removeEntity(getEntityId());
        removed = true;
    }
}
