package me.lor3mipsum.next.client.module;

import me.lor3mipsum.next.Next;
import me.lor3mipsum.next.client.event.EventManager;
import me.lor3mipsum.next.client.event.EventTarget;
import me.lor3mipsum.next.client.impl.events.KeyEvent;
import me.lor3mipsum.next.client.impl.events.RenderEvent;
import me.lor3mipsum.next.client.impl.modules.client.*;
import me.lor3mipsum.next.client.impl.modules.combat.*;
import me.lor3mipsum.next.client.impl.modules.exploit.AirPlace;
import me.lor3mipsum.next.client.impl.modules.exploit.BuildHeight;
import me.lor3mipsum.next.client.impl.modules.hud.*;
import me.lor3mipsum.next.client.impl.modules.movement.Blink;
import me.lor3mipsum.next.client.impl.modules.movement.NoSlow;
import me.lor3mipsum.next.client.impl.modules.movement.Sprint;
import me.lor3mipsum.next.client.impl.modules.movement.Velocity;
import me.lor3mipsum.next.client.impl.modules.player.AntiHunger;
import me.lor3mipsum.next.client.impl.modules.player.AutoTool;
import me.lor3mipsum.next.client.impl.modules.player.DeathCoords;
import me.lor3mipsum.next.client.impl.modules.player.FastUse;
import me.lor3mipsum.next.client.impl.modules.render.BreakIndicator;
import me.lor3mipsum.next.client.impl.modules.render.CustomFOV;
import me.lor3mipsum.next.client.impl.modules.render.Fullbright;
import me.lor3mipsum.next.client.impl.modules.render.NoRender;
import me.lor3mipsum.next.client.setting.SettingManager;

import java.util.ArrayList;
import java.util.List;

public class ModuleManager {

    private List<Module> modules = new ArrayList<>();

    public ModuleManager() {
        EventManager.register(this);
    }

    private void addModule(Module module) {
        modules.add(module);
        SettingManager.registerObject(module.getName(), module);
    }

    public void addModules() {
        //Combat
        addModule(new AutoArmor());
        addModule(new KillAura());
        addModule(new CrystalAura());
        addModule(new Surround());
        addModule(new Offhand());
        addModule(new AutoTotem());
        //Movement
        addModule(new Sprint());
        addModule(new Velocity());
        addModule(new NoSlow());
        addModule(new Blink());
        //Exploit
        addModule(new AirPlace());
        addModule(new BuildHeight());
        //Render
        addModule(new Fullbright());
        addModule(new BreakIndicator());
        addModule(new CustomFOV());
        addModule(new NoRender());
        //Player
        addModule(new FastUse());
        addModule(new DeathCoords());
        addModule(new AutoTool());
        addModule(new AntiHunger());
        //Client
        addModule(new ClickGuiModule());
        addModule(new ColorMode());
        addModule(new HudEditor());
        addModule(new DiscordPresence());
        //Hud
        addModule(new Welcomer());
        addModule(new Coordinates());
        addModule(new ArrayListModule());
        addModule(new Watermark());
        addModule(new PlayerModel());
        addModule(new Totems());
        addModule(new Crystals());
        addModule(new Gapples());
        addModule(new Ping());
        addModule(new ArmorHud());
        addModule(new Time());
        addModule(new Speed());
        addModule(new InventoryViewer());
        addModule(new Fps());
        addModule(new Tps());
        addModule(new Crapples());
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
