package me.lor3mipsum.next.client.impl.commands;

import me.lor3mipsum.next.Next;
import me.lor3mipsum.next.client.command.Command;
import me.lor3mipsum.next.client.command.CommandException;
import me.lor3mipsum.next.client.impl.settings.*;
import me.lor3mipsum.next.client.module.Module;
import me.lor3mipsum.next.client.setting.Setting;
import me.lor3mipsum.next.client.utils.ChatUtils;
import net.minecraft.client.util.InputUtil;

import java.util.Locale;

public class Set extends Command {
    public Set() {
        super("set", "Allows you to set a specific setting in a module to a certain value");
    }

    @Override
    public void run(String alias, String[] args) {
        if (args.length < 3)
            throw new CommandException("Usage: " + Next.prefix + alias + " <module> <setting> <value>");

        Module mod = Next.INSTANCE.moduleManager.getModule(args[0], false);

        if (mod == null) throw new CommandException("The module '" + args[0] + "' does not exist");

        Setting toSet = Next.INSTANCE.settingManager.get(mod.getName(), args[1]);

        if (toSet == null) throw new CommandException("The module '" + args[0] + "' does not have any setting called '" + args[1] + "'");

        if (toSet instanceof KeybindSetting) {
            int key;
            try {
                key = InputUtil.fromTranslationKey("key.keyboard." + args[2].toLowerCase(Locale.ENGLISH)).getCode();
            } catch (IllegalArgumentException e) {
                if (args[2].toLowerCase(Locale.ENGLISH).startsWith("right")) {
                    try {
                        key = InputUtil.fromTranslationKey("key.keyboard." + args[2].toLowerCase(Locale.ENGLISH).replaceFirst("right", "right.")).getCode();
                    } catch (IllegalArgumentException e1) {
                        throw new CommandException("Unknown key: " + args[2] + " / " + args[2].toLowerCase(Locale.ENGLISH).replaceFirst("right", "right."));
                    }
                } else if (args[2].toLowerCase(Locale.ENGLISH).startsWith("r")) {
                    try {
                        key = InputUtil.fromTranslationKey("key.keyboard." + args[2].toLowerCase(Locale.ENGLISH).replaceFirst("r", "right.")).getCode();
                    } catch (IllegalArgumentException e1) {
                        throw new CommandException("Unknown key: " + args[2] + " / " + args[2].toLowerCase(Locale.ENGLISH).replaceFirst("r", "right."));
                    }
                } else
                    throw new CommandException("Unknown key: " + args[2]);
            }
            ((KeybindSetting) toSet).setKey(key);
        } else if (toSet instanceof NumberSetting) {
            double value;
            try {
                value = Double.parseDouble(args[2]);
            } catch (NumberFormatException e) {
                throw new CommandException(args[0] + " is not a valid number setting");
            }
            ((NumberSetting) toSet).setNumber(value);
        } else if (toSet instanceof BooleanSetting) {
            if (!(args[2].equalsIgnoreCase("true") || args[2].equalsIgnoreCase("false")))
                throw new CommandException(args[0] + " is not a valid boolean setting");
            ((BooleanSetting) toSet).setEnabled(Boolean.parseBoolean(args[2]));
        } else if (toSet instanceof ModeSetting) {
            String value = args[2];
            if(!((ModeSetting) toSet).modes.contains(value))
                throw new CommandException(args[2] + " is not a valid mode setting");
            ((ModeSetting) toSet).setMode(value);
        } else if (toSet instanceof ColorSetting) {
            long value;
            try {
                value = Long.parseLong(args[2]);
            } catch (NumberFormatException e) {
                throw new CommandException(args[0] + " is not a valid color setting");
            }
            ((ColorSetting) toSet).fromInteger(value);
        }

        ChatUtils.info("(highlight)%s(default) of (highlight)%s(default) has been set to (highlight)%s (default)", toSet.name, mod.getName(), args[2]);
    }
}
