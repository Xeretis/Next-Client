package me.lor3mipsum.next.client.impl.commands;

import me.lor3mipsum.next.Next;
import me.lor3mipsum.next.client.command.Command;
import me.lor3mipsum.next.client.command.CommandException;
import me.lor3mipsum.next.client.module.Module;
import me.lor3mipsum.next.client.setting.Setting;
import me.lor3mipsum.next.client.setting.SettingManager;
import me.lor3mipsum.next.client.utils.ChatUtils;

public class ListCmd extends Command {
    public ListCmd() {
        super("list", "Lists all modules or all settings of a module");
    }

    @Override
    public void run(String alias, String[] args) {
        if (args.length < 1)
            throw new CommandException("Usage: " + Next.prefix + alias + " <modules/settings> [<settings ? module>]");
        if (args[0].equalsIgnoreCase("modules")) {
            StringBuilder msg = new StringBuilder();
            for (Module module : Next.INSTANCE.moduleManager.getModules())
                msg.append(module.getName()).append(", ");
            msg = new StringBuilder(msg.substring(0, msg.length() - 2));
            ChatUtils.info(msg.toString());
        } else if (args[0].equalsIgnoreCase("settings")) {
            if (args.length != 2)
                throw new CommandException("Usage: " + Next.prefix + alias + " <modules/settings> [<settings ? module>]");
            Module mod = Next.INSTANCE.moduleManager.getModule(args[1], false);

            if (mod == null) throw new CommandException("The module '" + args[1] + "doesn't exist");

            StringBuilder msg = new StringBuilder();
            for (Setting setting : SettingManager.getAllSettingsFrom(mod.getName())) {
                msg.append(setting.getName()).append(", ");
            }
            msg = new StringBuilder(msg.substring(0, msg.length() - 2));
            ChatUtils.info(msg.toString());
        } else
            throw new CommandException("Usage: " + Next.prefix + alias + " <modules/settings> [<settings ? module>]");
    }
}
