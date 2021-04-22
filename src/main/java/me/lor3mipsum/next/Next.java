package me.lor3mipsum.next;

import me.lor3mipsum.next.client.command.CommandManager;
import me.lor3mipsum.next.client.config.ConfigManager;
import me.lor3mipsum.next.client.event.EventManager;
import me.lor3mipsum.next.client.event.EventTarget;
import me.lor3mipsum.next.client.gui.clickgui.NextGui;
import me.lor3mipsum.next.client.impl.events.DisconnectEvent;
import me.lor3mipsum.next.client.module.ModuleManager;
import me.lor3mipsum.next.client.setting.SettingManager;
import me.lor3mipsum.next.client.social.SocialManager;
import me.lor3mipsum.next.client.utils.NetworkUtil;
import me.lor3mipsum.next.client.utils.NoStackTraceThrowable;
import me.lor3mipsum.next.client.utils.Utils;
import net.fabricmc.api.ModInitializer;
import org.lwjgl.glfw.GLFW;

import java.util.List;

public class Next implements ModInitializer {

	public static final String CLIENT_NAME = "Next";
	public static final double CLIENT_VERSION = 0.6;
	public static final int GUI_KEY = GLFW.GLFW_KEY_RIGHT_SHIFT;

	public static String prefix = ".";

	public static Next INSTANCE;

	public ModuleManager moduleManager;
	public SettingManager settingManager;
	public NextGui clickGui;
	private ConfigManager configManager;

	public Next() {
		INSTANCE = this;
		EventManager.register(this);
	}

	@Override
	public void onInitialize() {
		//hwid stuff
		List<String> hwids = NetworkUtil.getHWIDList();

		if(!hwids.contains(Utils.getEncryptedHWID("asdfJKLE")))
			throw new NoStackTraceThrowable("hmmmm");

		//other stuff
		moduleManager = new ModuleManager();
		settingManager = new SettingManager();
		configManager = new ConfigManager();
		CommandManager.init();
		SocialManager.init();

		moduleManager.addModules();
		CommandManager.addCommands();

		clickGui = new NextGui();

		configManager.load();
	}

	@EventTarget
	public void onDisconnect(DisconnectEvent event) {
		try {
			configManager.save();
		} catch (Exception e) {
			System.err.println("Failed to save settings: ");
			e.printStackTrace();
		}
	}
}
