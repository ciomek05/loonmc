package ciomek.loon.commands;

import ciomek.loon.mqtt.MQTTClient;
import ciomek.loon.mqtt.payload.ShowUsernameResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilderLiteral;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.net.command.CommandManager;
import net.minecraft.core.net.command.CommandSource;

import static com.mojang.brigadier.builder.ArgumentBuilderLiteral.literal;

public class WebUsernameCommand implements CommandManager.CommandRegistry {
	@SuppressWarnings("unchecked")
	@Override
	public void register(CommandDispatcher<CommandSource> commandDispatcher) {
		commandDispatcher
			.register(
				(ArgumentBuilderLiteral<CommandSource>) (Object) literal("web-username")
					.executes(context -> {
						CommandSource source = (CommandSource) context.getSource();
						Player player = source.getSender();

						if (player == null) {
							source.sendMessage("This command can only be run by a player!");
							return 0;
						}

						String uuid = player.uuid.toString();
						String responseTopic = "loon/auth/show_username/" + uuid + "/response";

						MQTTClient.getInstance().subscribe(responseTopic, (topic, message) -> {
							MQTTClient.getInstance().unsubscribe(responseTopic);

							ObjectMapper objectMapper = new ObjectMapper();
							ShowUsernameResponse response = objectMapper.readValue(
								message.getPayload(),
								ShowUsernameResponse.class
							);

							if (response == null)
								return;

							if (response.success())
								source.sendMessage(response.internalUsername());
							else
								source.sendMessage("You are not registered. Use /register command. ");
						});

						MQTTClient.getInstance().publish("loon/auth/show_username/" + uuid, "{}");

						return 1;
					})
			);
	}
}
