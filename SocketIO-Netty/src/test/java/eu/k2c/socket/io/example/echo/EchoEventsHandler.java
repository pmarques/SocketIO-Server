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
package eu.k2c.socket.io.example.echo;

import org.apache.log4j.Logger;

import eu.k2c.socket.io.server.api.AbstractSocketIOHandler;
import eu.k2c.socket.io.server.api.DisconnectReason;
import eu.k2c.socket.io.server.api.SocketIOEventHandler;
import eu.k2c.socket.io.server.api.SocketIOOutbound;
import eu.k2c.socket.io.server.api.SocketIOSessionEventRegister;
import eu.k2c.socket.io.server.api.SocketIOSessionNSRegister;
import eu.k2c.socket.io.server.exceptions.SocketIOException;

public class EchoEventsHandler extends AbstractSocketIOHandler {
	private static final Logger LOGGER = Logger.getLogger(EchoEventsHandler.class);
	private SocketIOOutbound outbound;
	private SocketIOSessionEventRegister eventRegister;

	@Override
	public boolean validate(String URI) {
		LOGGER.debug(URI);
		return true;
	}

	@Override
	public void onConnect(SocketIOOutbound outbound, SocketIOSessionEventRegister eventRegister,
			SocketIOSessionNSRegister NSRegister) {
		this.outbound = outbound;
		this.eventRegister = eventRegister;
	}

	@Override
	public void onDisconnect(final DisconnectReason reason, final String errorMessage) {
		LOGGER.info("onDisconnect");

		eventRegister.registerEventListeners("echo", new SocketIOEventHandler() {

			@Override
			public void onEvent(long messageID, String endPoint, String data) {
				LOGGER.info("Received a event of tpe 'echoEvent'");

				try {
					outbound.sendJSONMessage("ECHO (JSON Data): " + data, null);
				} catch (SocketIOException e) {
					e.printStackTrace();
					outbound.close();
				}
			}
		});
	}

	@Override
	public void onMessage(final long messageID, final String message) {
		LOGGER.info("onMessage");
	}

	@Override
	public void onJSONMessage(final long messageID, final String message) {
		LOGGER.info("onJSONMessage");
	}

	@Override
	public void onAck(final long messageID, final String data) {
		LOGGER.info("onAck");
	}

	@Override
	public void onError(final String reason, final String advice) {
		LOGGER.info("onError");
	}
}
