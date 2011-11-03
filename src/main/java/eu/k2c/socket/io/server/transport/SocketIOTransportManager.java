package eu.k2c.socket.io.server.transport;

import java.util.HashMap;
import java.util.Map;

public class SocketIOTransportManager {
    private static final Map<String, SocketIOTransport> transportList;
    private static String transportsString = "";

    /**
     * Search for supported transport protocols
     */
    static {
	transportList = new HashMap<String, SocketIOTransport>();
	transportList.put("websocket", new WebSocketTransport());
	transportsString = "websocket";
    }

    public static SocketIOTransport get(final String tranposrtName) {
	return transportList.get(tranposrtName);
    }

    public static String getTransportsString() {
	return transportsString;

    }
}
