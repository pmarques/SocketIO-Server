package eu.k2c.socket.io.utils;

import java.util.UUID;

public class Utils {

	public static String getUniqueSocketIOSessionID() {
		return UUID.randomUUID().toString();
	}
}
