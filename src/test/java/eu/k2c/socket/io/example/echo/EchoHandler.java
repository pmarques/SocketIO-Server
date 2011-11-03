package eu.k2c.socket.io.example.echo;

import org.apache.log4j.Logger;

import eu.k2c.socket.io.server.SocketIOSession;
import eu.k2c.socket.io.server.api.DisconnectReason;
import eu.k2c.socket.io.server.api.SocketIOInbound;
import eu.k2c.socket.io.server.api.SocketIOOutbound;
import eu.k2c.socket.io.server.exceptions.SocketIOException;

public class EchoHandler extends SocketIOInbound {
	private static final Logger LOGGER = Logger.getLogger(EchoHandler.class);
	private SocketIOOutbound outbound;

	@Override
	public void onConnect(final SocketIOOutbound outbound,
			final SocketIOSession.SocketIOSessionEventRegister register) {
		this.outbound = outbound;
	}

	@Override
	public void onDisconnect(final DisconnectReason reason,
			final String errorMessage) {
		LOGGER.debug("onDisconnect");
	}

	@Override
	public void onMessage(final long messageID, final String endPoint,
			final String message) {
		LOGGER.debug("onMessage");
		try {
			outbound.sendJSONMessage("ECHO: " + message);
		} catch (SocketIOException e) {
			e.printStackTrace();
			outbound.close();
		}
	}

	@Override
	public void onJSONMessage(final long messageID, final String endPoint,
			final String message) {
		LOGGER.debug("onJSONMessage");
	}

	@Override
	public void onEvent(final long messageID, final String endPoint,
			final String eventName, final String data) {
		LOGGER.debug("onEvent");
	}

	@Override
	public void onAck(final long messageID, final String data) {
		LOGGER.debug("onAck");
	}

	@Override
	public void onError(final String endPoint, final String reason,
			final String advice) {
		LOGGER.debug("onError");
	}

}
