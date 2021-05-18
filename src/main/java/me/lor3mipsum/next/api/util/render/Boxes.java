package me.lor3mipsum.next.api.util.render;

import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class Boxes {

    public static Vec3d getMinVec(Box box) {
        return new Vec3d(box.minX, box.minY, box.minZ);
    }

    public static Vec3d getMaxVec(Box box) {
        return new Vec3d(box.maxX, box.maxY, box.maxZ);
    }

    public static Box moveToZero(Box box) {
        return box.offset(getMinVec(box).negate());
    }

    public static double getCornerLength(Box box) {
        return getMinVec(box).distanceTo(getMaxVec(box));
    }

    public static double getAxisLength(Box box, Direction.Axis axis) {
        return box.getMax(axis) - box.getMin(axis);
    }

}
