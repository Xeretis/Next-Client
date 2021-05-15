package me.lor3mipsum.next.api.util.misc;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.Random;

public class MathUtils {

    private static final Random rng = new Random();

    public static float clamp(float value, float min, float max) {
        if (value < min) return min;
        if (value > max) return max;
        return value;
    }

    public int getRandom(int cap) {
        return rng.nextInt(cap);
    }

    public int getRandom(int floor, int cap) {
        return floor + rng.nextInt(cap - floor + 1);
    }

    public int randInt(int min, int max) {
        return rng.nextInt(max - min + 1) + min;
    }

    public double randDouble(double min, double max) {
        return min + rng.nextDouble() * (max - min);
    }

    public float randFloat(float min, float max) {
        return min + rng.nextFloat() * (max - min);
    }

    public Vec3d getVec3dOf(Entity entity) {
        return new Vec3d(entity.getX(), entity.getY(), entity.getZ());
    }

    public Vec3d getVec3dOf(BlockPos blockPos) {
        return new Vec3d(blockPos.getX(), blockPos.getY(), blockPos.getZ());
    }
}
