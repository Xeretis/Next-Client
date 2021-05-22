package me.lor3mipsum.next.client.impl.commands;

import me.lor3mipsum.next.Main;
import me.lor3mipsum.next.api.util.player.ChatUtils;
import me.lor3mipsum.next.client.core.command.Command;
import me.lor3mipsum.next.client.core.command.annotation.Cmd;
import me.lor3mipsum.next.client.core.module.Module;
import net.minecraft.util.Formatting;

@Cmd(name = "drawn", description = "You can toggle the drawn status of module with this")
public class Drawn extends Command {

    @Override
    public void run(String alias, String[] args) {
        if(args.length != 2) {
            ChatUtils.commandError(this, "Usage: " + Main.prefix + alias + " <module> <true/false/toggle>");
            return;
        }

        Module mod = Main.moduleManager.getModule(args[0], false);

        if (mod == null) {
            ChatUtils.commandError(this, Formatting.WHITE + args[0] + Formatting.RED + " isn't a valid module");
            return;
        }

        if (!(args[1].equalsIgnoreCase("true") || args[1].equalsIgnoreCase("false") || args[1].equalsIgnoreCase("toggle"))) {
            ChatUtils.commandError(this, Formatting.WHITE + args[1] + Formatting.RED + " isn't a valid drawn value");
            return;
        }

        if (args[1].equalsIgnoreCase("toggle")) {
            mod.setDrawn(!mod.getDrawn());
            args[1] = Boolean.toString(mod.getDrawn());
        } else
            mod.setDrawn(Boolean.parseBoolean(args[1]));



        ChatUtils.commandInfo(this, "Successfully set the drawn status of " + Formatting.WHITE + mod.getName() + Formatting.GRAY + " to " + Formatting.WHITE + args[1]);
    }

}
