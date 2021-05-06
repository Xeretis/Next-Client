package me.lor3mipsum.next.api.event.player;

import me.lor3mipsum.next.api.event.NextEvent;
import net.minecraft.item.ItemStack;

public class StoppedUsingItemEvent extends NextEvent {
    public ItemStack itemStack;

    public StoppedUsingItemEvent(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public StoppedUsingItemEvent(ItemStack itemStack, Era era) {
        super(era);
        this.itemStack = itemStack;
    }
}
