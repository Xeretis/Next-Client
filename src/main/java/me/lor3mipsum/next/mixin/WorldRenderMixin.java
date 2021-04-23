package me.lor3mipsum.next.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import me.lor3mipsum.next.Next;
import me.lor3mipsum.next.client.event.EventManager;
import me.lor3mipsum.next.client.impl.events.WorldRenderEvent;
import me.lor3mipsum.next.client.impl.modules.render.BreakIndicator;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public class WorldRenderMixin {
    @Shadow
    @Final
    private MinecraftClient client;

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void render_head(MatrixStack matrices, float tickDelta, long limitTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer,
                             LightmapTextureManager lightmapTextureManager, Matrix4f matrix4f, CallbackInfo info) {
        WorldRenderEvent event = new WorldRenderEvent.Pre(tickDelta);
        EventManager.call(event);

        if (event.isCancelled()) {
            info.cancel();
        }
    }

    @Inject(method = "render", at = @At("RETURN"))
    private void render_return(MatrixStack matrices, float tickDelta, long limitTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer,
                               LightmapTextureManager lightmapTextureManager, Matrix4f matrix4f, CallbackInfo info) {
        RenderSystem.clear(GL11.GL_DEPTH_BUFFER_BIT, MinecraftClient.IS_SYSTEM_MAC);
        WorldRenderEvent.Post event = new WorldRenderEvent.Post(tickDelta);
        EventManager.call(event);
    }

    @Inject(method = "setBlockBreakingInfo", at = @At("HEAD"), cancellable = true)
    private void onBlockBreakingInfo(int entityId, BlockPos pos, int stage, CallbackInfo ci) {
        BreakIndicator bi = Next.INSTANCE.moduleManager.getModule(BreakIndicator.class);
        if(!bi.isOn())
            return;

        if(!bi.others.isOn() && entityId != client.player.getEntityId())
            return;

        if (0 <= stage && stage <= 8) {
            BlockBreakingInfo info = new BlockBreakingInfo(entityId, pos);
            info.setStage(stage);
            bi.blocks.put(entityId, info);

            if (bi.noDefault.isOn()) {
                ci.cancel();
            }

        } else {
            bi.blocks.remove(entityId);
        }
    }

    @Inject(method = "removeBlockBreakingInfo", at = @At("TAIL"))
    private void onBlockBreakingInfoRemoval(BlockBreakingInfo info, CallbackInfo ci) {
        BreakIndicator bi = Next.INSTANCE.moduleManager.getModule(BreakIndicator.class);
        if(!bi.isOn())
            return;

        bi.blocks.values().removeIf(info::equals);
    }
}
