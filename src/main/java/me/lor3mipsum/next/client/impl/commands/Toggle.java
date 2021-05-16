package me.lor3mipsum.next.client.impl.commands;

import me.lor3mipsum.next.Main;
import me.lor3mipsum.next.api.util.player.ChatUtils;
import me.lor3mipsum.next.client.core.command.Command;
import me.lor3mipsum.next.client.core.command.annotation.Cmd;
import me.lor3mipsum.next.client.core.module.Module;
import net.minecraft.util.Formatting;

@Cmd(name = "toggle", description = "Toggles a module", aliases = {"t"})
public class Toggle extends Command {

    @Override
    public void run(String alias, String[] args) {
        if (args.length < 1) {
            ChatUtils.commandError(this, "Usage: " + Main.prefix + alias + " <module> [<on/off>]");
            return;
        }

        Module mod = Main.moduleManager.getModule(args[0], false);

        if (mod == null) {
            ChatUtils.commandError(this, "The module '" + args[0] + "' doesn't exist");
            return;
        }

        boolean state = !mod.getEnabled();

        if (args.length >= 2) {
            if (args[1].equalsIgnoreCase("on")) state = true;
            else if (args[1].equalsIgnoreCase("off")) state = false;
            else {
                ChatUtils.commandError(this, "Usage: " + Main.prefix + alias + " <module> [<on/off>]");
                return;
            }
        }

        mod.setEnabled(state);

        ChatUtils.commandInfo(this, mod.getName() + " has been " + (state ? Formatting.GREEN : Formatting.RED) + (state ? "enabled" : "disabled"));
    }

}
