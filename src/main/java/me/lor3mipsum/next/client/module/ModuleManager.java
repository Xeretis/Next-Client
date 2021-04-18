package me.lor3mipsum.next.client.module;

import me.lor3mipsum.next.Next;
import me.lor3mipsum.next.client.event.EventManager;
import me.lor3mipsum.next.client.event.EventTarget;
import me.lor3mipsum.next.client.impl.events.KeyEvent;
import me.lor3mipsum.next.client.impl.modules.misc.Demo;

import java.util.ArrayList;
import java.util.List;

public class ModuleManager {

    private List<Module> modules = new ArrayList<>();

    public ModuleManager() {
        EventManager.register(this);
    }

    private void addModule(Module module) {
        modules.add(module);
        EventManager.register(module);
        Next.INSTANCE.settingManager.registerObject(module.getName(), module);
    }

    public void addModules() {
        //Misc
        addModule(new Demo());
    }

    public List<Module> getModules() {
        return modules;
    }

    public <T extends Module> T getModule(Class<T> module) {
        return (T) modules.stream().filter(mod -> mod.getClass() == module).findFirst().orElse(null);
    }

    public Module getModule(String name, boolean caseSensitive) {
        return modules.stream().filter(mod -> !caseSensitive && name.equalsIgnoreCase(mod.getName()) || name.equals(mod.getName())).findFirst().orElse(null);
    }

    @EventTarget
    private void onKey(KeyEvent event) {
        for (Module module : modules) if (module.getKeybind() == event.getKey() && module.isCanBeEnabled()) module.setState(!module.getState());
    }

}
