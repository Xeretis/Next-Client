package me.lor3mipsum.next.client.impl.commands;

import me.lor3mipsum.next.Main;
import me.lor3mipsum.next.api.util.player.ChatUtils;
import me.lor3mipsum.next.client.core.command.Command;
import me.lor3mipsum.next.client.core.command.annotation.Cmd;
import net.minecraft.util.Formatting;

import java.util.List;

@Cmd(name = "help", description = "Shows you some info about other commands", aliases = {"h"})
public class Help extends Command {

    @Override
    public void run(String alias, String[] args) {
        List<Command> commands = Main.commandManager.getCommands();

        if (args.length < 1) {
            ChatUtils.commandInfo(this, "Commands:");
            for (Command command : commands) {
                StringBuilder msg = new StringBuilder("[" + Formatting.WHITE);

                List<String> aliases = command.getNameAndAliases();
                for (String element : aliases) {
                    msg.append(element).append(", ");
                }

                msg = new StringBuilder(msg.substring(0, msg.length() - 2));
                msg.append(Formatting.GRAY + "] - " + command.getDescription());

                ChatUtils.commandInfo(this, msg.toString());
            }
        } else if (args.length == 1) {
            Command cmd = commands.stream().filter(c -> c.getNameAndAliases().contains(args[0])).findFirst().orElse(null);;

            StringBuilder msg = new StringBuilder("[" + Formatting.WHITE);

            List<String> aliases = cmd.getNameAndAliases();
            for (String element : aliases) {
                msg.append(element).append(", ");
            }

            msg = new StringBuilder(msg.substring(0, msg.length() - 2));
            msg.append(Formatting.GRAY + "] - " + cmd.getDescription());

            ChatUtils.commandInfo(this, msg.toString());
        } else
            ChatUtils.commandError(this, "Usage: " + Main.prefix + alias + " [<command>]");
    }

}
