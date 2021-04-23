package me.lor3mipsum.next.client.impl.commands;

import me.lor3mipsum.next.Next;
import me.lor3mipsum.next.client.command.Command;
import me.lor3mipsum.next.client.command.CommandException;
import me.lor3mipsum.next.client.utils.ChatUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.Vec3d;

public class Nbt extends Command {
    public Nbt() {
        super("nbt", "Gives you the nbt data of the held item");
    }

    @Override
    public void run(String alias, String[] args) {
        if (args.length > 0)
            throw new CommandException("Usage: " + Next.prefix + alias);

        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        ItemStack stack = player.inventory.getMainHandStack();
        if(stack.isEmpty())
            throw new CommandException("You must hold an item");

        CompoundTag tag = stack.getTag();
        String nbt = tag == null ? "null" : tag.asString();

        ChatUtils.info("NBT: (highlight)%s", nbt);
    }
}
