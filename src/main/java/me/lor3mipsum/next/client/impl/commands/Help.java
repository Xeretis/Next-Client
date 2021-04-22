package me.lor3mipsum.next.client.impl.commands;

import me.lor3mipsum.next.Next;
import me.lor3mipsum.next.client.command.Command;
import me.lor3mipsum.next.client.command.CommandException;
import me.lor3mipsum.next.client.command.CommandManager;
import me.lor3mipsum.next.client.utils.ChatUtils;
import net.minecraft.util.Formatting;

import java.util.List;
import java.util.NoSuchElementException;

public class Help extends Command {
    public Help() {
        super("help", "Lists all the commands", "h");
    }

    @Override
    public void run(String alias, String[] args) {
        List<Command> commands = CommandManager.getCommands();

        if (args.length < 1) {
            ChatUtils.info("Commands:");
            for (Command command : commands) {
                StringBuilder msg = new StringBuilder("[" + Formatting.WHITE);

                List<String> aliases = command.getNameAndAliases();
                for (String element : aliases) {
                    msg.append(element).append(", ");
                }

                msg = new StringBuilder(msg.substring(0, msg.length() - 2));
                msg.append(Formatting.GRAY + "] - " + command.description);

                ChatUtils.info(msg.toString());
            }
        } else if (args.length == 1) {
            Command command;

            try {
                command = commands.stream().filter(c -> c.getNameAndAliases().contains(args[0])).findFirst().get();
            } catch (NoSuchElementException e) {
                throw new CommandException("Couldn't find command '" + args[0] + "'");
            }

            StringBuilder msg = new StringBuilder("[" + Formatting.WHITE);

            List<String> aliases = command.getNameAndAliases();
            for (String element : aliases) {
                msg.append(element).append(", ");
            }

            msg = new StringBuilder(msg.substring(0, msg.length() - 2));
            msg.append(Formatting.GRAY + "] - " + command.description);

            ChatUtils.info(msg.toString());
        } else
            throw new CommandException("Usage: " + Next.prefix + alias + " [<command>]");
    }
}
