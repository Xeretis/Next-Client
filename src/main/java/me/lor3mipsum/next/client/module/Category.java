package me.lor3mipsum.next.client.module;

public enum Category {
    COMBAT("Combat"), EXPLOIT("Exploit"), PLAYER("Player"), RENDER("Render"), MISC("Misc"), CLIENT("Client"), HUD("Hud");

    private String name;

    Category(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
