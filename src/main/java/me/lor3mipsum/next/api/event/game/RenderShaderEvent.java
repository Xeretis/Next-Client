package me.lor3mipsum.next.api.event.game;

import me.lor3mipsum.next.api.event.NextEvent;
import net.minecraft.client.gl.ShaderEffect;

public class RenderShaderEvent extends NextEvent {
    public ShaderEffect effect;

    public RenderShaderEvent(ShaderEffect effect) {
        this.effect = effect;
    }

    public RenderShaderEvent(ShaderEffect effect, Era era) {
        super(era);
        this.effect = effect;
    }
}
