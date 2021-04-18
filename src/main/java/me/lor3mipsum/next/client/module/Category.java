package me.lor3mipsum.next.client.module;

public enum Category {
    COMBAT("Combat"), EXPLOIT("Exploit"), PLAYER("Player"), RENDER("Render"), MISC("Misc");

    private String name;

    Category(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
