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
package eu.k2c.socket.io.ci.usecases;

import java.io.UnsupportedEncodingException;

import org.apache.log4j.Logger;

import eu.k2c.socket.io.ci.AbstractHandler;
import eu.k2c.socket.io.server.api.DisconnectReason;
import eu.k2c.socket.io.server.api.SocketIONSHandler;
import eu.k2c.socket.io.server.api.SocketIOOutbound;
import eu.k2c.socket.io.server.api.SocketIOSessionEventRegister;
import eu.k2c.socket.io.server.api.SocketIOSessionNSRegister;
import eu.k2c.socket.io.server.exceptions.SocketIOException;

public class UC16Handler extends AbstractHandler {
	private static final Logger LOGGER = Logger.getLogger(UC17Handler.class);
	
	private static String text;

	static {
		final byte [] b2 = {(byte) 0xC3, (byte) 0xB1}; // \uC3B1
		try {
			text = new String(b2, "UTF8");
		} catch (UnsupportedEncodingException e) {
			LOGGER.fatal(e);
		}
	}

	@Override
	public void onConnect(final SocketIOOutbound outbound, final SocketIOSessionEventRegister eventRegister,
			final SocketIOSessionNSRegister NSRegister) {
		NSRegister.registerNSListeners("/woot", new SocketIONSHandler() {
			int count = 0;

			@Override
			public void onMessage(long messageID, String message) {
				if (text.equals(message)) {
					if (++count == 4) {
						try {
							outbound.sendEventMessage("done", "", "/woot");
						} catch (SocketIOException e) {
							LOGGER.fatal(e);
						}
					}
				}
			}

			@Override
			public void onJSONMessage(long messageID, String message) {
			}

			@Override
			public void onError(String reason, String advice) {
			}

			@Override
			public void onDisconnect(DisconnectReason reason, String errorMessage) {
			}

			@Override
			public void onConnect(SocketIOOutbound outbound) {
			}
		});
	}
}
