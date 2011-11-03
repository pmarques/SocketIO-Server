package eu.k2c.socket.io.server.transport;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;

import eu.k2c.socket.io.server.SocketIOSession;

public interface SocketIOTransport {
    void handleRequest(ChannelHandlerContext ctx, HttpRequest req, HttpResponse res, SocketIOSession session);
}
