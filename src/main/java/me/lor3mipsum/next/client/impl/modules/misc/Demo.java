package me.lor3mipsum.next.client.impl.modules.misc;

import me.lor3mipsum.next.client.module.Category;
import me.lor3mipsum.next.client.module.Module;
import org.lwjgl.glfw.GLFW;

public class Demo extends Module {
    public Demo() {
        super("Demo", "A demo module", Category.MISC, true, false, GLFW.GLFW_KEY_R);
    }

    @Override
    public void onEnable() {
        System.out.println("Hello Fabric world!");
    }
}
