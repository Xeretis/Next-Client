package me.lor3mipsum.next.client.core.module;

import me.lor3mipsum.next.Main;
import me.lor3mipsum.next.api.event.module.ModuleDisableEvent;
import me.lor3mipsum.next.api.event.module.ModuleEnableEvent;
import me.lor3mipsum.next.client.core.module.annotation.Mod;
import me.zero.alpine.listener.Listenable;
import net.minecraft.client.MinecraftClient;

public class Module implements Listenable {

    private final String name = getClass().getAnnotation(Mod.class).name();
    private final String description = getClass().getAnnotation(Mod.class).description();

    private final Category category = getClass().getAnnotation(Mod.class).category();
    private int bind = getClass().getAnnotation(Mod.class).bind();
    private boolean enabled = getClass().getAnnotation(Mod.class).enabled();
    private boolean drawn = getClass().getAnnotation(Mod.class).drawn();

    protected static final MinecraftClient mc = MinecraftClient.getInstance();

    //For override
    protected void onEnable() {

    }

    protected void onDisable() {

    }

    protected String getHudInfo() {
        return "";
    }

    //For state manipulation
    public void setEnabled(boolean enabled) {
        if (enabled) {
            this.enabled = true;
            Main.EVENT_BUS.subscribe(this);
            Main.EVENT_BUS.post(new ModuleEnableEvent(this));
            onEnable();
        } else {
            this.enabled = false;
            Main.EVENT_BUS.unsubscribe(this);
            Main.EVENT_BUS.post(new ModuleDisableEvent(this));
            onDisable();
        }
    }

    public void toggle() {
        setEnabled(!enabled);
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
