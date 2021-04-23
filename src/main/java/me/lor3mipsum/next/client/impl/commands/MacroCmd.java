package me.lor3mipsum.next.client.impl.commands;

import me.lor3mipsum.next.Next;
import me.lor3mipsum.next.client.command.Command;
import me.lor3mipsum.next.client.command.CommandException;
import me.lor3mipsum.next.client.command.macro.Macro;
import me.lor3mipsum.next.client.command.macro.MacroManager;
import me.lor3mipsum.next.client.utils.ChatUtils;
import me.lor3mipsum.next.client.utils.Utils;
import net.minecraft.client.util.InputUtil;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class MacroCmd extends Command {
    public MacroCmd() {
        super("macro", "Allows you to bind commands to keys");
    }

    @Override
    public void run(String alias, String[] args) {
        if(args.length < 1)
            throw new CommandException("Usage: " + Next.prefix + alias + " <add/remove/list> [<add/remove ? key>] [<add ? command>]");

        if(args[0].equalsIgnoreCase("list")) {
            List<Macro> macros = Next.INSTANCE.macroManager.getMacros();
            if (macros.size() != 0) {
                StringBuilder msg = new StringBuilder();
                for (Macro macro : macros)
                    msg.append(Utils.getKeyName(macro.key)).append(" - ").append(macro.command).append("\n");
                msg = new StringBuilder(msg.substring(0, msg.length() - 1));
                ChatUtils.info(msg.toString());
            } else
                ChatUtils.info("No macros registered yet");
        } else if (args[0].equalsIgnoreCase("add")) {
            if (args.length < 3)
                throw new CommandException("Usage: " + Next.prefix + alias + " <add/remove/list> [<add/remove ? key>] [<add ? command>]");

            int key = Utils.getKeyFromName(args[1]);

            if (key == -2)
                throw new CommandException("Unknown key: " + args[1]);

            if (Next.INSTANCE.macroManager.getKeys().contains(key))
                throw new CommandException("A macro is already registered for that key");

            String[] commandArray = Arrays.copyOfRange(args, 2, args.length);
            String command = String.join(" ", commandArray);

            Next.INSTANCE.macroManager.addMacro(new Macro(key, command));
            ChatUtils.info("Successfully added macro (highlight)%s", args[1]);
        } else if (args[0].equalsIgnoreCase("remove")) {
            if (args.length != 2)
                throw new CommandException("Usage: " + Next.prefix + alias + " <add/remove/list> [<add/remove ? key>] [<add ? command>]");

            int key = Utils.getKeyFromName(args[1]);

            if (key == -2)
                throw new CommandException("Unknown key: " + args[1]);

            Next.INSTANCE.macroManager.removeMacro(key);
            ChatUtils.info("Successfully removed macro (highlight)%s", args[1]);
        } else
            throw new CommandException("Usage: " + Next.prefix + alias + " <add/remove/list> [<add/remove ? key>] [<add ? command>]");
    }
}
