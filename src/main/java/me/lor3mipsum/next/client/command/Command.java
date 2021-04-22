package me.lor3mipsum.next.client.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class Command {
    public String name;
    public String description;
    public String[] aliases;

    protected Command(String name, String description, String... aliases) {
        this.name = name;
        this.description = description;
        this.aliases = aliases;
    }

    public abstract void run(String alias, String[] args);

    boolean match(String name) {
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

}
