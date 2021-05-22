package me.lor3mipsum.next.client.core.command;

import me.lor3mipsum.next.Main;
import me.lor3mipsum.next.api.util.player.ChatUtils;
import me.lor3mipsum.next.client.core.command.annotation.Cmd;
import net.minecraft.util.Formatting;
import org.reflections.Reflections;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class CommandManager {
    private final List<Command> commands = new ArrayList<>();

    public CommandManager() {
        try {
            addCommands();
        } catch (Exception e) {
            Main.LOG.error("Failed to load commands");
            Main.LOG.error(e.getMessage(), e);
        }
    }

    private void addCommands() throws IllegalAccessException, InstantiationException {
        Reflections reflections = new Reflections("me.lor3mipsum.next.client.impl.commands");

        Set<Class<? extends Command>> commandClasses = reflections.getSubTypesOf(Command.class);

        for (Class<? extends Command> commandClass : commandClasses) {
            if (commandClass.isAnnotationPresent(Cmd.class)) {
                Command loadedCommand = commandClass.newInstance();
                commands.add(loadedCommand);
            }
        }
    }

    public void executeCommand(String string) {
        String raw = string.substring(1);
        String[] split = raw.split(" ");

        if (split.length == 0) return;

        String cmdName = split[0];

        Command command = commands.stream().filter(cmd -> cmd.match(cmdName)).findFirst().orElse(null);


        if (command == null) {
            ChatUtils.error("Command " + Formatting.WHITE + cmdName + Formatting.RED + " doesn't exist");
        } else {
            String[] args = new String[split.length - 1];

            System.arraycopy(split, 1, args, 0, split.length - 1);

            command.run(split[0], args);

        }
    }

    //Getters
    public List<Command> getCommands() {
        return commands;
    }
}
