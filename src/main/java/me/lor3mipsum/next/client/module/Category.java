package me.lor3mipsum.next.client.module;

public enum Category {
    COMBAT("Combat"), EXPLOIT("Exploit"), PLAYER("Player"), MOVEMENT("Movement"), RENDER("Render"), CLIENT("Client"), HUD("Hud");

    private final String name;

    Category(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
