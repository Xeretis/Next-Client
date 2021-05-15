package me.lor3mipsum.next.client.core.module;

import me.lor3mipsum.next.Main;
import me.lor3mipsum.next.api.event.module.ModuleDisableEvent;
import me.lor3mipsum.next.api.event.module.ModuleEnableEvent;
import me.lor3mipsum.next.client.core.module.annotation.Mod;
import me.zero.alpine.listener.Listenable;
import net.minecraft.client.MinecraftClient;

public class Module implements Listenable {

    private final String name = getModDeclaration().name();
    private final String description = getModDeclaration().description();

    private final Category category = getModDeclaration().category();
    private int bind = getModDeclaration().bind();
    private boolean enabled = getModDeclaration().enabled();
    private boolean drawn = getModDeclaration().drawn();

    protected static final MinecraftClient mc = MinecraftClient.getInstance();

    public Mod getModDeclaration() {
        return getClass().getAnnotation(Mod.class);
    }

    //For override
    protected void onEnable() {

    }

    protected void onDisable() {

    }

    public String getHudInfo() {
        return "";
    }

    //For state manipulation
    public void setEnabled(boolean enabled) {
        if (enabled) {
            this.enabled = true;

            Main.EVENT_BUS.subscribe(this);
            Main.EVENT_BUS.post(new ModuleEnableEvent(this));

            Main.LOG.info("Enabled module '" + getName() + "'");

            onEnable();
        } else {
            this.enabled = false;

            Main.EVENT_BUS.unsubscribe(this);
            Main.EVENT_BUS.post(new ModuleDisableEvent(this));

            Main.LOG.info("Disabled module '" + getName() + "'");

            onDisable();
        }
    }

    public void toggle() {
        setEnabled(!enabled);
    }

    public void setDrawn(boolean drawn) {
        this.drawn = drawn;
    }

    public void setBind(int bind) {
        this.bind = bind;
    }

    //Getters
    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Category getCategory() {
        return category;
    }

    public int getBind() {
        return bind;
    }

    public boolean getEnabled() {
        return enabled;
    }

    public boolean getDrawn() {
        return drawn;
    }
}
