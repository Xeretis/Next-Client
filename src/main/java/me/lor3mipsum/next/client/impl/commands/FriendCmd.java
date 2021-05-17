package me.lor3mipsum.next.client.impl.commands;

import me.lor3mipsum.next.Main;
import me.lor3mipsum.next.api.util.player.ChatUtils;
import me.lor3mipsum.next.client.core.command.Command;
import me.lor3mipsum.next.client.core.command.annotation.Cmd;
import me.lor3mipsum.next.client.core.social.Friend;
import me.lor3mipsum.next.client.core.social.SocialManager;
import net.minecraft.util.Formatting;

import java.util.List;

@Cmd(name = "friend", description = "You can manage your friends with this", aliases = {"f"})
public class FriendCmd extends Command {

    @Override
    public void run(String alias, String[] args) {
        if (args.length < 1) {
            ChatUtils.commandError(this, "Usage: " + Main.prefix + alias + " <list/add/remove> [<add/remove ? friend>] [<add ? level>]");
            return;
        }

        if (args[0].equalsIgnoreCase("list")) {
            List<Friend> friends = Main.socialManager.getFriends();

            if (friends.size() != 0) {
                String msg = "Friends: ";
                for (Friend friend : friends)
                    msg += friend.getName() + " - " + friend.getLevel() + ", ";
                msg = msg.substring(0, msg.length() - 2);
                ChatUtils.commandInfo(this, msg);
            } else {
                ChatUtils.commandInfo(this, "Damn, you don't have any friends :(");
            }
        } else if (args[0].equalsIgnoreCase("add")) {
            if (args.length < 2) {
                ChatUtils.commandError(this, "Usage: " + Main.prefix + alias + " <list/add/remove> [<add/remove ? friend>] [<add ? level>]");
                return;
            }

            if (Main.socialManager.getFriendNames().contains(args[1])) {
                ChatUtils.commandError(this, Formatting.WHITE + args[1] + Formatting.RED + " is already your friend");
                return;
            }

            if (Main.socialManager.getEnemyNames().contains(args[1])) {
                ChatUtils.commandError(this, "Can't have " + Formatting.WHITE + args[1] + Formatting.RED + " as both a friend and an enemy");
                return;
            }

            if (args.length == 2)
                Main.socialManager.addFriend(new Friend(args[1], 0));
            else {
                int level;

                try {
                    level = Integer.parseInt(args[2]);
                } catch (NumberFormatException e) {
                    ChatUtils.commandError(this, "'" + Formatting.WHITE + args[2] + Formatting.RED + "' isn't a valid level");
                    return;
                }

                Main.socialManager.addFriend(new Friend(args[1], level));
            }

            ChatUtils.commandInfo(this, "Successfully added " + Formatting.WHITE + args[1] + Formatting.GRAY + " as friend");
        } else if (args[0].equalsIgnoreCase("remove")) {
            if (args.length != 2) {
                ChatUtils.commandError(this, "Usage: " + Main.prefix + alias + " <list/add/remove> [<add/remove ? friend>] [<add ? level>]");
                return;
            }

            if (!Main.socialManager.getFriendNames().contains(args[1])) {
                ChatUtils.commandError(this, "Couldn't find " + Formatting.WHITE + args[1] + Formatting.RED + " in your friend list");
                return;
            }

            Main.socialManager.deleteFriend(args[1]);

            ChatUtils.commandInfo(this, "Successfully removed " + Formatting.WHITE + args[1] + Formatting.GRAY + " from your friend list");
        } else
            ChatUtils.commandError(this, "Usage: " + Main.prefix + alias + " <list/add/remove> [<add/remove ? friend>] [<add ? level>]");
    }

}
