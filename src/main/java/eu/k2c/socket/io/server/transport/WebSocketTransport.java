package eu.k2c.socket.io.server.transport;

import org.apache.log4j.Logger;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import org.jboss.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;

import eu.k2c.socket.io.example.echo.EchoServerNettyTest;
import eu.k2c.socket.io.server.SocketIOServer;
import eu.k2c.socket.io.server.SocketIOSession;

public class WebSocketTransport implements SocketIOTransport {
    private static final Logger LOGGER = Logger.getLogger(WebSocketTransport.class);

    private WebSocketServerHandshaker handshaker = null;

    @Override
    public void handleRequest(ChannelHandlerContext ctx, HttpRequest req, HttpResponse res, SocketIOSession session) {
	WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(
		this.getWebSocketLocation(req), null, false);
	this.handshaker = wsFactory.newHandshaker(ctx, req);
	if (this.handshaker == null) {
	    wsFactory.sendUnsupportedWebSocketVersionResponse(ctx);
	} else {
	    this.handshaker.executeOpeningHandshake(ctx, req);
	}
    }

    private String getWebSocketLocation(HttpRequest req) {
	final String proto = EchoServerNettyTest.ssl ? "wss://" : "wss://";
	return proto + req.getHeader(HttpHeaders.Names.HOST) + SocketIOServer.WEBSOCKET_PATH;
    }
}
