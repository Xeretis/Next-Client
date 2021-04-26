package me.lor3mipsum.next.client.impl.commands;

import me.lor3mipsum.next.Next;
import me.lor3mipsum.next.client.command.Command;
import me.lor3mipsum.next.client.command.CommandException;
import me.lor3mipsum.next.client.utils.ChatUtils;

import java.util.Arrays;

public class Rpc extends Command {
    public static String stateText = "also normal text";
    public static String detailsText = "normal text";

    public Rpc() {
        super("rpc", "Changes the text of the discord rpc");
    }

    @Override
    public void run(String alias, String[] args) {
        if(args.length < 2)
            throw new CommandException("Usage: " + Next.prefix + alias + " <details/state> <text>");

        String text = String.join(" ", Arrays.copyOfRange(args, 1, args.length));

        if (args[0].equalsIgnoreCase("state")) {
            stateText = text;
            ChatUtils.info("State has been set to (highlight)%s", text);
        } else if (args[0].equalsIgnoreCase("details")) {
            detailsText = text;
            ChatUtils.info("Details have been set to (highlight)%s", text);
        } else
            throw new CommandException("Usage: " + Next.prefix + alias + " <details/state> <text>");
    }
}
