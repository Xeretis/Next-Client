package me.lor3mipsum.next.client.impl.commands;

import me.lor3mipsum.next.Main;
import me.lor3mipsum.next.api.util.entity.FakePlayerUtils;
import me.lor3mipsum.next.api.util.player.ChatUtils;
import me.lor3mipsum.next.client.core.command.Command;
import me.lor3mipsum.next.client.core.command.annotation.Cmd;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Formatting;

@Cmd(name = "fakeplayer", description = "Spawns a fake player for testing purposes", aliases = {"fp"})
public class FakePlayerCmd extends Command {

    private static final MinecraftClient mc = MinecraftClient.getInstance();

    @Override
    public void run(String alias, String[] args) {
        if(args.length < 1) {
            ChatUtils.commandError(this, "Usage: " + Main.prefix + alias + " <spawn/clear> [<spawn ? name>]");
            return;
        }

        if (args[0].equalsIgnoreCase("spawn")) {
            if (args.length < 2) {
                ChatUtils.commandError(this, "Usage: " + Main.prefix + alias + " <spawn/clear> [<spawn ? name>]");
                return;
            }

            FakePlayerUtils.add(args[1], mc.player.getHealth(), mc.player.getAbsorptionAmount(), true);
            ChatUtils.commandInfo(this, "Successfully spawned a new FakePlayer with the name " + Formatting.WHITE + args[1]);
        } else if (args[0].equalsIgnoreCase("clear")) {
            FakePlayerUtils.clear();
            ChatUtils.commandInfo(this, "Successfully cleared all FakePlayers");
        } else
            ChatUtils.commandError(this, "Usage: " + Main.prefix + alias + " <spawn/clear> [<spawn ? name>]");
    }

}
