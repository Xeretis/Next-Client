package me.lor3mipsum.next.client.impl.commands;

import me.lor3mipsum.next.Next;
import me.lor3mipsum.next.client.command.Command;
import me.lor3mipsum.next.client.command.CommandException;
import me.lor3mipsum.next.client.social.SocialManager;
import me.lor3mipsum.next.client.utils.ChatUtils;

import java.util.ArrayList;

public class EnemyCmd extends Command {
    public EnemyCmd() {
        super("enemy", "You can manage your enemies with this");
    }

    @Override
    public void run(String alias, String[] args) {
        if (args.length < 1)
            throw new CommandException("Usage: " + Next.prefix + alias + " <list/add/remove> [<add/remove ? enemy>]");

        if (args[0].equalsIgnoreCase("list")) {
            ArrayList<String> enemies = SocialManager.getEnemyNames();

            if (enemies.size() != 0) {
                String msg = "Enemies: ";
                for (String enemy : enemies)
                    msg += enemy + ", ";
                msg = msg.substring(0, msg.length() - 2);
                ChatUtils.info(msg);
            } else {
                ChatUtils.info("Ay, you don't have any enemies :)");
            }
        } else if (args[0].equalsIgnoreCase("add")) {
            if (args.length != 2)
                throw new CommandException("Usage: " + Next.prefix + alias + " <list/add/remove> [<add/remove ? enemy>]");

            if (SocialManager.isEnemy(args[1]))
                throw new CommandException(args[1] + " is already your enemy");

            if (SocialManager.isFriend(args[1]))
                throw new CommandException("Can't have " + args[1] + " as both an enemy and a friend");

            SocialManager.addEnemy(args[1]);
            ChatUtils.info("Successfully added (highlight)%s (default)as an enemy", args[1]);
        } else if (args[0].equalsIgnoreCase("remove")) {
            if (args.length != 2)
                throw new CommandException("Usage: " + Next.prefix + alias + " <list/add/remove> [<add/remove ? enemy>]");

            if (!SocialManager.isEnemy(args[1]))
                throw new CommandException("Couldn't find " + args[1] + " in your enemy list");

            SocialManager.deleteEnemy(args[1]);
            ChatUtils.info("Successfully removed (highlight)%s (default)from your enemy list", args[1]);
        } else {
            throw new CommandException("Usage: " + Next.prefix + alias + " <list/add/remove> [<add/remove ? enemy>]");
        }
    }
}
