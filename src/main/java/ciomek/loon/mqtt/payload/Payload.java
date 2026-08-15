package ciomek.loon.mqtt.payload;

import ciomek.loon.mqtt.MQTTClient;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

public interface Payload {
	ObjectMapper MAPPER = new ObjectMapper();

	List<String> topics();

	default String toJson() {
		try {
			MAPPER.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
			return MAPPER.writeValueAsString(this);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}

	default void send()
	{
		topics().forEach(topic -> {
			MQTTClient.getInstance().publish(
				topic,
				this
			);
		});
	}
}
