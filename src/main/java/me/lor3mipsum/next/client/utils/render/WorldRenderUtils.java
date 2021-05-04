package me.lor3mipsum.next.client.utils.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

public class WorldRenderUtils {

    private static final MinecraftClient mc = MinecraftClient.getInstance();

    /**
     * Draws a Text string in the world.
     *
     * @return The used MatrixStack for further use
     */
    public static MatrixStack drawText(String str, double x, double y, double z, double scale) {
        MatrixStack matrix = matrixFrom(x, y, z);

        Camera camera = mc.gameRenderer.getCamera();
        matrix.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(-camera.getYaw()));
        matrix.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(camera.getPitch()));

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        matrix.scale(-0.025f * (float) scale, -0.025f * (float) scale, 1);

        int halfWidth = mc.textRenderer.getWidth(str) / 2;

        int opacity = (int) (MinecraftClient.getInstance().options.getTextBackgroundOpacity(0.25F) * 255.0F) << 24;

        mc.textRenderer.draw(str, -halfWidth, 0f, 553648127, false, matrix.peek().getModel(), mc.getBufferBuilders().getEntityVertexConsumers(), true, opacity, 0xf000f0);
        mc.textRenderer.draw(str, -halfWidth, 0f, -1, false, matrix.peek().getModel(), mc.getBufferBuilders().getEntityVertexConsumers(), false, 0, 0xf000f0);

        RenderSystem.disableBlend();

        return matrix;
    }

    /**
     * Draws a 2D gui items somewhere in the world.
     *
     * @return The used MatrixStack for further use
     */
    public static MatrixStack drawGuiItem(double x, double y, double z, double offX, double offY, double scale, ItemStack item) {
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


    public static MatrixStack matrixFrom(double x, double y, double z) {
        MatrixStack matrix = new MatrixStack();

        Camera camera = mc.gameRenderer.getCamera();
        matrix.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(camera.getPitch()));
        matrix.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(camera.getYaw() + 180.0F));

        matrix.translate(x - camera.getPos().x, y - camera.getPos().y, z - camera.getPos().z);

        return matrix;
    }
}