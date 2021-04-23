package me.lor3mipsum.next.client.command;

import me.lor3mipsum.next.client.impl.commands.*;
import me.lor3mipsum.next.client.impl.commands.Set;
import me.lor3mipsum.next.client.utils.ChatUtils;

import java.util.*;

public class CommandManager {
    private static List<Command> commands;

    public static List<Command> getCommands() {
        return commands;
    }

    public static void init() {
        commands = new ArrayList<>();
    }

    public static void addCommands() {
        addCommand(new HClip());
        addCommand(new Toggle());
        addCommand(new Set());
        addCommand(new Prefix());
        addCommand(new VClip());
        addCommand(new Help());
        addCommand(new Config());
        addCommand(new ListCmd());
        addCommand(new Say());
        addCommand(new MacroCmd());
        addCommand(new Nbt());
    }

    private static void addCommand(Command cmd) {
        commands.add(cmd);
    }

    public static boolean executeCommand(String string) {
        String raw = string.substring(1);
        String[] split = raw.split(" ");

        if (split.length == 0) return false;

        String cmdName = split[0];

        Command command = commands.stream().filter(cmd -> cmd.match(cmdName)).findFirst().orElse(null);

        try {
            if (command == null) {
                ChatUtils.error("'" + cmdName + "' doesn't exist");
                return false;
            } else {
                String[] args = new String[split.length - 1];

                System.arraycopy(split, 1, args, 0, split.length - 1);

                command.run(split[0], args);
                return true;
            }
        } catch (CommandException e) {
            ChatUtils.error( e.getMessage());
        }
        return true;
    }
}
