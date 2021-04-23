package me.lor3mipsum.next.client.command.macro;

import me.lor3mipsum.next.client.command.CommandManager;

public class Macro {
    public int key;
    public String command;

    public Macro(int key, String command) {
        this.key = key;
        this.command = command;
    }

    public void run() {
        CommandManager.executeCommand(command);
    }
}
