package me.lor3mipsum.next.client.utils.player;

import me.lor3mipsum.next.mixininterface.IVec3d;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

public class PlayerUtils {

    private static final MinecraftClient mc = MinecraftClient.getInstance();

    public static boolean canSeeEntity(Entity entity) {
        Vec3d vec1 = new Vec3d(0, 0, 0);
        Vec3d vec2 = new Vec3d(0, 0, 0);

        ((IVec3d) vec1).set(mc.player.getX(), mc.player.getY() + mc.player.getStandingEyeHeight(), mc.player.getZ());
        ((IVec3d) vec2).set(entity.getX(), entity.getY(), entity.getZ());
        boolean canSeeFeet = mc.world.raycast(new RaycastContext(vec1, vec2, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, mc.player)).getType() == HitResult.Type.MISS;

        ((IVec3d) vec2).set(entity.getX(), entity.getY() + entity.getStandingEyeHeight(), entity.getZ());
        boolean canSeeEyes = mc.world.raycast(new RaycastContext(vec1, vec2, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, mc.player)).getType() == HitResult.Type.MISS;

        return canSeeFeet || canSeeEyes;
    }

    public static float[] calculateAngle(Vec3d target) {
        Vec3d eyesPos = new Vec3d(mc.player.getX(), mc.player.getY() + mc.player.getEyeHeight(mc.player.getPose()), mc.player.getZ());

        double dX = target.x - eyesPos.x;
        double dY = (target.y - eyesPos.y) * -1.0D;
        double dZ = target.z - eyesPos.z;

        double dist = MathHelper.sqrt(dX * dX + dZ * dZ);

        return new float[]{(float) MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(dZ, dX)) - 90.0D), (float) MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(dY, dist)))};
    }
}
