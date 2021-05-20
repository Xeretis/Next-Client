package me.lor3mipsum.next.client.impl.commands;

import me.lor3mipsum.next.Main;
import me.lor3mipsum.next.api.util.player.ChatUtils;
import me.lor3mipsum.next.client.core.command.Command;
import me.lor3mipsum.next.client.core.command.annotation.Cmd;
import me.lor3mipsum.next.client.core.social.Enemy;
import net.minecraft.util.Formatting;

import java.util.List;

@Cmd(name = "enemy", description = "You can manage your enemies with this", aliases = {"e"})
public class EnemyCmd extends Command {

    @Override
    public void run(String alias, String[] args) {
        if (args.length < 1) {
            ChatUtils.commandError(this, "Usage: " + Main.prefix + alias + " <list/add/remove> [<add/remove ? enemy>] [<add ? level>]");
            return;
        }

        if (args[0].equalsIgnoreCase("list")) {
            List<Enemy> enemies = Main.socialManager.getEnemies();

            if (enemies.size() != 0) {
                StringBuilder msg = new StringBuilder("Enemies: ");
                for (Enemy enemy : enemies)
                    msg.append(enemy.getName()).append(" - ").append(enemy.getLevel()).append(", ");
                msg = new StringBuilder(msg.substring(0, msg.length() - 2));
                ChatUtils.commandInfo(this, msg.toString());
            } else {
                ChatUtils.commandInfo(this, "Ayyy, you don't have any enemies :)");
            }
        } else if (args[0].equalsIgnoreCase("add")) {
            if (args.length < 2) {
                ChatUtils.commandError(this, "Usage: " + Main.prefix + alias + " <list/add/remove> [<add/remove ? enemy>] [<add ? level>]");
                return;
            }

            if (Main.socialManager.getEnemyNames().contains(args[1])) {
                ChatUtils.commandError(this, Formatting.WHITE + args[1] + Formatting.RED + " is already your enemy");
                return;
            }

            if (Main.socialManager.getFriendNames().contains(args[1])) {
                ChatUtils.commandError(this, "Can't have " + Formatting.WHITE + args[1] + Formatting.RED + " as both an enemy and a friend");
                return;
            }

            if (args.length == 2)
                Main.socialManager.addEnemy(new Enemy(args[1], 0));
            else {
                int level;

                try {
                    level = Integer.parseInt(args[2]);
                } catch (NumberFormatException e) {
                    ChatUtils.commandError(this, "'" + Formatting.WHITE + args[2] + Formatting.RED + "' isn't a valid level");
                    return;
                }

                Main.socialManager.addEnemy(new Enemy(args[1], level));
            }

            ChatUtils.commandInfo(this, "Successfully added " + Formatting.WHITE + args[1] + Formatting.GRAY + " as enemy");
        } else if (args[0].equalsIgnoreCase("remove")) {
            if (args.length != 2) {
                ChatUtils.commandError(this, "Usage: " + Main.prefix + alias + " <list/add/remove> [<add/remove ? enemy>] [<add ? level>]");
                return;
            }

            if (!Main.socialManager.getEnemyNames().contains(args[1])) {
                ChatUtils.commandError(this, "Couldn't find " + Formatting.WHITE + args[1] + Formatting.RED + " in your enemies list");
                return;
            }

            Main.socialManager.deleteEnemy(args[1]);

            ChatUtils.commandInfo(this, "Successfully removed " + Formatting.WHITE + args[1] + Formatting.GRAY + " from your enemies list");
        } else
            ChatUtils.commandError(this, "Usage: " + Main.prefix + alias + " <list/add/remove> [<add/remove ? enemy>] [<add ? level>]");
    }

}
