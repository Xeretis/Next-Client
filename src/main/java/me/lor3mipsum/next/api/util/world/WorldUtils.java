package me.lor3mipsum.next.api.util.world;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

public class WorldUtils {

    private static MinecraftClient mc = MinecraftClient.getInstance();

    public static Vec3d getLegitLookPos(BlockPos pos, Direction dir, boolean raycast, int res) {
        return getLegitLookPos(new Box(pos), dir, raycast, res, 0.01);
    }

    public static Vec3d getLegitLookPos(Box box, Direction dir, boolean raycast, int res, double extrude) {
        Vec3d eyePos = new Vec3d(mc.player.getX(), mc.player.getEyeY(), mc.player.getZ());
        Vec3d blockPos = new Vec3d(box.minX, box.minY, box.minZ).add(
                (dir == Direction.WEST ? -extrude : dir.getOffsetX() * box.getXLength() + extrude),
                (dir == Direction.DOWN ? -extrude : dir.getOffsetY() * box.getYLength() + extrude),
                (dir == Direction.NORTH ? -extrude : dir.getOffsetZ() * box.getZLength() + extrude));

        for (double i = 0; i <= 1; i += 1d / (double) res) {
            for (double j = 0; j <= 1; j += 1d / (double) res) {
                Vec3d lookPos = blockPos.add(
                        (dir.getAxis() == Direction.Axis.X ? 0 : i * box.getXLength()),
                        (dir.getAxis() == Direction.Axis.Y ? 0 : dir.getAxis() == Direction.Axis.Z ? j * box.getYLength() : i * box.getYLength()),
                        (dir.getAxis() == Direction.Axis.Z ? 0 : j * box.getZLength()));

                if (eyePos.distanceTo(lookPos) > 4.55)
                    continue;

                if (raycast) {
                    if (mc.world.raycast(new RaycastContext(eyePos, lookPos,
                            RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, mc.player)).getType() == HitResult.Type.MISS) {
                        return lookPos;
                    }
                } else {
                    return lookPos;
                }
            }
        }

        return null;
    }

}
