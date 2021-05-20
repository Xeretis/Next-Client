package me.lor3mipsum.next.client.core.command.macro;

import me.lor3mipsum.next.Main;
import me.lor3mipsum.next.api.event.client.KeyEvent;
import me.lor3mipsum.next.api.util.client.KeyboardUtils;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listenable;
import me.zero.alpine.listener.Listener;
import net.minecraft.client.MinecraftClient;

import java.util.ArrayList;
import java.util.List;

public class MacroManager implements Listenable {
    private List<Macro> macros = new ArrayList<>();

    public MacroManager() {
        Main.EVENT_BUS.subscribe(this);
    }

    public List<Macro> getMacros() {
        return macros;
    }

    public void addMacro(Macro macro) {
        macros.add(macro);
    }

    public Macro getMacro(String name) {
        for (Macro macro : macros)
            if (macro.getName() == name)
                return macro;
        return null;
    }

    public void removeMacro(Macro macro) {
        macros.remove(macro);
    }

    @EventHandler
    private Listener<KeyEvent> onKey = new Listener<>(event -> {
        if (event.action == KeyboardUtils.KeyAction.Repeat || event.action == KeyboardUtils.KeyAction.Release) return;
        if (MinecraftClient.getInstance().currentScreen == null)
            for (Macro macro : macros)
                if (macro.getKey() == event.key)
                    macro.run();
    });
}
