package me.lor3mipsum.next.client.core.command.macro;

import me.lor3mipsum.next.Main;

public class Macro {
    private String name;
    private int key;
    private String command;

    public Macro(String name, int key, String command) {
        this.name = name;
        this.key = key;
        this.command = command;
    }

    public String getName() {
        return name;
    }

    public int getKey() {
        return key;
    }

    public String getCommand() {
        return command;
    }

    public void run() {
        Main.commandManager.executeCommand(Main.prefix + command);
    }
}
