package me.lor3mipsum.next.api.event.module;

import me.lor3mipsum.next.api.event.NextEvent;
import me.lor3mipsum.next.client.core.module.Module;

public class ModuleEvent extends NextEvent {
    public final Module mod;

    public ModuleEvent(Module mod) {
        this.mod = mod;
    }
}
