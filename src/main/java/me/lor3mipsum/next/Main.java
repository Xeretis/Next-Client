package me.lor3mipsum.next;

import me.lor3mipsum.next.api.config.LoadConfig;
import me.lor3mipsum.next.api.config.SaveConfig;
import me.lor3mipsum.next.api.event.game.GameLeftEvent;
import me.lor3mipsum.next.api.event.game.RenderEvent;
import me.lor3mipsum.next.api.util.client.ClientUtils;
import me.lor3mipsum.next.api.util.player.RotationUtils;
import me.lor3mipsum.next.api.util.world.TpsUtils;
import me.lor3mipsum.next.client.core.command.CommandManager;
import me.lor3mipsum.next.client.core.command.macro.MacroManager;
import me.lor3mipsum.next.client.core.gui.NextGui;
import me.lor3mipsum.next.client.core.module.ModuleManager;
import me.lor3mipsum.next.client.core.setting.SettingManager;
import me.lor3mipsum.next.client.core.social.SocialManager;
import me.zero.alpine.bus.EventBus;
import me.zero.alpine.bus.EventManager;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listenable;
import me.zero.alpine.listener.Listener;
import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.awt.*;

public class Main implements ModInitializer, Listenable {

	public static final String CLIENT_NAME = "Next";
	public static final String CLIENT_VERSION = "2.0.0";

	public static final Logger LOG = LogManager.getLogger("Next");
	public static final EventBus EVENT_BUS = new EventManager();

	public static ModuleManager moduleManager;
	public static SettingManager settingManager;
	public static CommandManager commandManager;
	public static MacroManager macroManager;
	public static SocialManager socialManager;
	public static NextGui clickGui;

	public static String prefix = ".";

	@Override
	public void onInitialize() {
		LOG.info("Initializing the client");

		ClientUtils.checkHWID();

		EVENT_BUS.subscribe(this);

		settingManager = new SettingManager();
		LOG.info("Initialized the setting manager");

		moduleManager = new ModuleManager();
		LOG.info("Initialized the module manager");

		commandManager = new CommandManager();
		LOG.info("Initialized the command manager");

		macroManager = new MacroManager();
		LOG.info("Initialized the macro manager");

		socialManager = new SocialManager();
		LOG.info("Initialized the social manager");

		clickGui = new NextGui();
		LOG.info("Initialized the clickgui");

		LoadConfig.load();
		LOG.info("Loaded the config");

		EVENT_BUS.subscribe(RotationUtils.INSTANCE);
		EVENT_BUS.subscribe(TpsUtils.INSTANCE);
		LOG.info("Registered the utils");
	}

	@EventHandler
	private Listener<GameLeftEvent> onDisconnect = new Listener<>(event -> {
		SaveConfig.save();
		LOG.info("Saved the config");
	});

	@EventHandler
	private Listener<RenderEvent> onRender = new Listener<>(event -> clickGui.render());
}
