package me.lor3mipsum.next.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import me.lor3mipsum.next.Main;
import me.lor3mipsum.next.api.event.NextEvent;
import me.lor3mipsum.next.api.event.entity.BlockEntityRenderAllEvent;
import me.lor3mipsum.next.api.event.entity.EntityRenderAllEvent;
import me.lor3mipsum.next.api.event.entity.EntityRenderEvent;
import me.lor3mipsum.next.api.event.world.WorldRenderEvent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.profiler.Profiler;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V"))
    private void render_swap(Profiler profiler, String string) {
        switch (string) {
            case "entities":
                Main.EVENT_BUS.post(new EntityRenderAllEvent(NextEvent.Era.PRE));
                break;
            case "blockentities":
                Main.EVENT_BUS.post(new EntityRenderAllEvent(NextEvent.Era.POST));
                Main.EVENT_BUS.post(new BlockEntityRenderAllEvent(NextEvent.Era.PRE));
                break;
            case "destroyProgress":
                Main.EVENT_BUS.post(new BlockEntityRenderAllEvent(NextEvent.Era.POST));
        }
    }

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void render_head(MatrixStack matrices, float tickDelta, long limitTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, Matrix4f matrix4f, CallbackInfo info) {
        WorldRenderEvent event = new WorldRenderEvent(tickDelta, NextEvent.Era.PRE);

        Main.EVENT_BUS.post(event);

        if (event.isCancelled()) {
            info.cancel();
        }
    }

    @Inject(method = "render", at = @At("RETURN"))
    private void render_return(MatrixStack matrices, float tickDelta, long limitTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, Matrix4f matrix4f, CallbackInfo info) {
        RenderSystem.clear(GL11.GL_DEPTH_BUFFER_BIT, MinecraftClient.IS_SYSTEM_MAC);

        WorldRenderEvent event = new WorldRenderEvent(tickDelta, NextEvent.Era.POST);

        Main.EVENT_BUS.post(event);
    }
}
