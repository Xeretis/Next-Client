package me.lor3mipsum.next.client.core.module;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import me.lor3mipsum.next.Main;
import me.lor3mipsum.next.api.event.client.KeyEvent;
import me.lor3mipsum.next.api.util.client.KeyboardUtils;
import me.lor3mipsum.next.client.core.module.annotation.Mod;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listenable;
import me.zero.alpine.listener.Listener;
import net.minecraft.client.MinecraftClient;
import org.reflections.Reflections;

import java.util.Set;

public class ModuleManager implements Listenable {

    private final List<Module> modules = new ArrayList<>();

    public ModuleManager() {
        Main.EVENT_BUS.subscribe(this);
        try {
            addModules();
        } catch (Exception e) {
            Main.LOG.error("Failed to load modules");
            Main.LOG.error(e.getMessage(), e);
        }
    }

    private void addModules() throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        Reflections reflections = new Reflections("me.lor3mipsum.next.client.impl.modules");

        Set<Class<? extends Module>> moduleClasses = reflections.getSubTypesOf(Module.class);

        for (Class<? extends Module> moduleClass : moduleClasses) {
            if (moduleClass.isAnnotationPresent(Mod.class)) {
                Module loadedModule = moduleClass.getDeclaredConstructor().newInstance();
                if (loadedModule.getEnabled()) {
                    Main.EVENT_BUS.subscribe(loadedModule);
                    loadedModule.onEnable();
                }
                Main.settingManager.registerObject(loadedModule.getName(), loadedModule);
                modules.add(loadedModule);
            }
        }
    }

    //Getters
    public List<Module> getModules() {
        return modules;
    }

    public ArrayList<Module> getModulesInCategory(Category category) {
        ArrayList<Module> list = new ArrayList<>();

        for (Module module : modules) {
            if (!module.getCategory().equals(category)) continue;
            list.add(module);
        }

        return list;
    }

    @SuppressWarnings("unchecked")
    public <T extends Module> T getModule(Class<T> module) {
        return (T) modules.stream().filter(mod -> mod.getClass() == module).findFirst().orElse(null);
    }

    public Module getModule(String name, boolean caseSensitive) {
        return modules.stream().filter(mod -> !caseSensitive && name.equalsIgnoreCase(mod.getName()) || name.equals(mod.getName())).findFirst().orElse(null);
    }

    @EventHandler
    private Listener<KeyEvent> onKey = new Listener<>(event -> {
        if (event.action == KeyboardUtils.KeyAction.Repeat || event.action == KeyboardUtils.KeyAction.Release) return;
        if (MinecraftClient.getInstance().currentScreen == null)
            for(Module mod : modules)
                if (mod.getBind() == event.key)
                    mod.toggle();
    });
}
