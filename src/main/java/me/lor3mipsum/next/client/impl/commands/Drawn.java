package me.lor3mipsum.next.client.impl.commands;

import me.lor3mipsum.next.Next;
import me.lor3mipsum.next.client.command.Command;
import me.lor3mipsum.next.client.command.CommandException;
import me.lor3mipsum.next.client.module.Module;
import me.lor3mipsum.next.client.utils.ChatUtils;
import net.minecraft.util.Formatting;

public class Drawn extends Command {
    public Drawn() {
        super("drawn", "You can toggle modules in the array list with this");
    }

    @Override
    public void run(String alias, String[] args) {
        if (args.length < 1) {
            throw new CommandException("Usage: " + Next.prefix + alias + " <module> [<on/off>]");
        }

        Module mod = Next.INSTANCE.moduleManager.getModule(args[0], false);

        if (mod == null) throw new CommandException("The module '" + args[0] + "' does not exist");

        boolean state = !mod.getDrawn();

        if (args.length >= 2) {
            if (args[1].equalsIgnoreCase("on")) state = true;
            else if (args[1].equalsIgnoreCase("off")) state = false;
            else throw new CommandException("Usage: " + Next.prefix + alias + " <module> [<on/off>]");
        }

        mod.setDrawn(state);

        ChatUtils.info("The drawn property of " + mod.getName() + " has been " + (state ? Formatting.GREEN : Formatting.RED) + (state ? "enabled" : "disabled"));
    }
}
