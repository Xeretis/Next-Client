package me.lor3mipsum.next.api.util.render;

import me.lor3mipsum.next.api.util.misc.MathUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryStack;

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

    public Vec3d to2D(Vec3d worldPos) {
        double minX = worldPos.getX() - mc.getEntityRenderDispatcher().camera.getPos().x;
        double minY = worldPos.getY() - mc.getEntityRenderDispatcher().camera.getPos().y;
        double minZ = worldPos.getZ() - mc.getEntityRenderDispatcher().camera.getPos().z;
        Vec3d bound = new Vec3d(minX, minY, minZ);
        Vec3d twoD = to2D(bound.x, bound.y, bound.z);
        return new Vec3d(twoD.x, twoD.y, twoD.z);
    }

    private Vec3d to2D(double x, double y, double z) {
        int displayHeight = mc.getWindow().getHeight();
        org.joml.Vector3f screenCoords = new org.joml.Vector3f();
        int[] viewport = new int[4];
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer modelView = stack.mallocFloat(16);
            FloatBuffer projection = stack.mallocFloat(16);
            GL11.glGetFloatv(GL11.GL_MODELVIEW_MATRIX, modelView);
            GL11.glGetFloatv(GL11.GL_PROJECTION_MATRIX, projection);
            GL11.glGetIntegerv(GL11.GL_VIEWPORT, viewport);
            new org.joml.Matrix4f(projection).mul(new org.joml.Matrix4f(modelView)).project((float) x, (float) y, (float) z, viewport, screenCoords);
        }
        return new Vec3d(screenCoords.x / mc.getWindow().getScaleFactor(), (displayHeight - screenCoords.y) / mc.getWindow().getScaleFactor(), screenCoords.z);
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
