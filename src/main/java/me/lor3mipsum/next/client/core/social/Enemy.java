package me.lor3mipsum.next.client.core.social;

public class Enemy {
    private final String name;
    private final int level;

    public Enemy(String name, int level) {
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
