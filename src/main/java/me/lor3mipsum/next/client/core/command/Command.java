package me.lor3mipsum.next.client.core.command;

import me.lor3mipsum.next.client.core.command.annotation.Cmd;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class Command {

    private final String name = getCmdDeclaration().name();
    private final String description = getCmdDeclaration().description();

    private final String[] aliases = getCmdDeclaration().aliases();

    public Cmd getCmdDeclaration() {
        return getClass().getAnnotation(Cmd.class);
    }

    public abstract void run(String alias, String[] args);

    public boolean match(String name) {
        for (String alias : aliases) {
            if (alias.equalsIgnoreCase(name)) return true;
        }
        return this.name.equalsIgnoreCase(name);
    }

    public List<String> getNameAndAliases() {
        List<String> l = new ArrayList<>();
        l.add(name);
        l.addAll(Arrays.asList(aliases));

        return l;
    }

    //Getters
    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String[] getAliases() {
        return aliases;
    }
}
