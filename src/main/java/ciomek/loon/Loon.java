package ciomek.loon;

import ciomek.loon.commands.ChangePasswordCommand;
import ciomek.loon.commands.RegisterCommand;
import ciomek.loon.commands.WebUsernameCommand;
import ciomek.loon.mqtt.MQTTClient;
import net.fabricmc.api.ModInitializer;
import net.minecraft.core.net.command.CommandManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import turniplabs.halplibe.HalpLibe;
import turniplabs.halplibe.event.defs.CommonEvents;
import turniplabs.halplibe.util.dependency.Key;

public class Loon implements ModInitializer {
	public static final String MOD_ID = HalpLibe.registerMod("loon", true);
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final String EnvVarPrefix = "LOON__";

	@Override
	public void onInitialize() {
		CommonEvents.BEFORE_GAME_START.listen(Key.of(MOD_ID), this::beforeGameStart);
		CommonEvents.AFTER_GAME_START.listen(Key.of(MOD_ID), this::afterGameStart);
		LOGGER.info("Loon initialized.");


		CommandManager.registerCommand(new RegisterCommand());
		CommandManager.registerCommand(new ChangePasswordCommand());
		CommandManager.registerCommand(new WebUsernameCommand());

		MQTTClient.getInstance();
	}

	public void beforeGameStart() {

	}

	public void afterGameStart() {

	}
}
