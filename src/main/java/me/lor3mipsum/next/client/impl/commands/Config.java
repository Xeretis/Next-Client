package me.lor3mipsum.next.client.impl.commands;

import me.lor3mipsum.next.Next;
import me.lor3mipsum.next.client.command.Command;
import me.lor3mipsum.next.client.command.CommandException;
import me.lor3mipsum.next.client.config.ConfigManager;
import me.lor3mipsum.next.client.utils.ChatUtils;

import java.io.IOException;


public class Config extends Command {
    public Config() {
        super("config", "Allows you to save or load a config while in game");
    }

    @Override
    public void run(String alias, String[] args) {
        if (args.length < 1)
            throw new CommandException("Usage: " + Next.prefix + alias + " <save/load>");

        if (args[0].equalsIgnoreCase("save")) {
            try {
                ConfigManager.save();
            } catch (IOException e) {
                throw new CommandException("Failed to save config");
            }
            ChatUtils.info("Successfully saved config");
        } else if (args[0].equalsIgnoreCase("load")) {
            ConfigManager.load();
            ChatUtils.info("Successfully loaded config");
        } else {
            throw new CommandException("Usage: " + Next.prefix + alias + " <save/load>");
        }
    }
}
