package ciomek.loon.mqtt;

import ciomek.loon.Loon;
import ciomek.loon.mqtt.payload.Payload;
import ciomek.loon.mqtt.payload.data.PlayerIdentity;
import ciomek.loon.mqtt.request.*;
import org.eclipse.paho.client.mqttv3.*;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MQTTClient {
	private static MQTTClient instance;
	private MqttClient client;
	private MQTTSettings settings;

	public static synchronized MQTTClient getInstance() {
		if(instance != null)
			return instance;

		MQTTClient mqttClient = MQTTClient.init();
		instance = mqttClient;

		return instance;
	}

	public void subscribe(String topicFilter, IMqttMessageListener messageListener) {
		try {
			client.subscribe(topicFilter, messageListener);
		} catch (MqttException e) {
			throw new RuntimeException(e);
		}
	}

	public void publish(String topic, String message, int qos) {
		MqttMessage mqttMessage = new MqttMessage(message.getBytes());
		mqttMessage.setQos(qos);

		try {
			client.publish(topic, mqttMessage);
		} catch (MqttException e) {
			throw new RuntimeException(e);
		}
	}

	public void publish(String topic, String message) {
		publish(topic, message, 1);
	}

	public void publish(String topic, Payload payload) {
		publish(topic, payload.toJson());
	}


	static MQTTClient init() {
		MQTTClient client = new MQTTClient();
		client.establish();

		return client;
	}

	public void establish() {
		settings = MQTTSettings.getFromEnvVars();

		String broker = settings.broker();
		String clientId = settings.clientId();
		String username = settings.username();
		String password = settings.password();

		try {
			client = new MqttClient(broker, clientId);
			MqttConnectOptions options = new MqttConnectOptions();
			options.setUserName(username);
			options.setPassword(password.toCharArray());
			options.setMaxInflight(10000);
			options.setAutomaticReconnect(true);

			client.setCallback(new MqttCallbackExtended() {
				@Override
				public void connectComplete(boolean reconnect, String serverURI) {
					try {
						client.subscribe("loon/player/+/inventory/full/request");
						client.subscribe("loon/player/+/online/request");
						client.subscribe("loon/player/+/position/request");
						client.subscribe("loon/world/chunks/+/+/request");
						client.subscribe("loon/server/info/request");

						if (reconnect)
							Loon.LOGGER.info("MQTT reconnected");
						else
							Loon.LOGGER.info("MQTT connected");

					} catch (MqttException e) {
						throw new RuntimeException(e);
					}
				}

				@Override
				public void connectionLost(Throwable cause) {
					Loon.LOGGER.warn("MQTT connection lost, reconnecting...", cause);
				}

				@Override
				public void messageArrived(String topic, MqttMessage message) {
					if (topic.matches("loon/player/[^/]+/inventory/full/request")) {
						Pattern pattern = Pattern.compile("loon/player/([^/]+)/inventory/full/request");
						Matcher matcher = pattern.matcher(topic);

						if (matcher.matches()) {
							UUID playerId = parsePlayerUuid(matcher.group(1));
							if (playerId == null)
								return;

							RequestManager.addRequest(new InventoryRequest(playerId));
						}
					}

					if (topic.matches("loon/player/[^/]+/online/request")) {
						Pattern pattern = Pattern.compile("loon/player/([^/]+)/online/request");
						Matcher matcher = pattern.matcher(topic);

						if (matcher.matches()) {
							UUID playerId = parsePlayerUuid(matcher.group(1));
							if (playerId == null)
								return;

							RequestManager.addRequest(new PlayerOnlineRequest(playerId));
						}
					}

					if (topic.matches("loon/player/[^/]+/position/request")) {
						Pattern pattern = Pattern.compile("loon/player/([^/]+)/position/request");
						Matcher matcher = pattern.matcher(topic);

						if (matcher.matches()) {
							UUID playerId = parsePlayerUuid(matcher.group(1));
							if (playerId == null)
								return;

							RequestManager.addRequest(new PlayerPositionRequest(playerId));
						}
					}

					Pattern pattern = Pattern.compile("loon/world/chunks/(-?\\d+):(-?\\d+)/(-?\\d+):(-?\\d+)/request");
					Matcher matcher = pattern.matcher(topic);

					if (matcher.matches())
					{
						int xStart = Integer.parseInt(matcher.group(1));
						int xEnd = Integer.parseInt(matcher.group(2));
						int zStart = Integer.parseInt(matcher.group(3));
						int zEnd = Integer.parseInt(matcher.group(4));

						ChunkMapRequest request = new ChunkMapRequest(xStart, xEnd, zStart, zEnd);

						RequestManager.addRequest(request);
					}

					if (topic.equals("loon/server/info/request")) {
						RequestManager.addRequest(new ServerInfoRequest());
					}
				}

				@Override
				public void deliveryComplete(IMqttDeliveryToken token) {

				}
			});

			client.connect(options);
		} catch (MqttException e) {
			throw new RuntimeException(e);
		}
	}

	private static UUID parsePlayerUuid(String raw) {
		try {
			return UUID.fromString(raw);
		} catch (IllegalArgumentException e) {
			Loon.LOGGER.warn("Ignoring MQTT request with invalid player UUID: {}", raw);
			return null;
		}
	}
}
