package eu.k2c.socket.io.server;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.log4j.Logger;

public class SocketIOSessionManager {
    private static final Logger LOGGER = Logger.getLogger(SocketIOSessionManager.class);
    private static final SocketIOSessionManager instance = new SocketIOSessionManager();
    private static final ConcurrentMap<String, SocketIOSession> socketIOSessions = new ConcurrentHashMap<String, SocketIOSession>();

    public static SocketIOSessionManager getInstace() {
	return instance;
    }

    private SocketIOSessionManager() {
    }

    public SocketIOSession createSession() {
	LOGGER.trace("SocketIOSession");
	final SocketIOSession impl = null;
	final String sessionID = null;
	socketIOSessions.put(sessionID, impl);
	return impl;
    }

    public int getSocketIOTimeOut() {
	return 15;
    }

    public int getSocketIOExpireSession() {
	return 25;
    }
}
