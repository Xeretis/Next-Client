package me.lor3mipsum.next;

import me.lor3mipsum.next.client.core.module.ModuleManager;
import me.lor3mipsum.next.client.core.setting.SettingManager;
import me.zero.alpine.bus.EventBus;
import me.zero.alpine.bus.EventManager;
import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class Main implements ModInitializer {

	public static Main INSTANCE;

	public static final Logger LOG = LogManager.getLogger("Next");
	public static final EventBus EVENT_BUS = new EventManager();

	public static ModuleManager moduleManager;
	public  static SettingManager settingManager;

	public static String prefix = ".";

	@Override
	public void onInitialize() {
		LOG.info("Initializing the client");

		settingManager = new SettingManager();
		LOG.info("Initialized the setting manager");

		moduleManager = new ModuleManager();
		LOG.info("Initialized the module manager");
	}
}
