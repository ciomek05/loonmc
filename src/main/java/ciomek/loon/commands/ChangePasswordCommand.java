package ciomek.loon.commands;

import ciomek.loon.mqtt.MQTTClient;
import ciomek.loon.mqtt.payload.ChangePasswordPayload;
import ciomek.loon.mqtt.payload.RegisterResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentTypeString;
import com.mojang.brigadier.builder.ArgumentBuilderLiteral;
import com.mojang.brigadier.builder.ArgumentBuilderRequired;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.net.command.CommandManager;
import net.minecraft.core.net.command.CommandSource;

import static com.mojang.brigadier.builder.ArgumentBuilderLiteral.literal;

public class ChangePasswordCommand implements CommandManager.CommandRegistry {
	@SuppressWarnings("unchecked")
	@Override
	public void register(CommandDispatcher<CommandSource> commandDispatcher) {
		commandDispatcher
			.register(
				(ArgumentBuilderLiteral<CommandSource>) (Object) literal("change-password")
					.then(
						ArgumentBuilderRequired.argument("password", ArgumentTypeString.string())
							.executes(context -> {
								CommandSource source = (CommandSource) context.getSource();
								Player player = source.getSender();

								if (player == null) {
									source.sendMessage("This command can only be run by a player!");
									return 0;
								}

								String password = ArgumentTypeString.getString(context, "password");

								ChangePasswordPayload payload = new ChangePasswordPayload(player.uuid.toString(), password);

								MQTTClient.getInstance().publish(payload.topics().get(0), payload.toJson());

								source.sendMessage("Sent request to the server! Waiting for the response. ");

								MQTTClient.getInstance().subscribe("loon/register/+/response", (topic, message) -> {
									String[] parts = topic.split("/");

									String playerUuid = parts[2];

									ObjectMapper objectMapper = new ObjectMapper();
									RegisterResponse response = objectMapper.readValue(
										message.getPayload(),
										RegisterResponse.class
									);

									if (response == null)
										return;

									if (response.success())
										source.sendMessage("Successfully changed your password!");
									else
									{
										source.sendMessage("Failed to change your password!");
										source.sendMessage("Here's what went wrong:");
										source.sendMessage(response.error());
									}
								});

								return 1;
							})
					)
			);
	}
}
