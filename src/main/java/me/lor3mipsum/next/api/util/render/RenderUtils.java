package me.lor3mipsum.next.api.util.render;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import me.lor3mipsum.next.api.util.client.FontUtils;
import me.lor3mipsum.next.api.util.misc.MathUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import org.lwjgl.system.MemoryStack;

import java.awt.*;
import java.nio.FloatBuffer;

public class RenderUtils {

    private static final MinecraftClient mc = MinecraftClient.getInstance();

    public static MatrixStack matrixFrom(double x, double y, double z) {
        MatrixStack matrix = new MatrixStack();

        Camera camera = mc.gameRenderer.getCamera();
        matrix.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(camera.getPitch()));
        matrix.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(camera.getYaw() + 180.0F));

        matrix.translate(x - camera.getPos().x, y - camera.getPos().y, z - camera.getPos().z);

        return matrix;
    }

    public static MatrixStack draw2DItem(double x, double y, double z, double offX, double offY, double scale, ItemStack item) {
        MatrixStack matrix = matrixFrom(x, y, z);

        Camera camera = mc.gameRenderer.getCamera();
        matrix.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(-camera.getYaw()));
        matrix.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(camera.getPitch()));

        matrix.scale((float) scale, (float) scale, 0.001f);
        matrix.translate(offX, offY, 0);

        if (item.isEmpty())
            return matrix;

        matrix.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(180f));

        mc.getBufferBuilders().getEntityVertexConsumers().draw();

        DiffuseLighting.disableGuiDepthLighting();
        GL11.glDepthFunc(GL11.GL_ALWAYS);
        mc.getItemRenderer().renderItem(item, ModelTransformation.Mode.GUI, 0xF000F0,
                OverlayTexture.DEFAULT_UV, matrix, mc.getBufferBuilders().getEntityVertexConsumers());

        mc.getBufferBuilders().getEntityVertexConsumers().draw();
        GL11.glDepthFunc(GL11.GL_LEQUAL);

        matrix.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(-180f));

        return matrix;
    }
}
