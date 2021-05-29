package me.lor3mipsum.next.api.util.world;

import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

public class WorldUtils {

    private static MinecraftClient mc = MinecraftClient.getInstance();

    public static boolean doesBoxTouchBlock(Box box, Block block) {
        for (int x = (int) Math.floor(box.minX); x < Math.ceil(box.maxX); x++)
            for (int y = (int) Math.floor(box.minY); y < Math.ceil(box.maxY); y++)
                for (int z = (int) Math.floor(box.minZ); z < Math.ceil(box.maxZ); z++)
                    if (mc.world.getBlockState(new BlockPos(x, y, z)).getBlock() == block)
                        return true;

        return false;
    }

}
