package me.lor3mipsum.next;

import me.lor3mipsum.next.client.module.ModuleManager;
import me.lor3mipsum.next.client.setting.SettingManager;
import net.fabricmc.api.ModInitializer;

public class Next implements ModInitializer {

	public static final String CLIENT_NAME = "Next";
	public static final String CLIENT_VERSION = "0.2.0";

	public static Next INSTANCE;

	public ModuleManager moduleManager;
	public SettingManager settingManager;

	public Next() {
		INSTANCE = this;
	}

	@Override
	public void onInitialize() {
		moduleManager = new ModuleManager();
		settingManager = new SettingManager();

		moduleManager.addModules();
	}
}
