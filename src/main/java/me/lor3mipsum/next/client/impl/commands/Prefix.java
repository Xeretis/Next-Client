package me.lor3mipsum.next.client.impl.commands;

import me.lor3mipsum.next.Main;
import me.lor3mipsum.next.api.util.player.ChatUtils;
import me.lor3mipsum.next.client.core.command.Command;
import me.lor3mipsum.next.client.core.command.annotation.Cmd;
import net.minecraft.util.Formatting;

@Cmd(name = "prefix", description = "You can change the client prefix with this (. by default)", aliases = {"p"})
public class Prefix extends Command {

    @Override
    public void run(String alias, String[] args) {
        if(args.length != 1) {
            ChatUtils.commandError(this, "Usage: " + Main.prefix + alias + " <prefix>");
        }

        Main.prefix = args[0];

        ChatUtils.commandInfo(this, "Successfully set prefix to " + Formatting.WHITE + args[0]);
    }

}
