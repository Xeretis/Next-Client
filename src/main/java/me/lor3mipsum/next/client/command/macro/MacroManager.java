package me.lor3mipsum.next.client.command.macro;

import me.lor3mipsum.next.client.event.EventManager;
import me.lor3mipsum.next.client.event.EventTarget;
import me.lor3mipsum.next.client.impl.events.KeyEvent;

import java.util.ArrayList;
import java.util.List;

public class MacroManager {
    private List<Macro> macros = new ArrayList<>();

    public MacroManager() {
        EventManager.register(this);
    }

    public List<Macro> getMacros() {
        return macros;
    }

    public List<Integer> getKeys() {
        List<Integer> keys = new ArrayList<>();
        for (Macro macro : macros)
            keys.add(macro.key);
        return keys;
    }

    public void addMacro(Macro macro) {
        macros.add(macro);
    }

    public void removeMacro(int key) {
        for (Macro macro : macros)
            if (macro.key == key)
                macros.remove(macro);
    }

    @EventTarget
    private void onKey(KeyEvent event) {
        for (Macro macro : macros)
            if (macro.key == event.getKey()) {
                System.out.println(macro.command);
                macro.run();
            }

    }
}
