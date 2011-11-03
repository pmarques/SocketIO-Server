package eu.k2c.socket.io.server;

import org.jboss.netty.buffer.ChannelBuffer;

import eu.k2c.socket.io.server.api.SocketIOEventHandler;
import eu.k2c.socket.io.server.api.SocketIOInbound;

public class SocketIOSession implements WebSocketMessageHandler {
    private final String sessionID;
    private final SocketIOInbound inbound;

    public SocketIOSession(final String sessionID, final SocketIOInbound inbound) {
	this.sessionID = sessionID;
	this.inbound = inbound;
    }

    public WebSocketMessageHandler getHandler() {
	return this;
    }

    public void onTextMessage(String text) {
    }

    public void onBinaryMessage(ChannelBuffer binaryData) {
    }

    public interface SocketIOSessionEventRegister {
	/**
	 * Register a event listener for client session. When a event with a
	 * {@link evenName} is received, this is send to the {@link handler}. If
	 * there is no event listener for the coming event this is will be sent
	 * to the {@link SocketIOSession#onEvent}
	 * 
	 * @param eventName
	 *            Event name to be added.
	 * @param handler
	 *            Event listener.
	 */
	void registerEventListeners(final String eventName, final SocketIOEventHandler handler);

	/**
	 * Remove event listener from client session
	 * 
	 * @param eventName
	 *            Event Name to be removed.
	 */
	void unRegisterEventListeners(final String eventName);

	/**
	 * Remove all event listeners from the client session.
	 */
	void removeEventListeners();
    }
}
