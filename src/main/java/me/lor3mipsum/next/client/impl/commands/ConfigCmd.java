package me.lor3mipsum.next.client.impl.commands;

import me.lor3mipsum.next.Main;
import me.lor3mipsum.next.api.config.Backup;
import me.lor3mipsum.next.api.config.LoadConfig;
import me.lor3mipsum.next.api.config.SaveConfig;
import me.lor3mipsum.next.api.util.player.ChatUtils;
import me.lor3mipsum.next.client.core.command.Command;
import me.lor3mipsum.next.client.core.command.annotation.Cmd;

import java.io.IOException;

@Cmd(name = "config", description = "Allows you to manage your current config in-game")
public class ConfigCmd extends Command {

    @Override
    public void run(String alias, String[] args) {
        if (args.length < 1) {
            ChatUtils.commandError(this, "Usage: " + Main.prefix + alias + " <save/load/backup>");
            return;
        }

        if (args[0].equalsIgnoreCase("save")) {
            SaveConfig.save();

            Main.LOG.info("Saved the config");

            ChatUtils.commandInfo(this, "Successfully saved the config");

        } else if (args[0].equalsIgnoreCase("load")) {
            LoadConfig.load();

            ChatUtils.commandInfo(this, "Successfully loaded the config");

        } else if (args[0].equalsIgnoreCase("backup")) {
            Backup.backup("Triggered by in-game command");

            Main.LOG.info("Loaded the config");

            ChatUtils.commandInfo(this, "Successfully backed up the config");

        } else
            ChatUtils.commandError(this, "Usage: " + Main.prefix + alias + " <save/load/backup>");
    }

}
