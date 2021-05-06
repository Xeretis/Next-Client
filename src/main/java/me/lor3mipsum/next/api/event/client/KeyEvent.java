package me.lor3mipsum.next.api.event.client;

import me.lor3mipsum.next.api.event.NextEvent;
import me.lor3mipsum.next.api.util.client.KeyboardUtils;

public class KeyEvent extends NextEvent {

    public int key;
    public KeyboardUtils.KeyAction action;

    public KeyEvent(int key, KeyboardUtils.KeyAction action) {
        this.key = key;
        this.action = action;
    }
}
