package me.lor3mipsum.next.client.impl.commands;

import me.lor3mipsum.next.Main;
import me.lor3mipsum.next.api.util.client.KeyboardUtils;
import me.lor3mipsum.next.api.util.player.ChatUtils;
import me.lor3mipsum.next.client.core.command.Command;
import me.lor3mipsum.next.client.core.command.annotation.Cmd;
import me.lor3mipsum.next.client.core.module.Module;
import net.minecraft.util.Formatting;

@Cmd(name = "bind", description = "Bind or unbind (null) modules with this")
public class Bind extends Command {
    @Override
    public void run(String alias, String[] args) {
        if(args.length != 2) {
            ChatUtils.commandError(this, "Usage: " + Main.prefix + alias + " <module> <prefix>");
            return;
        }

        Module mod = Main.moduleManager.getModule(args[0], false);

        if (mod == null) {
            ChatUtils.commandError(this, Formatting.WHITE + args[0] + Formatting.RED + " isn't a valid module");
            return;
        }

        int key = KeyboardUtils.getKeyFromName(args[1]);

        if (key == -2) {
            ChatUtils.commandError(this, Formatting.WHITE + args[1] + Formatting.RED + " isn't a valid key");
            return;
        }

        mod.setBind(key);

        ChatUtils.commandInfo(this, "Successfully set the bind of " + Formatting.WHITE + mod.getName() + Formatting.GRAY + " to " + Formatting.WHITE + args[1]);
    }
}
