package me.lor3mipsum.next.api.event.player;

import me.lor3mipsum.next.api.event.NextEvent;
import net.minecraft.item.ItemStack;

public class FinishUsingItemEvent extends NextEvent {
    public ItemStack itemStack;

    public FinishUsingItemEvent(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public FinishUsingItemEvent(ItemStack itemStack, Era era) {
        super(era);
        this.itemStack = itemStack;
    }
}
