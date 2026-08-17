package ciomek.loon.utils;

import de.mkammerer.argon2.Argon2Factory;

public class PasswordHasher {
	public static String hash(String password) {
		var argon2 = Argon2Factory.create();
		return argon2.hash(3, 65536, 1, password.toCharArray());
	}
}
