package me.lor3mipsum.next.client.impl.modules.player;

import me.lor3mipsum.next.Main;
import me.lor3mipsum.next.api.util.client.ChatUtils;
import me.lor3mipsum.next.client.core.module.Category;
import me.lor3mipsum.next.client.core.module.Module;
import me.lor3mipsum.next.client.core.module.annotation.Mod;
import org.lwjgl.glfw.GLFW;

@Mod(name = "TestModule", description = "Test description", category = Category.PLAYER, bind = GLFW.GLFW_KEY_R)
public class TestModule extends Module {
    @Override
    public void onEnable() {
        Main.LOG.info("TestModule enabled");
        ChatUtils.moduleInfo(this, "hey");
    }

    @Override
    public void onDisable() {
        Main.LOG.info("TestModule disabled");
    }
}
