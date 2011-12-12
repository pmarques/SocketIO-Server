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
package eu.k2c.socket.io.server.api;

/**
 * This Class implements the event and namespace logic.
 * 
 * @author "Patrick F. Marques <patrick.marques@k2c.eu>"
 */
public abstract class AbstractSocketIOHandler implements SocketIOInboundEvents {
	private SocketIOSessionEventRegister eventRegister = new SocketIOSessionEventRegisterImpl();
	private SocketIOSessionNSRegister nsRegister = new SocketIOSessionNSRegisterImpl();

	public abstract void onConnect(final SocketIOOutbound outbound, final SocketIOSessionEventRegister eventRegister,
			final SocketIOSessionNSRegister NSRegister);

	/**
	 * Handle the connection event and send the above one that needs to be
	 * implemented by the user(server) handling application.
	 * 
	 * This shall be final, if you don't need the event handling use/implement
	 * the {@link SocketIOInbound} instead extending this class
	 */
	public void onConnect(final SocketIOOutbound outbound, final String endPoint) {
		if (endPoint == null) {
			this.onConnect(outbound, eventRegister, nsRegister);
			return;
		}

		SocketIONSHandler handler = nsRegister.getHandler(endPoint);
		if (handler != null) {
			handler.onConnect(outbound);
		}

		// TODO: Ignore message?
		return;
	}

	/**
	 * Handle the events and send to the corresponding event handler. If the
	 * event doesn't have any handler,
	 * 
	 * 
	 * This shall be final, if you don't need the event handling use/implement
	 * the {@link SocketIOInbound} instead extendig this class
	 */
	public final void onEvent(final long messageID, final String endPoint, final String eventName, final String data) {
		final SocketIOEventHandler eventHandler = eventRegister.getHandler(eventName);
		if (eventHandler != null) {
			eventHandler.onEvent(messageID, endPoint, data);
		}

		// TODO: Ignore event?
		return;
	}

	public void onDisconnect(final DisconnectReason reason, final String errorMessage, final String endPoint) {
		if (endPoint == null) {
			this.onDisconnect(reason, errorMessage);
			return;
		}

		SocketIONSHandler handler = nsRegister.getHandler(endPoint);
		if (handler != null) {
			handler.onDisconnect(reason, errorMessage);
		}

		// TODO: Ignore message?
		return;
	}

	public void onMessage(final long messageID, final String endPoint, final String message) {
		if (endPoint == null) {
			this.onMessage(messageID, message);
			return;
		}

		SocketIONSHandler handler = nsRegister.getHandler(endPoint);
		if (handler != null) {
			handler.onMessage(messageID, message);
		}

		// TODO: Ignore message?
		return;
	}

	public void onJSONMessage(final long messageID, final String endPoint, final String message) {
		if (endPoint == null) {
			this.onJSONMessage(messageID, message);
			return;
		}

		SocketIONSHandler handler = nsRegister.getHandler(endPoint);
		if (handler != null) {
			handler.onJSONMessage(messageID, message);
		}

		// TODO: Ignore message?
		return;
	}

	public void onError(final String endPoint, final String reason, final String advice) {
		if (endPoint == null) {
			this.onError(reason, advice);
			return;
		}

		SocketIONSHandler handler = nsRegister.getHandler(endPoint);
		if (handler != null) {
			handler.onError(reason, advice);
		}

		// TODO: Ignore message?
		return;
	}

	public abstract void onDisconnect(final DisconnectReason reason, final String errorMessage);

	public abstract void onMessage(final long messageID, final String message);

	public abstract void onJSONMessage(final long messageID, final String message);

	public abstract void onError(final String reason, final String advice);
}
