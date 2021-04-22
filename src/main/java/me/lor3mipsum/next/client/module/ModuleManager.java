package me.lor3mipsum.next.client.module;

import me.lor3mipsum.next.Next;
import me.lor3mipsum.next.client.event.EventManager;
import me.lor3mipsum.next.client.event.EventTarget;
import me.lor3mipsum.next.client.impl.events.KeyEvent;
import me.lor3mipsum.next.client.impl.events.RenderEvent;
import me.lor3mipsum.next.client.impl.modules.client.ClickGuiModule;
import me.lor3mipsum.next.client.impl.modules.client.ColorMode;
import me.lor3mipsum.next.client.impl.modules.client.HudEditor;
import me.lor3mipsum.next.client.impl.modules.exploit.AirPlace;
import me.lor3mipsum.next.client.impl.modules.hud.Welcomer;
import me.lor3mipsum.next.client.impl.modules.movement.Sprint;
import me.lor3mipsum.next.client.impl.modules.movement.Velocity;
import me.lor3mipsum.next.client.impl.modules.player.FastUse;
import me.lor3mipsum.next.client.impl.modules.render.Fullbright;

import java.util.ArrayList;
import java.util.List;

public class ModuleManager {

    private List<Module> modules = new ArrayList<>();

    public ModuleManager() {
        EventManager.register(this);
    }

    private void addModule(Module module) {
        modules.add(module);
        Next.INSTANCE.settingManager.registerObject(module.getName(), module);
    }

    public void addModules() {
        //Movement
        addModule(new Sprint());
        addModule(new Velocity());
        //Exploit
        addModule(new AirPlace());
        //Render
        addModule(new Fullbright());
        //Player
        addModule(new FastUse());
        //Client
        addModule(new ClickGuiModule());
        addModule(new ColorMode());
        addModule(new HudEditor());
        //Hud
        addModule(new Welcomer());
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

    public List<Module> getModulesByCategory(Category c) {
        List<Module> returnList = new ArrayList<>();

        for(Module m : modules) {
            if(m.getCategory() == c)
                returnList.add(m);
        }
        return returnList;
    }

    @EventTarget
    private void onHotbarRenderEvent(RenderEvent event) {
        Next.INSTANCE.clickGui.render();
    }

    @EventTarget
    private void onKey(KeyEvent event) {
        for (Module module : modules)
            if (module.getKeybind() == event.getKey())
                module.setState(!module.getState());
    }

}
