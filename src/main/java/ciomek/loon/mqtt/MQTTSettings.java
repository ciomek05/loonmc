package ciomek.loon.mqtt;

import ciomek.loon.Loon;

public record MQTTSettings (
	String broker,
	String username,
	String password,
	String clientId
)
{
	public static MQTTSettings getFromEnvVars()
	{
		return new MQTTSettings(getEnvVar("broker"), getEnvVar("username"), getEnvVar("password"), getEnvVar("clientid"));
	}

	static String getEnvVar(String name)
	{
		String fullName = Loon.EnvVarPrefix + name.toUpperCase();

		String value = System.getenv(fullName);

		if  (value == null || value.isBlank())
			throw new IllegalArgumentException(
				"Missing environment variable: " + fullName + ". "
			);

		return value;
	}
}
