package me.lor3mipsum.next.client.core.social;

public class Friend {
    private final String name;
    private final int level;

    public Friend(String name, int level) {
        this.name = name;
        this.level = level;
    }

    public String getName() {
        return this.name;
    }

    public int getLevel() {
        return this.level;
    }
}
