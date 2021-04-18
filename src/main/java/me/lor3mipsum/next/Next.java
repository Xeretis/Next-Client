package me.lor3mipsum.next;

import me.lor3mipsum.next.client.config.ConfigManager;
import me.lor3mipsum.next.client.event.EventManager;
import me.lor3mipsum.next.client.event.EventTarget;
import me.lor3mipsum.next.client.impl.events.DisconnectEvent;
import me.lor3mipsum.next.client.module.ModuleManager;
import me.lor3mipsum.next.client.setting.SettingManager;
import net.fabricmc.api.ModInitializer;

public class Next implements ModInitializer {

	public static final String CLIENT_NAME = "Next";
	public static final double CLIENT_VERSION = 0.2;

	public static Next INSTANCE;

	public ModuleManager moduleManager;
	public SettingManager settingManager;
	private ConfigManager configManager;

	public Next() {
		INSTANCE = this;
		EventManager.register(this);
	}

	@Override
	public void onInitialize() {
		moduleManager = new ModuleManager();
		settingManager = new SettingManager();
		configManager = new ConfigManager();

		moduleManager.addModules();

		configManager.load();
	}

	@EventTarget
	public void onDisconnect(DisconnectEvent event) {
		System.out.println("Disc.");
		try {
			configManager.save();
		} catch (Exception e) {
			System.err.println("Failed to save settings: ");
			e.printStackTrace();
		}
	}
}
