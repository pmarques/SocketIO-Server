package eu.k2c.socket.io.server;

import static org.jboss.netty.handler.codec.http.HttpHeaders.isKeepAlive;
import static org.jboss.netty.handler.codec.http.HttpHeaders.setContentLength;
import static org.jboss.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST;
import static org.jboss.netty.handler.codec.http.HttpResponseStatus.FORBIDDEN;
import static org.jboss.netty.handler.codec.http.HttpResponseStatus.NOT_IMPLEMENTED;
import static org.jboss.netty.handler.codec.http.HttpResponseStatus.OK;
import static org.jboss.netty.handler.codec.http.HttpVersion.HTTP_1_1;

import java.util.List;

import org.apache.log4j.Logger;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.handler.codec.http.QueryStringDecoder;
import org.jboss.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import org.jboss.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import org.jboss.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.jboss.netty.handler.codec.http.websocketx.WebSocketFrame;
import org.jboss.netty.util.CharsetUtil;

import eu.k2c.socket.io.server.api.SocketIOInbound;
import eu.k2c.socket.io.server.transport.SocketIOTransport;
import eu.k2c.socket.io.server.transport.SocketIOTransportManager;
import eu.k2c.socket.io.utils.Utils;

public class SocketIOServer extends SimpleChannelUpstreamHandler {
    private static final Logger LOGGER = Logger.getLogger(SocketIOServer.class);

    private static final String NEWLINE = "\r\n";

    public static final String WEBSOCKET_PATH = "/socket.io/";

    private static final SocketIOSessionManager sessionManager = SocketIOSessionManager.getInstace();

    private Class<? extends SocketIOInbound> clazz;

    public SocketIOServer(Class<? extends SocketIOInbound> clazz) {
	super();
	LOGGER.trace("SocketIONetty");
	this.clazz = clazz;
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
	LOGGER.trace("messageReceived");

	Object msg = e.getMessage();
	if (msg instanceof HttpRequest) {
	    handleHttpRequest(ctx, (HttpRequest) msg);
	    return;
	} else if (msg instanceof WebSocketFrame) {
	    handleWebSocketFrame(ctx, (WebSocketFrame) msg);
	} else {
	    throw new UnsupportedOperationException();
	    // super.handleUpstream(ctx, e);
	}
    }

    private void handleHttpRequest(ChannelHandlerContext ctx, HttpRequest req) throws Exception {
	LOGGER.trace("handleHttpRequest");

	QueryStringDecoder decoder = new QueryStringDecoder(req.getUri());
	LOGGER.debug("Request URI[" + req.getUri() + "]");
	String path = decoder.getPath();

	if (!path.startsWith(WEBSOCKET_PATH)) {
	    final HttpResponseStatus status = new HttpResponseStatus(BAD_REQUEST.getCode(),
		    "Socket.IO path is invalid '" + path + "'");
	    sendHttpResponse(ctx, req, new DefaultHttpResponse(HTTP_1_1, status));
	    return;
	}

	path = path.replace(WEBSOCKET_PATH, "");

	if (path == null || path.length() == 0 || "/".equals(path)) {
	    final HttpResponseStatus status = new HttpResponseStatus(BAD_REQUEST.getCode(),
		    "Missing SocketIO transport");
	    sendHttpResponse(ctx, req, new DefaultHttpResponse(HTTP_1_1, status));
	    return;
	}

	if (path.startsWith("/"))
	    path = path.substring(1);

	final String[] parts = path.split("/");

	if (parts.length == 1) {
	    final String version = parts[0];

	    /* if version is not supported stop here */
	    if (!("1".equals(version))) {
		HttpResponseStatus status = new HttpResponseStatus(NOT_IMPLEMENTED.getCode(), "Verion " + version
			+ " is not supported yet");
		sendHttpResponse(ctx, req, new DefaultHttpResponse(HTTP_1_1, status));
		return;
	    }

	    final HttpResponse response = new DefaultHttpResponse(HTTP_1_1, OK);

	    final String uuid = Utils.getUniqueSocketIOSessionID();
	    final String transportList = SocketIOTransportManager.getTransportsString();
	    final int timeout = sessionManager.getSocketIOTimeOut();
	    final int expireTime = sessionManager.getSocketIOExpireSession();

	    final String hs = uuid + ":" + timeout + ":" + expireTime + ":" + transportList;
	    String result = null;

	    List<String> tmpParam = decoder.getParameters().get("jsonp");

	    if (tmpParam.isEmpty()) {
		result = hs;
	    } else {
		String jsonpIdx = tmpParam.get(0);
		result = "io.j[" + jsonpIdx + "](\"" + hs + "\");";
	    }
	    response.addHeader("Content-Type", "application/javascript; charset=UTF-8");

	    final ChannelBuffer content = ChannelBuffers.copiedBuffer(result, CharsetUtil.US_ASCII);
	    setContentLength(response, content.readableBytes());
	    response.setContent(content);
	    sendHttpResponse(ctx, req, response);
	    return;
	}

	if (parts.length >= 2) {
	    final String transportName = parts[1];
	    final String sessionsID = parts[2];
	    SocketIOTransport transport = SocketIOTransportManager.get(transportName);

	    if (transport == null) {
		final HttpResponseStatus status = new HttpResponseStatus(FORBIDDEN.getCode(),
			"Unknow Socket.IO Transport '" + transportName + "'");
		final HttpResponse res = new DefaultHttpResponse(HTTP_1_1, status);
		sendHttpResponse(ctx, req, res);
		return;
	    }

	    final HttpResponse res = new DefaultHttpResponse(HTTP_1_1, OK);
	    final SocketIOInbound inbound = clazz.newInstance();
	    final SocketIOSession session = new SocketIOSession(sessionsID, inbound);
	    transport.handleRequest(ctx, req, res, session);
	    ctx.getChannel().write(new TextWebSocketFrame("1::"));
	    return;
	}

	// Send an error page otherwise.
	sendHttpResponse(ctx, req, new DefaultHttpResponse(HTTP_1_1, FORBIDDEN));
    }

    private void sendHttpResponse(ChannelHandlerContext ctx, HttpRequest req, HttpResponse res) {
	LOGGER.trace("sendHttpResponse");

	// Generate an error page if response status code is not OK (200).
	if (res.getStatus().getCode() != 200) {
	    res.setContent(ChannelBuffers.copiedBuffer(res.getStatus().toString(), CharsetUtil.UTF_8));
	    setContentLength(res, res.getContent().readableBytes());
	}

	// Send the response and close the connection if necessary.
	ChannelFuture f = ctx.getChannel().write(res);
	if (!isKeepAlive(req) || res.getStatus().getCode() != 200)
	    f.addListener(ChannelFutureListener.CLOSE);
    }

    private void handleWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame) {

	// Check for closing frame
	if (frame instanceof CloseWebSocketFrame) {
	    // this.handshaker.executeClosingHandshake(ctx,
	    // (CloseWebSocketFrame) frame);
	    return;
	} else if (frame instanceof PingWebSocketFrame) {
	    // ctx.getChannel().write(new
	    // PongWebSocketFrame(frame.getBinaryData()));
	    return;
	} else if (!(frame instanceof TextWebSocketFrame)) {
	    throw new UnsupportedOperationException(String.format("%s frame types not supported", frame.getClass()
		    .getName()));
	}

	// Send the uppercase string back.
	String request = ((TextWebSocketFrame) frame).getText();
	LOGGER.debug(String.format("Channel %s received %s", ctx.getChannel().getId(), request));
	ctx.getChannel().write(new TextWebSocketFrame(request.toUpperCase()));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
	LOGGER.trace("exceptionCaught");

	e.getCause().printStackTrace();
	e.getChannel().close();
    }
}
