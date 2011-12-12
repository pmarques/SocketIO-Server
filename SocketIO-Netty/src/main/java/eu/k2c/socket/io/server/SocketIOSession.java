/**
 * Copyright (C) 2011 K2C @ Patrick Marques <patrickfmarques@gmail.com>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 * Except as contained in this notice, the name(s) of the above copyright holders
 * shall not be used in advertising or otherwise to promote the sale, use or other
 * dealings in this Software without prior written authorization.
 */
package eu.k2c.socket.io.server;

import static eu.k2c.socket.io.frames.SocketIOFrame.EMPTY_FIELD;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.log4j.Logger;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.handler.timeout.ReadTimeoutException;

import eu.k2c.socket.io.frames.FrameType;
import eu.k2c.socket.io.frames.SocketIOFrame;
import eu.k2c.socket.io.server.api.AckHandler;
import eu.k2c.socket.io.server.api.ConnectionState;
import eu.k2c.socket.io.server.api.DisconnectReason;
import eu.k2c.socket.io.server.api.SocketIOInbound;
import eu.k2c.socket.io.server.api.SocketIOOutbound;
import eu.k2c.socket.io.server.exceptions.SocketIOException;
import eu.k2c.socket.io.server.exceptions.SocketIOMalformedMessageException;

/**
 * This class only deals with the transport layer of SocketIO. It read and
 * interpret the raw messages format and the sends it to the session
 * implementation
 * 
 * @author "Patrick F. Marques <patrick.marques@k2c.eu>"
 */
public class SocketIOSession extends SimpleChannelUpstreamHandler implements SocketIOOutbound {
	private static final Logger LOGGER = Logger.getLogger(SocketIOServer.class);

	private final String sessionID;
	private final SocketIOInbound inbound;
	private Channel channel = null;
	private ConnectionState connectionState;

	private AtomicLong messageIDSeq = new AtomicLong(0);

	// TODO: View "maps" performance and select the best one...
	private final Map<Long, AckHandler> ackHandlers = new IdentityHashMap<Long, AckHandler>(100);

	public SocketIOSession(final String sessionID, final SocketIOInbound inbound) {
		this.sessionID = sessionID;
		this.inbound = inbound;
		connectionState = ConnectionState.CONNECTING;
	}

	@Override
	public void messageReceived(final ChannelHandlerContext ctx, final MessageEvent e) throws Exception {
		Object msg = e.getMessage();
		if (!(msg instanceof SocketIOFrame)) {
			throw new UnsupportedOperationException();
		}

		SocketIOFrame frame = (SocketIOFrame) msg;

		LOGGER.trace(frame.toString());

		final long messageID = frame.getMessageID();
		final String endPoint = frame.getEndPoint();
		final String eventName = frame.getEventName();
		final String data = frame.getData();
		final boolean ackForm = frame.getTreasAsEvent();

		switch (frame.getFrameType()) {
			case CONNECT:
				onConnect(endPoint);
				break;
			case EVENT:
				inbound.onEvent(messageID, endPoint, eventName, data);
				break;
			case MESSAGE:
				returnAck(messageID);
				inbound.onMessage(messageID, endPoint, data);
				break;
			case JSON:
				returnAck(messageID);
				inbound.onJSONMessage(messageID, endPoint, data);
				break;
			case ACK:
				onAck(messageID, data, ackForm);
				break;
			case HEARTBEAT:
				// The hearbeat is catched by the Netty ReadTimeoutHandler.
				break;
			case DISCONNECT:
				final DisconnectReason reason = DisconnectReason.CLOSED_REMOTELY;
				final String errorMessage = "Session closed by user";
				inbound.onDisconnect(reason, errorMessage);
				break;
			default:
				// Never should reach this point...
				throw new UnsupportedOperationException("Methode: [" + frame.getFrameType().toString()
						+ "]Session ID: " + sessionID);
		}
	}

	private void returnAck(final long messageID) {
		channel.write(new SocketIOFrame(FrameType.ACK, messageID, null));
	}

	private void onConnect(final String endPoint) {
		channel.write(new SocketIOFrame(FrameType.CONNECT, endPoint));

		inbound.onConnect(this, endPoint);
	}

	private void onAck(final long messageID, final String data, final boolean ackForm) {
		if (ackForm) {
			if (ackHandlers.containsKey(messageID))
				ackHandlers.get(messageID).onEvent(data);
			else
				// TODO: This can happen? what i do with it?
				throw new UnsupportedOperationException("something is wrong where, where is the ack handler?");
		} else {
			throw new UnsupportedOperationException();
		}

	}

	@Override
	public void disconnect() {
		try {
			channel.write(new SocketIOFrame(FrameType.DISCONNECT));
		} catch (SocketIOMalformedMessageException e) {
			LOGGER.fatal(e);
		}
	}

	@Override
	public void close() {
		throw new UnsupportedOperationException();
	}

	@Override
	public ConnectionState getConnectionState() {
		// TODO Auto-generated method stub
		return connectionState;
	}

	@Override
	public void sendMessage(final String message, final String endPoint) throws SocketIOException {
		channel.write(new SocketIOFrame(FrameType.MESSAGE, EMPTY_FIELD, endPoint, message));
	}

	@Override
	public void sendJSONMessage(final String message, final String endPoint) throws SocketIOException {
		channel.write(new SocketIOFrame(FrameType.JSON, EMPTY_FIELD, endPoint, message));
	}

	@Override
	public void sendEventMessage(final String eventName, final String message, final String endPoint)
			throws SocketIOException {
		channel.write(new SocketIOFrame(FrameType.EVENT, EMPTY_FIELD, endPoint, eventName, message));
	}

	@Override
	public void sendMessage(final String message, final String endPoint, final AckHandler handler)
			throws SocketIOException {
		long mid = messageIDSeq.getAndIncrement();
		ackHandlers.put((long) mid, handler);
		channel.write(new SocketIOFrame(FrameType.MESSAGE, mid, false, endPoint, null, message));
	}

	@Override
	public void sendJSONMessage(final String message, final String endPoint, final AckHandler handler)
			throws SocketIOException {
		long mid = messageIDSeq.getAndIncrement();
		ackHandlers.put((long) mid, handler);
		channel.write(new SocketIOFrame(FrameType.JSON, mid, false, endPoint, null, message));
	}

	@Override
	public void sendEventMessage(final String eventName, final String message, final String endPoint,
			final AckHandler handler) throws SocketIOException {
		long mid = messageIDSeq.getAndIncrement();
		ackHandlers.put((long) mid, handler);
		channel.write(new SocketIOFrame(FrameType.EVENT, mid, true, endPoint, eventName, message));
	}

	@Override
	public void sendAck(long messageID, String message) throws SocketIOException {
		channel.write(new SocketIOFrame(FrameType.ACK, messageID, message));
	}

	public void onHandshake(final ChannelHandlerContext ctx) {
		this.channel = ctx.getChannel();
		onConnect(null);
		connectionState = ConnectionState.CONNECTED;
	}

	@Override
	public void exceptionCaught(final ChannelHandlerContext ctx, final ExceptionEvent e) throws Exception {
		if (e.getCause() instanceof ReadTimeoutException) {
			LOGGER.debug("Send HeartBeat to client with SessionID[" + sessionID + "]");

			if (channel.isOpen()) {
				channel.write(new SocketIOFrame(FrameType.HEARTBEAT));
			} else {
				inbound.onDisconnect(DisconnectReason.TIMEOUT, "user not respond to timeouts");
			}

			return;
		}

		// FIX: Pass exception information to user is not a good practice!
		Throwable thr = e.getCause();
		inbound.onError(null, e.getCause().getMessage(), e.getCause().getMessage());
		LOGGER.fatal("Unknow exception behavior", thr);
	}
}
