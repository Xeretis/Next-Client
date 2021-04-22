package me.lor3mipsum.next.client.impl.commands;

import me.lor3mipsum.next.Next;
import me.lor3mipsum.next.client.command.Command;
import me.lor3mipsum.next.client.command.CommandException;
import me.lor3mipsum.next.client.utils.ChatUtils;

public class Prefix extends Command {

    public Prefix() {
        super("prefix", "Allows you to change the command prefix ('.' by default)", "p");
    }

    @Override
    public void run(String alias, String[] args) {
        if(args.length < 1)
            throw new CommandException("Usage: " + Next.prefix + alias + " <prefix>");

        Next.prefix = args[0];

        ChatUtils.info("Successfully set prefix to (highlight)%s", args[0]);
    }
}
