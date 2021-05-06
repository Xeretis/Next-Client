package me.lor3mipsum.next.client.core.module;

import java.util.ArrayList;
import java.util.List;

import me.lor3mipsum.next.Main;
import me.lor3mipsum.next.client.core.module.annotation.Mod;
import me.zero.alpine.listener.Listenable;
import org.reflections.Reflections;

import java.util.Set;

public class ModuleManager implements Listenable {

    private final List<Module> modules = new ArrayList<>();

    public ModuleManager() {
        Main.EVENT_BUS.subscribe(this);
        try {
            addModules();
        } catch (Exception e) {
            Main.LOG.error(e.getStackTrace());
        }
    }

    public void addModules() throws IllegalAccessException, InstantiationException {
        Reflections reflections = new Reflections("me.lor3mipsum.next.client.impl");

        Set<Class<? extends Module>> featureClasses = reflections.getSubTypesOf(Module.class);

        for (Class<? extends Module> moduleClass : featureClasses) {
            if (moduleClass.isAnnotationPresent(Mod.class)) {
                Module loadedFeature = moduleClass.newInstance();
                modules.add(loadedFeature);
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
}
