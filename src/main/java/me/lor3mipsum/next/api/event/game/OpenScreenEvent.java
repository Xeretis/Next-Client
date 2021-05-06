package me.lor3mipsum.next.api.event.game;

import me.lor3mipsum.next.api.event.NextEvent;
import net.minecraft.client.gui.screen.Screen;

public class OpenScreenEvent extends NextEvent {
    public Screen screen;

    public OpenScreenEvent(Screen screen) {
        this.screen = screen;
    }

    public OpenScreenEvent(Screen screen, Era era) {
        super(era);
        this.screen = screen;
    }
}
