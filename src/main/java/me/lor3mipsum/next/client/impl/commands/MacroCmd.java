package me.lor3mipsum.next.client.impl.commands;

import me.lor3mipsum.next.Main;
import me.lor3mipsum.next.api.util.client.KeyboardUtils;
import me.lor3mipsum.next.api.util.player.ChatUtils;
import me.lor3mipsum.next.client.core.command.Command;
import me.lor3mipsum.next.client.core.command.annotation.Cmd;
import me.lor3mipsum.next.client.core.command.macro.Macro;
import net.minecraft.util.Formatting;

import java.util.Arrays;
import java.util.List;

@Cmd(name = "macro", description = "Allows you to bind commands to keys")
public class MacroCmd extends Command {

    @Override
    public void run(String alias, String[] args) {
        if(args.length < 1) {
            ChatUtils.commandError(this, "Usage: " + Main.prefix + alias + " <add/remove/list> [<add/remove ? name>] [<add ? key>] [<add ? command>]");
            return;
        }

        if(args[0].equalsIgnoreCase("list")) {
            List<Macro> macros = Main.macroManager.getMacros();
            if (macros.size() != 0) {
                for (Macro macro : macros)
                    ChatUtils.info(macro.getName() + ": " + KeyboardUtils.getKeyName(macro.getKey()) + " - " + macro.getCommand());
            } else
                ChatUtils.info("No macros registered yet");
        } else if (args[0].equalsIgnoreCase("add")) {
            if (args.length < 4) {
                ChatUtils.commandError(this, "Usage: " + Main.prefix + alias + " <add/remove/list> [<add/remove ? name>] [<add ? key>] [<add ? command>]");
                return;
            }

            int key = KeyboardUtils.getKeyFromName(args[2]);

            if (key == -2) {
                ChatUtils.commandError(this, "Unknown key: " + args[2]);
                return;
            }

            String[] commandArray = Arrays.copyOfRange(args, 3, args.length);
            String command = String.join(" ", commandArray);

            Main.macroManager.addMacro(new Macro(args[1], key, command));
            ChatUtils.commandInfo(this, "Successfully added macro " + Formatting.WHITE + args[1]);
        } else if (args[0].equalsIgnoreCase("remove")) {
            if (args.length != 2) {
                ChatUtils.commandError(this, "Usage: " + Main.prefix + alias + " <add/remove/list> [<add/remove ? name>] [<add ? key>] [<add ? command>]");
                return;
            }

            Macro macro = Main.macroManager.getMacro(args[1]);

            if(macro == null) {
                ChatUtils.commandError(this, "Couldn't find macro " + Formatting.WHITE + args[1]);
                return;
            }

            Main.macroManager.removeMacro(macro);

            ChatUtils.commandInfo(this, "Successfully removed macro " + Formatting.WHITE + args[1]);
        } else
            ChatUtils.commandError(this, "Usage: " + Main.prefix + alias + " <add/remove/list> [<add/remove ? name>] [<add ? key>] [<add ? command>]");
    }

}
