package me.lor3mipsum.next.api.event.client;

import me.lor3mipsum.next.api.event.NextEvent;
import me.lor3mipsum.next.api.util.client.KeyboardUtils;

public class MouseEvent extends NextEvent {
    public int button;
    public KeyboardUtils.KeyAction action;

    public MouseEvent(int button, KeyboardUtils.KeyAction action) {
        this.button = button;
        this.action = action;
    }

    public MouseEvent(int button, KeyboardUtils.KeyAction action, Era era) {
        super(era);
        this.button = button;
        this.action = action;
    }
}
