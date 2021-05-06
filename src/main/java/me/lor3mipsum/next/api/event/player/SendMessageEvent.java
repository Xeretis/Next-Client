package me.lor3mipsum.next.api.event.player;

import me.lor3mipsum.next.api.event.NextEvent;

public class SendMessageEvent extends NextEvent {
    public String msg;

    public SendMessageEvent(String msg) {
        this.msg = msg;
    }

    public SendMessageEvent(String msg, Era era) {
        super(era);
        this.msg = msg;
    }
}
