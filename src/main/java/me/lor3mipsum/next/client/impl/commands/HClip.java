package me.lor3mipsum.next.client.impl.commands;

import me.lor3mipsum.next.client.command.Command;
import me.lor3mipsum.next.client.command.CommandException;
import me.lor3mipsum.next.client.utils.ChatUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.Vec3d;

public class HClip extends Command {
    public HClip() {
        super("hclip");
    }

    @Override
    public void run(String alias, String[] args) {
        if (args.length < 1) {
            throw new CommandException("Usage: ." + alias + " <distance>");
        }
        double dist;
        try {
            dist = Double.parseDouble(args[0]);
        } catch (NumberFormatException e) {
            throw new CommandException(args[0] + " is not a valid distance");
        }

        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        assert player != null;

        Vec3d forward = Vec3d.fromPolar(0, player.yaw).normalize();
        player.updatePosition(player.getX() + forward.x * dist, player.getY(), player.getZ() + forward.z * dist);

        ChatUtils.info("Hclipped " + dist + " blocks");
    }
}
