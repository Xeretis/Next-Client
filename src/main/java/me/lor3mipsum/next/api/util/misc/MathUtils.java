package me.lor3mipsum.next.api.util.misc;

public class MathUtils {
    public static float clamp(float value, float min, float max) {
        if (value < min) return min;
        if (value > max) return max;
        return value;
    }
}
