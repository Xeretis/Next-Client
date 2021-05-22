package me.lor3mipsum.next.client.impl.commands;

import me.lor3mipsum.next.Main;
import me.lor3mipsum.next.api.util.player.ChatUtils;
import me.lor3mipsum.next.client.core.command.Command;
import me.lor3mipsum.next.client.core.command.annotation.Cmd;
import me.lor3mipsum.next.client.core.module.Module;
import me.lor3mipsum.next.client.core.setting.Setting;
import me.lor3mipsum.next.client.core.setting.SettingSeparator;
import net.minecraft.util.Formatting;

@Cmd(name = "list", description = "It lists all the modules or all the settings of a module")
public class ListCmd extends Command {

    @Override
    public void run(String alias, String[] args) {
        if (args.length < 1) {
            ChatUtils.commandError(this, "Usage: " + Main.prefix + alias + " <modules/settings> [<settings ? module>]");
            return;
        }

        if (args[0].equalsIgnoreCase("modules")) {
            StringBuilder msg = new StringBuilder();

            for (Module module : Main.moduleManager.getModules())
                msg.append(module.getName()).append(", ");

            msg = new StringBuilder(msg.substring(0, msg.length() - 2));

            ChatUtils.commandInfo(this, msg.toString());

        } else if (args[0].equalsIgnoreCase("settings")) {

            if (args.length != 2) {
                ChatUtils.commandError(this, "Usage: " + Main.prefix + alias + " <modules/settings> [<settings ? module>]");
                return;
            }

            Module mod = Main.moduleManager.getModule(args[1], false);

            if (mod == null) {
                ChatUtils.commandError(this, Formatting.WHITE + args[1] + Formatting.RED + " isn't a valid module");
                return;
            }

            StringBuilder msg = new StringBuilder();

            for (Setting setting : Main.settingManager.getAllSettingsFrom(mod.getName())) {
                if (setting instanceof SettingSeparator)
                    continue;
                msg.append(setting.getName()).append(", ");
            }

            msg = new StringBuilder(msg.substring(0, msg.length() - 2));

            ChatUtils.info(msg.toString());

        } else
            ChatUtils.commandError(this, "Usage: " + Main.prefix + alias + " <modules/settings> [<settings ? module>]");
    }

}
