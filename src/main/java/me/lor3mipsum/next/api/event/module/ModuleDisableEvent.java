package me.lor3mipsum.next.api.event.module;

import me.lor3mipsum.next.client.core.module.Module;

public class ModuleDisableEvent extends ModuleEvent{

    public ModuleDisableEvent(Module mod) {
        super(mod);
    }

}
