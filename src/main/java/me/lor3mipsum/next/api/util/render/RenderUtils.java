package me.lor3mipsum.next.api.util.render;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import me.lor3mipsum.next.api.util.render.color.LineColor;
import me.lor3mipsum.next.api.util.render.color.QuadColor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.*;
import org.apache.commons.lang3.ArrayUtils;

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

    public static void drawItem(ItemStack itemStack, int x, int y, boolean overlay) {
        RenderSystem.disableLighting();
        RenderSystem.disableDepthTest();
        DiffuseLighting.enable();
        MinecraftClient.getInstance().getItemRenderer().renderGuiItemIcon(itemStack, x, y);
        if (overlay) MinecraftClient.getInstance().getItemRenderer().renderGuiItemOverlay(MinecraftClient.getInstance().textRenderer, itemStack, x, y, null);
        DiffuseLighting.disable();
        DiffuseLighting.disable();
        RenderSystem.enableDepthTest();
    }

    public static void drawItem(ItemStack itemStack, int x, int y, double scale, boolean overlay) {
        RenderSystem.pushMatrix();
        RenderSystem.scaled(scale, scale, 1);
        drawItem(itemStack, (int) (x / scale), (int) (y / scale), overlay);
        RenderSystem.popMatrix();
    }

    public static void drawBoxBoth(BlockPos blockPos, QuadColor color, float lineWidth, Direction... excludeDirs) {
        drawBoxBoth(new Box(blockPos), color, lineWidth, excludeDirs);
    }

    public static void drawBoxBoth(Box box, QuadColor color, float lineWidth, Direction... excludeDirs) {
        QuadColor outlineColor = color.clone();
        outlineColor.overwriteAlpha(255);

        drawBoxBoth(box, color, outlineColor, lineWidth, excludeDirs);
    }

    public static void drawBoxBoth(BlockPos blockPos, QuadColor fillColor, QuadColor outlineColor, float lineWidth, Direction... excludeDirs) {
        drawBoxBoth(new Box(blockPos), fillColor, outlineColor, lineWidth, excludeDirs);
    }

    public static void drawBoxBoth(Box box, QuadColor fillColor, QuadColor outlineColor, float lineWidth, Direction... excludeDirs) {
        drawBoxFill(box, fillColor, excludeDirs);
        drawBoxOutline(box, outlineColor, lineWidth, excludeDirs);
    }

    public static void drawBoxFill(BlockPos blockPos, QuadColor color, Direction... excludeDirs) {
        drawBoxFill(new Box(blockPos), color, excludeDirs);
    }

    public static void drawBoxFill(Box box, QuadColor color, Direction... excludeDirs) {
        setup();

        MatrixStack matrix = matrixFrom(box.minX, box.minY, box.minZ);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        // Fill
        buffer.begin(7, VertexFormats.POSITION_COLOR);
        Vertexer.vertexBoxQuads(matrix, buffer, Boxes.moveToZero(box), color, excludeDirs);
        tessellator.draw();

        cleanup();
    }

    public static void drawBoxOutline(BlockPos blockPos, QuadColor color, float lineWidth, Direction... excludeDirs) {
        drawBoxOutline(new Box(blockPos), color, lineWidth, excludeDirs);
    }

    public static void drawBoxOutline(Box box, QuadColor color, float lineWidth, Direction... excludeDirs) {
        setup();

        MatrixStack matrix = matrixFrom(box.minX, box.minY, box.minZ);

        Tessellator tessellator = RenderSystem.renderThreadTesselator();
        BufferBuilder buffer = tessellator.getBuffer();

        // Outline
        RenderSystem.disableCull();
        RenderSystem.lineWidth(lineWidth);

        buffer.begin(3, VertexFormats.POSITION_COLOR);
        Vertexer.vertexBoxLines(matrix, buffer, Boxes.moveToZero(box), color, excludeDirs);
        tessellator.draw();

        RenderSystem.enableCull();
        cleanup();
    }

    public static void drawLine(double x1, double y1, double z1, double x2, double y2, double z2, LineColor color, float width) {
        setup();

        MatrixStack matrix = matrixFrom(x1, y1, z1);

        Tessellator tessellator = RenderSystem.renderThreadTesselator();
        BufferBuilder buffer = tessellator.getBuffer();

        // Line
        RenderSystem.disableDepthTest();
        RenderSystem.disableCull();
        RenderSystem.lineWidth(width);

        buffer.begin(3, VertexFormats.POSITION_COLOR);
        Vertexer.vertexLine(matrix, buffer, 0f, 0f, 0f, (float) (x2 - x1), (float) (y2 - y1), (float) (z2 - z1), color);
        tessellator.draw();

        RenderSystem.enableCull();
        RenderSystem.enableDepthTest();
        cleanup();
    }

    public static void setup() {
//        RenderSystem.enableBlend();
//        RenderSystem.defaultBlendFunc();
//        RenderSystem.disableAlphaTest();
//        RenderSystem.shadeModel(GL11.GL_SMOOTH);
//        RenderSystem.disableTexture();
        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO);
        RenderSystem.depthMask(MinecraftClient.isFabulousGraphicsOrBetter());
        RenderSystem.enableCull();
    }

    public static void cleanup() {
//        RenderSystem.shadeModel(GL11.GL_FLAT);
//        RenderSystem.enableAlphaTest();
//        RenderSystem.disableBlend();
//        RenderSystem.enableTexture();
        RenderSystem.enableTexture();
        RenderSystem.disableCull();
        RenderSystem.disableBlend();
        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(true);
    }

    public static class Vertexer {

        private static final int CULL_BACK = 0;
        private static final int CULL_FRONT = 1;
        private static final int CULL_NONE = 2;

        public static void vertexBoxQuads(MatrixStack matrix, VertexConsumer vertexConsumer, Box box, QuadColor quadColor, Direction... excludeDirs) {
            float x1 = (float) box.minX;
            float y1 = (float) box.minY;
            float z1 = (float) box.minZ;
            float x2 = (float) box.maxX;
            float y2 = (float) box.maxY;
            float z2 = (float) box.maxZ;

            int cullMode = excludeDirs.length == 0 ? CULL_BACK : CULL_NONE;

            if (!ArrayUtils.contains(excludeDirs, Direction.DOWN)) {
                vertexQuad(matrix, vertexConsumer, x1, y1, z1, x2, y1, z1, x2, y1, z2, x1, y1, z2, cullMode, quadColor);
            }

            if (!ArrayUtils.contains(excludeDirs, Direction.WEST)) {
                vertexQuad(matrix, vertexConsumer, x1, y1, z2, x1, y2, z2, x1, y2, z1, x1, y1, z1, cullMode, quadColor);
            }

            if (!ArrayUtils.contains(excludeDirs, Direction.EAST)) {
                vertexQuad(matrix, vertexConsumer, x2, y1, z1, x2, y2, z1, x2, y2, z2, x2, y1, z2, cullMode, quadColor);
            }

            if (!ArrayUtils.contains(excludeDirs, Direction.NORTH)) {
                vertexQuad(matrix, vertexConsumer, x1, y1, z1, x1, y2, z1, x2, y2, z1, x2, y1, z1, cullMode, quadColor);
            }

            if (!ArrayUtils.contains(excludeDirs, Direction.SOUTH)) {
                vertexQuad(matrix, vertexConsumer, x2, y1, z2, x2, y2, z2, x1, y2, z2, x1, y1, z2, cullMode, quadColor);
            }

            if (!ArrayUtils.contains(excludeDirs, Direction.UP)) {
                vertexQuad(matrix, vertexConsumer, x1, y2, z2, x2, y2, z2, x2, y2, z1, x1, y2, z1, cullMode, quadColor);
            }
        }

        public static void vertexQuad(MatrixStack matrix, VertexConsumer vertexConsumer, float x1, float y1, float z1, float x2, float y2, float z2, float x3, float y3, float z3, float x4, float y4, float z4, int cullMode, QuadColor quadColor) {
            int[] color = quadColor.getAllColors();

            if (cullMode != CULL_FRONT) {
                vertexConsumer.vertex(matrix.peek().getModel(), x1, y1, z1).color(color[0], color[1], color[2], color[3]).next();
                vertexConsumer.vertex(matrix.peek().getModel(), x2, y2, z2).color(color[4], color[5], color[6], color[7]).next();
                vertexConsumer.vertex(matrix.peek().getModel(), x3, y3, z3).color(color[8], color[9], color[10], color[11]).next();
                vertexConsumer.vertex(matrix.peek().getModel(), x4, y4, z4).color(color[12], color[13], color[14], color[15]).next();
            }

            if (cullMode != CULL_BACK) {
                vertexConsumer.vertex(matrix.peek().getModel(), x4, y4, z4).color(color[0], color[1], color[2], color[3]).next();
                vertexConsumer.vertex(matrix.peek().getModel(), x3, y3, z3).color(color[4], color[5], color[6], color[7]).next();
                vertexConsumer.vertex(matrix.peek().getModel(), x2, y2, z2).color(color[8], color[9], color[10], color[11]).next();
                vertexConsumer.vertex(matrix.peek().getModel(), x1, y1, z1).color(color[12], color[13], color[14], color[15]).next();
            }
        }

        public static void vertexBoxLines(MatrixStack matrix, VertexConsumer vertexConsumer, Box box, QuadColor quadColor, Direction... excludeDirs) {
            float x1 = (float) box.minX;
            float y1 = (float) box.minY;
            float z1 = (float) box.minZ;
            float x2 = (float) box.maxX;
            float y2 = (float) box.maxY;
            float z2 = (float) box.maxZ;

            boolean exDown = ArrayUtils.contains(excludeDirs, Direction.DOWN);
            boolean exWest = ArrayUtils.contains(excludeDirs, Direction.WEST);
            boolean exEast = ArrayUtils.contains(excludeDirs, Direction.EAST);
            boolean exNorth = ArrayUtils.contains(excludeDirs, Direction.NORTH);
            boolean exSouth = ArrayUtils.contains(excludeDirs, Direction.SOUTH);
            boolean exUp = ArrayUtils.contains(excludeDirs, Direction.UP);

            int[] color = quadColor.getAllColors();

            if (!exDown) {
                vertexLine(matrix, vertexConsumer, x1, y1, z1, x2, y1, z1, LineColor.single(color[0], color[1], color[2], color[3]));
                vertexLine(matrix, vertexConsumer, x2, y1, z1, x2, y1, z2, LineColor.single(color[4], color[5], color[6], color[7]));
                vertexLine(matrix, vertexConsumer, x2, y1, z2, x1, y1, z2, LineColor.single(color[8], color[9], color[10], color[11]));
                vertexLine(matrix, vertexConsumer, x1, y1, z2, x1, y1, z1, LineColor.single(color[12], color[13], color[14], color[15]));
            }

            if (!exWest) {
                if (exDown) vertexLine(matrix, vertexConsumer, x1, y1, z1, x1, y1, z2, LineColor.single(color[0], color[1], color[2], color[3]));
                vertexLine(matrix, vertexConsumer, x1, y1, z2, x1, y2, z2, LineColor.single(color[4], color[5], color[6], color[7]));
                vertexLine(matrix, vertexConsumer, x1, y1, z1, x1, y2, z1, LineColor.single(color[8], color[9], color[10], color[11]));
                if (exUp) vertexLine(matrix, vertexConsumer, x1, y2, z1, x1, y2, z2, LineColor.single(color[12], color[13], color[14], color[15]));
            }

            if (!exEast) {
                if (exDown) vertexLine(matrix, vertexConsumer, x2, y1, z1, x2, y1, z2, LineColor.single(color[0], color[1], color[2], color[3]));
                vertexLine(matrix, vertexConsumer, x2, y1, z2, x2, y2, z2, LineColor.single(color[4], color[5], color[6], color[7]));
                vertexLine(matrix, vertexConsumer, x2, y1, z1, x2, y2, z1, LineColor.single(color[8], color[9], color[10], color[11]));
                if (exUp) vertexLine(matrix, vertexConsumer, x2, y2, z1, x2, y2, z2, LineColor.single(color[12], color[13], color[14], color[15]));
            }

            if (!exNorth) {
                if (exDown) vertexLine(matrix, vertexConsumer, x1, y1, z1, x2, y1, z1, LineColor.single(color[0], color[1], color[2], color[3]));
                if (exEast) vertexLine(matrix, vertexConsumer, x2, y1, z1, x2, y2, z1, LineColor.single(color[4], color[5], color[6], color[7]));
                if (exWest) vertexLine(matrix, vertexConsumer, x1, y1, z1, x1, y2, z1, LineColor.single(color[8], color[9], color[10], color[11]));
                if (exUp) vertexLine(matrix, vertexConsumer, x1, y2, z1, x2, y2, z1, LineColor.single(color[12], color[13], color[14], color[15]));
            }

            if (!exSouth) {
                if (exDown) vertexLine(matrix, vertexConsumer, x1, y1, z2, x2, y1, z2, LineColor.single(color[0], color[1], color[2], color[3]));
                if (exEast) vertexLine(matrix, vertexConsumer, x2, y1, z2, x2, y2, z2, LineColor.single(color[4], color[5], color[6], color[7]));
                if (exWest) vertexLine(matrix, vertexConsumer, x1, y1, z2, x1, y2, z2, LineColor.single(color[8], color[9], color[10], color[11]));
                if (exUp) vertexLine(matrix, vertexConsumer, x1, y2, z2, x2, y2, z2, LineColor.single(color[12], color[13], color[14], color[15]));
            }

            if (!exUp) {
                vertexLine(matrix, vertexConsumer, x1, y2, z1, x2, y2, z1, LineColor.single(color[0], color[1], color[2], color[3]));
                vertexLine(matrix, vertexConsumer, x2, y2, z1, x2, y2, z2, LineColor.single(color[4], color[5], color[6], color[7]));
                vertexLine(matrix, vertexConsumer, x2, y2, z2, x1, y2, z2, LineColor.single(color[8], color[9], color[10], color[11]));
                vertexLine(matrix, vertexConsumer, x1, y2, z2, x1, y2, z1, LineColor.single(color[12], color[13], color[14], color[15]));
            }
        }

        public static void vertexLine(MatrixStack matrix, VertexConsumer vertexConsumer, float x1, float y1, float z1, float x2, float y2, float z2, LineColor lineColor) {
            Matrix4f model = matrix.peek().getModel();
            Matrix3f normal = matrix.peek().getNormal();

            Vector3f normalVec = getNormal(normal, x1, y1, z1, x2, y2, z2);

            int[] color1 = lineColor.getColor(x1, y1, z1, 0);
            int[] color2 = lineColor.getColor(x2, y2, z2, 1);

            vertexConsumer.vertex(model, x1, y1, z1).color(color1[0], color1[1], color1[2], 0).normal(normal, normalVec.getX(), normalVec.getY(), normalVec.getZ()).next();
            vertexConsumer.vertex(model, x1, y1, z1).color(color1[0], color1[1], color1[2], color1[3]).normal(normal, normalVec.getX(), normalVec.getY(), normalVec.getZ()).next();
            vertexConsumer.vertex(model, x2, y2, z2).color(color2[0], color2[1], color2[2], color2[3]).normal(normal, normalVec.getX(), normalVec.getY(), normalVec.getZ()).next();
            vertexConsumer.vertex(model, x2, y2, z2).color(color1[0], color1[1], color1[2], 0).normal(normal, normalVec.getX(), normalVec.getY(), normalVec.getZ()).next();
        }

        public static Vector3f getNormal(Matrix3f normal, float x1, float y1, float z1, float x2, float y2, float z2) {
            float xNormal = x2 - x1;
            float yNormal = y2 - y1;
            float zNormal = z2 - z1;
            float normalSqrt = MathHelper.sqrt(xNormal * xNormal + yNormal * yNormal + zNormal * zNormal);

            return new Vector3f(xNormal / normalSqrt, yNormal / normalSqrt, zNormal / normalSqrt);
        }
    }
}
