package me.lor3mipsum.next.client.impl.commands;

import me.lor3mipsum.next.Next;
import me.lor3mipsum.next.client.command.Command;
import me.lor3mipsum.next.client.command.CommandException;
import me.lor3mipsum.next.client.config.ConfigManager;
import me.lor3mipsum.next.client.social.SocialManager;
import me.lor3mipsum.next.client.utils.ChatUtils;

import java.io.IOException;
import java.util.ArrayList;

public class FriendCmd extends Command {
    public FriendCmd() {
        super("friend", "You can manage your friends with this");
    }

    @Override
    public void run(String alias, String[] args) {
        if (args.length < 1)
            throw new CommandException("Usage: " + Next.prefix + alias + " <list/add/remove> [<add/remove ? friend>]");

        if (args[0].equalsIgnoreCase("list")) {
            ArrayList<String> friends = SocialManager.getFriendNames();

            if (friends.size() != 0) {
                String msg = "Friends: ";
                for (String friend : friends)
                    msg += friend + ", ";
                msg = msg.substring(0, msg.length() - 2);
                ChatUtils.info(msg);
            } else {
                ChatUtils.info("Damn, you don't have any friends :(");
            }
        } else if (args[0].equalsIgnoreCase("add")) {
            if (args.length != 2)
                throw new CommandException("Usage: " + Next.prefix + alias + " <list/add/remove> [<add/remove ? friend>]");

            if (SocialManager.isFriend(args[1]))
                throw new CommandException(args[1] + " is already your friend");

            if (SocialManager.isEnemy(args[1]))
                throw new CommandException("Can't have " + args[1] + " as both a friend and an enemy");

            SocialManager.addFriend(args[1]);
            ChatUtils.info("Successfully added (highlight)%s (default)as friend", args[1]);
        } else if (args[0].equalsIgnoreCase("remove")) {
            if (args.length != 2)
                throw new CommandException("Usage: " + Next.prefix + alias + " <list/add/remove> [<add/remove ? friend>]");

            if (!SocialManager.isFriend(args[1]))
                throw new CommandException("Couldn't find " + args[1] + " in your friend list");

            SocialManager.deleteFriend(args[1]);
            ChatUtils.info("Successfully removed (highlight)%s (default)from your friend list", args[1]);
        } else {
            throw new CommandException("Usage: " + Next.prefix + alias + " <list/add/remove> [<add/remove ? enemy>]");
        }
    }
}
