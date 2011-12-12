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
package eu.k2c.socket.io.ci;

import eu.k2c.socket.io.server.api.AbstractSocketIOHandler;
import eu.k2c.socket.io.server.api.DisconnectReason;
import eu.k2c.socket.io.server.api.SocketIOOutbound;
import eu.k2c.socket.io.server.api.SocketIOSessionEventRegister;
import eu.k2c.socket.io.server.api.SocketIOSessionNSRegister;

public abstract class AbstractHandler extends AbstractSocketIOHandler {
	@Override
	public boolean validate(String URI) {
		// This isn't belongs to socketIO Spec AFAIK
		return true;
	}

	@Override
	public void onConnect(final SocketIOOutbound outbound, final SocketIOSessionEventRegister eventRegister,
			final SocketIOSessionNSRegister NSRegister) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void onDisconnect(final DisconnectReason reason, final String errorMessage) {
	}

	@Override
	public void onMessage(final long messageID, final String message) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void onJSONMessage(final long messageID, final String message) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void onAck(final long messageID, final String data) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void onError(final String reason, final String advice) {
	}
}
