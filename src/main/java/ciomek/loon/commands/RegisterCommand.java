package ciomek.loon.commands;

import ciomek.loon.mqtt.MQTTClient;
import ciomek.loon.mqtt.payload.RegisterPayload;
import ciomek.loon.mqtt.payload.RegisterResponse;
import ciomek.loon.utils.PasswordHasher;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentTypeString;
import com.mojang.brigadier.builder.ArgumentBuilderLiteral;
import com.mojang.brigadier.builder.ArgumentBuilderRequired;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.net.command.CommandManager;
import net.minecraft.core.net.command.CommandSource;

import static com.mojang.brigadier.builder.ArgumentBuilderLiteral.literal;

public class RegisterCommand implements CommandManager.CommandRegistry {
	@SuppressWarnings("unchecked")
	@Override
	public void register(CommandDispatcher<CommandSource> commandDispatcher) {
		commandDispatcher
			.register(
				(ArgumentBuilderLiteral<CommandSource>) (Object) literal("register")
					.then(
						ArgumentBuilderRequired.argument("internalUsername", ArgumentTypeString.string())
							.then(
								ArgumentBuilderRequired.argument("password", ArgumentTypeString.string())
									.executes(context -> {
										CommandSource source = (CommandSource) context.getSource();
										Player player = source.getSender();

										if (player == null) {
											source.sendMessage("This command can only be run by a player!");
											return 0;
										}

										MQTTClient.getInstance().subscribe("loon/auth/register/+/response", (topic, message) -> {
											String[] parts = topic.split("/");

											String playerUuid = parts[3];

											ObjectMapper objectMapper = new ObjectMapper();
											RegisterResponse response = objectMapper.readValue(
												message.getPayload(),
												RegisterResponse.class
											);

											if (response == null)
												return;

											if (response.success())
												source.sendMessage("Successfully registered the user!");
											else
											{
												source.sendMessage("Failed to register the user!");
												source.sendMessage("Here's what went wrong:");
												source.sendMessage(response.error());
											}
										});

										String internalUsername = ArgumentTypeString.getString(context, "internalUsername");
										String password = ArgumentTypeString.getString(context, "password");

										String hashedPassword = PasswordHasher.hash(password);

										RegisterPayload payload = new RegisterPayload(player.uuid.toString(), hashedPassword, internalUsername);

										MQTTClient.getInstance().publish(payload.topics().get(0), payload.toJson());

										source.sendMessage("Sent request to the server! Waiting for the response. ");

										return 1;
									})
							)
					)
			);
	}
}
