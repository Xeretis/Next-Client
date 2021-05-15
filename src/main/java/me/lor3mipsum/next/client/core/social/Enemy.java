package me.lor3mipsum.next.client.core.social;

import me.lor3mipsum.next.Main;
import me.lor3mipsum.next.client.impl.modules.client.Social;

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
        if (Main.moduleManager.getModule(Social.class).ignoreEnemyLevel.getValue())
            return 0;
        return this.level;
    }
}
