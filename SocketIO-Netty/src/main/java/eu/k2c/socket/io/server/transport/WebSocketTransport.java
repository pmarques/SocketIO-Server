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
package eu.k2c.socket.io.server.transport;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import org.jboss.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import org.jboss.netty.handler.timeout.ReadTimeoutHandler;
import org.jboss.netty.util.HashedWheelTimer;
import org.jboss.netty.util.Timer;

import eu.k2c.socket.io.frames.SocketIOFrameDecoder;
import eu.k2c.socket.io.frames.SocketIOFrameEncoder;
import eu.k2c.socket.io.server.SocketIOSession;

/**
 * @author "Patrick F. Marques <patrick.marques@k2c.eu>"
 */
public class WebSocketTransport implements SocketIOTransport {
	public static final String NAME = "websocket";

	private static final Timer timeoutTimer = new HashedWheelTimer();

	private WebSocketServerHandshaker handshaker = null;

	private static final int timeoutSeconds = SocketIOTransportManager.getSocketIOTimeOut();

	@Override
	public void handleRequest(final ChannelHandlerContext ctx, final HttpRequest req, final HttpResponse res,
			final SocketIOSession session, final boolean https) {
		WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(this.getWebSocketLocation(
				req, https), null, false);
		this.handshaker = wsFactory.newHandshaker(ctx, req);
		if (this.handshaker == null) {
			wsFactory.sendUnsupportedWebSocketVersionResponse(ctx);
		} else {
			this.handshaker.performOpeningHandshake(ctx, req);
		}

		/* Update pipelines to process socket.IO frames */
		ChannelPipeline pipe = ctx.getChannel().getPipeline();
		/* Add time out */
		pipe.addFirst("SocketIO-timeout", new ReadTimeoutHandler(timeoutTimer, timeoutSeconds));
		pipe.addAfter("wsdecoder", "SocketIO-decoder", new SocketIOFrameDecoder(handshaker));
		pipe.addAfter("wsencoder", "SocketIO-encoder", new SocketIOFrameEncoder());
		pipe.replace("handler", "SocketIO-handler", session);

		/* Initialize Socket.IO session with handshake info */
		session.onHandshake(ctx);
	}

	private String getWebSocketLocation(HttpRequest req, final boolean https) {
		final String proto = https ? "wss://" : "ws://";
		return proto + req.getHeader(HttpHeaders.Names.HOST) + req.getUri();
	}
}
