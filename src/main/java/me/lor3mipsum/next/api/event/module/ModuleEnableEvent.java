package me.lor3mipsum.next.api.event.module;

import me.lor3mipsum.next.client.core.module.Module;

public class ModuleEnableEvent extends ModuleEvent{

    public ModuleEnableEvent(Module mod) {
        super(mod);
    }

}
