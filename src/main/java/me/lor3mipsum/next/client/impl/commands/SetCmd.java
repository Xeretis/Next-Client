package me.lor3mipsum.next.client.impl.commands;

import me.lor3mipsum.next.Main;
import me.lor3mipsum.next.api.util.client.KeyboardUtils;
import me.lor3mipsum.next.api.util.misc.NextColor;
import me.lor3mipsum.next.api.util.player.ChatUtils;
import me.lor3mipsum.next.client.core.command.Command;
import me.lor3mipsum.next.client.core.command.annotation.Cmd;
import me.lor3mipsum.next.client.core.module.Module;
import me.lor3mipsum.next.client.core.setting.Setting;
import me.lor3mipsum.next.client.core.setting.SettingSeparator;
import me.lor3mipsum.next.client.impl.settings.*;
import net.minecraft.util.Formatting;

import java.util.Arrays;

@Cmd(name = "set", description = "Allows you to set a specific setting in a module to a certain value")
public class SetCmd extends Command {

    @Override
    public void run(String alias, String[] args) {
        if (args.length < 3) {
            ChatUtils.commandError(this, "Usage: " + Main.prefix + alias + " <module> <setting> <value / ColorSetting ? <rainbow>> [ColorSetting ? <rainbow value>]");
            return;
        }

        Module mod = Main.moduleManager.getModule(args[0], false);

        if (mod == null) {
            ChatUtils.commandError(this, Formatting.WHITE + args[0] + Formatting.RED + " isn't a valid module");
            return;
        }

        Setting toSet = Main.settingManager.getByConfigName(mod, args[1]);

        if (toSet == null || toSet instanceof SettingSeparator) {
            ChatUtils.commandError(this, "The module " + Formatting.WHITE + args[0] + Formatting.RED + " doesn't have any setting called " + Formatting.WHITE + args[1]);
            return;
        }

        if (toSet instanceof KeyBindSetting) {
            int key = KeyboardUtils.getKeyFromName(args[2]);

            if (key == -2) {
                ChatUtils.commandError(this, Formatting.WHITE + args[2] + Formatting.RED + " isn't a valid key");
                return;
            }

            toSet.setValue(key);

        } else if (toSet instanceof IntegerSetting) {
            int value;

            try {
                value = Integer.parseInt(args[2]);
            } catch (NumberFormatException e) {
                ChatUtils.commandError(this, Formatting.WHITE + args[2] + Formatting.RED + " isn't a valid integer setting");
                return;
            }

            toSet.setValue(value);

        } else if (toSet instanceof DoubleSetting) {
            double value;

            try {
                value = Double.parseDouble(args[2]);
            } catch (NumberFormatException e) {
                ChatUtils.commandError(this, Formatting.WHITE + args[2] + Formatting.RED + " isn't a valid double setting");
                return;
            }

            toSet.setValue(value);

        } else if (toSet instanceof BooleanSetting) {
            if (!(args[2].equalsIgnoreCase("true") || args[2].equalsIgnoreCase("false") || args[2].equalsIgnoreCase("toggle"))) {
                ChatUtils.commandError(this, Formatting.WHITE + args[2] + Formatting.RED + " isn't a valid boolean setting");
                return;
            }

            if (args[2].equalsIgnoreCase("toggle")) {
                toSet.setValue(!((BooleanSetting) toSet).getValue());
                args[2] = Boolean.toString(((BooleanSetting) toSet).getValue());
            } else
                toSet.setValue(Boolean.parseBoolean(args[2]));

        } else if (toSet instanceof EnumSetting) {
            if (!((EnumSetting) toSet).getModes().contains(args[2])) {
                ChatUtils.commandError(this, Formatting.WHITE + args[2] + Formatting.RED + " isn't a valid mode setting");
                return;
            }
            while (!toSet.getValue().toString().equals(args[2]))
                ((EnumSetting) toSet).increment();

        } else if (toSet instanceof StringSetting) {
            toSet.setValue(String.join(" ", Arrays.copyOfRange(args, 2, args.length)));

            args[2] = String.join(" ", Arrays.copyOfRange(args, 2, args.length));

        } else if (toSet instanceof ColorSetting) {
            if (args.length >= 4)
                if (args[2].equalsIgnoreCase("rainbow")) {
                    if (!(args[3].equalsIgnoreCase("true") || args[3].equalsIgnoreCase("false") || args[3].equalsIgnoreCase("toggle"))) {
                        ChatUtils.commandError(this, Formatting.WHITE + args[3] + Formatting.RED + " isn't a valid rainbow value");
                        return;
                    }

                    if (args[3].equalsIgnoreCase("toggle")) {
                        ((ColorSetting) toSet).setRainbow(!((ColorSetting) toSet).getRainbow());
                        args[3] = Boolean.toString(((ColorSetting) toSet).getRainbow());
                    } else
                        ((ColorSetting) toSet).setRainbow(Boolean.parseBoolean(args[3]));

                    args[2] += " " + args[3];

                } else {
                    ChatUtils.commandError(this, "Usage: " + Main.prefix + alias + " <module> <setting> <value / ColorSetting ? <rainbow>> [ColorSetting ? <rainbow value>]");
                    return;
                }
            else {
                int value;

                try {
                    value = Integer.parseInt(args[2]);
                } catch (NumberFormatException e) {
                    ChatUtils.commandError(this, Formatting.WHITE + args[2] + Formatting.RED + " isn't a valid color setting");
                    return;
                }

                toSet.setValue(new NextColor(value, true));
            }
        }

        ChatUtils.commandInfo(this, Formatting.WHITE + toSet.getName() + Formatting.GRAY + " of " + Formatting.WHITE + mod.getName() + Formatting.GRAY + " has been set to " + Formatting.WHITE + args[2]);
    }

}
