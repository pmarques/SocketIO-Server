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

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jboss.netty.handler.codec.http.QueryStringDecoder;

import eu.k2c.socket.io.ci.AbstractHandler;
import eu.k2c.socket.io.server.api.SocketIOOutbound;
import eu.k2c.socket.io.server.api.SocketIOSessionEventRegister;
import eu.k2c.socket.io.server.api.SocketIOSessionNSRegister;
import eu.k2c.socket.io.server.exceptions.SocketIOException;

public class UC17Handler extends AbstractHandler {
	private static final Logger LOGGER = Logger.getLogger(UC17Handler.class);

	// 'test sending query strings to the server'
	private String URI;

	@Override
	public boolean validate(final String URI) {
		this.URI = URI;
		return true;
	}

	@Override
	public void onConnect(final SocketIOOutbound outbound, final SocketIOSessionEventRegister eventRegister,
			final SocketIOSessionNSRegister NSRegister) {
		try {
			final QueryStringDecoder dec = new QueryStringDecoder(URI);
			final Map<String, List<String>> params = dec.getParameters();
			final String foo = params.get("foo").get(0);
			outbound.sendJSONMessage("{\"query\":{\"foo\":\"" + foo + "\"}}", null);
		} catch (SocketIOException e) {
			LOGGER.fatal(e);
		}
	}
}
