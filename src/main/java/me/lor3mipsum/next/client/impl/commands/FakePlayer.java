package me.lor3mipsum.next.client.impl.commands;

import me.lor3mipsum.next.Next;
import me.lor3mipsum.next.client.command.Command;
import me.lor3mipsum.next.client.command.CommandException;
import me.lor3mipsum.next.client.impl.modules.player.FakePlayerUtils;
import me.lor3mipsum.next.client.utils.ChatUtils;

public class FakePlayer extends Command {
    public FakePlayer() {
        super("fakeplayer", "Clears/Removes FakePlayers or spawns a FakePlayer", "fp");
    }

    @Override
    public void run(String alias, String[] args) {
        if(args.length < 1)
            throw new CommandException("Usage: " + Next.prefix + alias + " <spawn/remove/clear> [<remove ? id>]");

        if (args[0].equalsIgnoreCase("spawn")) {
            FakePlayerUtils.spawnFakePlayer();
            ChatUtils.info("Successfully spawned a new FakePlayer with id: (highlight)%d", FakePlayerUtils.ID - 1);
        } else if (args[0].equalsIgnoreCase("remove")) {
            if (args.length != 2)
                throw new CommandException("Usage: " + Next.prefix + alias + " <spawn/remove/clear> [<remove ? id>]");

            int id;
            try {
                id = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                throw new CommandException(args[1] + " is not a valid integer");
            }

            FakePlayerUtils.removeFakePlayer(id);
            ChatUtils.info("Successfully removed all FakePlayers with id: (highlight)%d", id);
        } else if (args[0].equalsIgnoreCase("clear")) {
            FakePlayerUtils.clearFakePlayers();
            ChatUtils.info("Successfully cleared all FakePlayers");
        } else
            throw new CommandException("Usage: " + Next.prefix + alias + " <spawn/remove/clear> [<remove ? id>]");
    }
}
