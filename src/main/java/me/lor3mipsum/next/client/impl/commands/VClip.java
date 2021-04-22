package me.lor3mipsum.next.client.impl.commands;

import me.lor3mipsum.next.Next;
import me.lor3mipsum.next.client.command.Command;
import me.lor3mipsum.next.client.command.CommandException;
import me.lor3mipsum.next.client.utils.ChatUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.Vec3d;

public class VClip extends Command {
    public VClip() {
        super("vclip");
    }

    @Override
    public void run(String alias, String[] args) {
        if (args.length < 1) {
            throw new CommandException("Usage: " + Next.prefix + alias + " <distance>");
        }
        double dist;
        try {
            dist = Double.parseDouble(args[0]);
        } catch (NumberFormatException e) {
            throw new CommandException(args[0] + " is not a valid distance");
        }

        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        assert player != null;

        player.updatePosition(player.getX(), player.getY() + dist, player.getZ());

        ChatUtils.info("Vclipped (highlight)%s(default) blocks", dist);
    }
}
