package me.lor3mipsum.next.api.event.player;

import me.lor3mipsum.next.api.event.NextEvent;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;

public class InteractItemEvent extends NextEvent {
    public Hand hand;
    public ActionResult toReturn;

    public InteractItemEvent(Hand hand) {
        this.hand = hand;
    }

    public InteractItemEvent(Hand hand, Era era) {
        super(era);
        this.hand = hand;
    }
}
