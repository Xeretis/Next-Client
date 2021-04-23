package me.lor3mipsum.next.client.impl.commands;

import me.lor3mipsum.next.Next;
import me.lor3mipsum.next.client.command.Command;
import me.lor3mipsum.next.client.command.CommandException;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;

public class Say extends Command {
    public Say() {
        super("say", "Sends a message to the chat even if it starts with the prefix");
    }
    @Override
    public void run(String alias, String[] args) {
        if(args.length < 1)
            throw new CommandException("Usage: " + Next.prefix + alias + " <message>");

        String message = String.join(" ", args);
        ChatMessageC2SPacket packet = new ChatMessageC2SPacket(message);
        MinecraftClient.getInstance().getNetworkHandler().sendPacket(packet);
    }

}
