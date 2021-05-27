package me.lor3mipsum.next.api.event.game;

import me.lor3mipsum.next.api.event.NextEvent;
import net.minecraft.util.Identifier;

public class RenderOverlayEvent extends NextEvent {
    public Identifier texture;
    public float opacity;

    public RenderOverlayEvent(Identifier texture, float opacity) {
        this.texture = texture;
        this.opacity = opacity;
    }

    public RenderOverlayEvent(Identifier texture, float opacity, Era era) {
        super(era);
        this.texture = texture;
        this.opacity = opacity;
    }
}
