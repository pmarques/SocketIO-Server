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
import org.jboss.netty.handler.codec.http.CookieEncoder;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.handler.codec.http.QueryStringDecoder;
import org.jboss.netty.util.CharsetUtil;

import eu.k2c.socket.io.server.api.SocketIOInbound;
import eu.k2c.socket.io.server.transport.SocketIOTransport;
import eu.k2c.socket.io.server.transport.SocketIOTransportManager;
import eu.k2c.socket.io.utils.Utils;

/**
 * @author "Patrick F. Marques <patrick.marques@k2c.eu>"
 */
public class SocketIOServer extends SimpleChannelUpstreamHandler {
	private static final Logger LOGGER = Logger.getLogger(SocketIOServer.class);

	public static final String WEBSOCKET_PATH = "/socket.io/";
	public static final String JSESSIONID = "WSESSIONID";

	private static final String transportList = SocketIOTransportManager.getTransportsString();
	private static final int timeout = SocketIOTransportManager.getSocketIOTimeOut();
	private static final int expireTime = SocketIOTransportManager.getSocketIOExpireSession();
	public boolean https;

	private Class<? extends SocketIOInbound> clazz;

	public SocketIOServer(Class<? extends SocketIOInbound> clazz, final boolean https) {
		super();
		this.clazz = clazz;
		this.https = https;
	}

	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
		LOGGER.trace("messageReceived");

		Object msg = e.getMessage();
		if (msg instanceof HttpRequest) {
			handleHttpRequest(ctx, (HttpRequest) msg);
			return;
		} else {
			throw new UnsupportedOperationException();
		}
	}

	private void handleHttpRequest(ChannelHandlerContext ctx, HttpRequest req) throws Exception {
		LOGGER.trace("handleHttpRequest");

		HttpMethod method = req.getMethod();
		if (!HttpMethod.GET.equals(method) && !HttpMethod.POST.equals(method)) {
			final HttpResponseStatus status = new HttpResponseStatus(BAD_REQUEST.getCode(), "Socket.IO invalid method'"
					+ method + "'");
			sendHttpResponse(ctx, req, new DefaultHttpResponse(HTTP_1_1, status));
		}

		QueryStringDecoder decoder = new QueryStringDecoder(req.getUri());
		LOGGER.debug("Request URI[" + req.getUri() + "]");
		String path = decoder.getPath();

		/* Socket.IO Specifies that path starts with '/socket.io/' */
		if (!path.startsWith(WEBSOCKET_PATH)) {
			final HttpResponseStatus status = new HttpResponseStatus(BAD_REQUEST.getCode(),
					"Socket.IO path is invalid '" + path + "'");
			sendHttpResponse(ctx, req, new DefaultHttpResponse(HTTP_1_1, status));
			return;
		}

		/*
		 * remove first part of path and its slashes, otherwise initial tokens
		 * will be ignored
		 */
		path = path.replace(WEBSOCKET_PATH, "");

		/* Ignore empty requests */
		if (path == null || path.length() == 0 || "/".equals(path)) {
			final HttpResponseStatus status = new HttpResponseStatus(BAD_REQUEST.getCode(),
					"Missing SocketIO transport");
			sendHttpResponse(ctx, req, new DefaultHttpResponse(HTTP_1_1, status));
			return;
		}

		/* split parameters */
		final String[] parts = path.split("/");

		/*
		 * If only one parameter is provided, this is a new non-handshake
		 * connection. The parameter gives the Socket.IO version.
		 */
		if (parts.length == 1) {
			final int version = Integer.parseInt(parts[0]);

			/* Handle only supported socket.IO versions */
			switch (version) {
				case 1:
					// done above...
					break;
				default:
					HttpResponseStatus status = new HttpResponseStatus(NOT_IMPLEMENTED.getCode(), "Verion " + version
							+ " is not supported yet");
					sendHttpResponse(ctx, req, new DefaultHttpResponse(HTTP_1_1, status));
					return;
			}

			final HttpResponse response = new DefaultHttpResponse(HTTP_1_1, OK);

			final String sessionID = Utils.getUniqueSocketIOSessionID();

			// Response body
			String hs = sessionID + ":" + timeout + ":" + expireTime + ":" + transportList;

			List<String> tmpParam = decoder.getParameters().get("jsonp");

			if (tmpParam != null) {
				if (!tmpParam.isEmpty()) {
					String jsonpIdx = tmpParam.get(0);
					hs = "io.j[" + jsonpIdx + "](\"" + hs + "\");";
				}
			}

			response.addHeader("Access-Control-Allow-Origin", "*");
			response.addHeader(HttpHeaders.Names.CONTENT_TYPE, "application/javascript; charset=UTF-8");

			// Add session cookie.
			// TODO: make cookie persistent? create a Cookie and set its
			// properties
			CookieEncoder cookieEnc = new CookieEncoder(true);
			cookieEnc.addCookie(JSESSIONID, sessionID);
			response.setHeader(HttpHeaders.Names.SET_COOKIE, cookieEnc.encode());

			final ChannelBuffer content = ChannelBuffers.copiedBuffer(hs, CharsetUtil.US_ASCII);
			setContentLength(response, content.readableBytes());
			response.setContent(content);
			sendHttpResponse(ctx, req, response);
			return;
		}

		/*
		 * If a socket.IO versions and protocol are specified do the handshake
		 */
		if (parts.length >= 2) {
			final String transportName = parts[1];
			final String sessionsID = parts[2];

			/* Get transport handler */
			SocketIOTransport transport = SocketIOTransportManager.get(transportName);
			if (transport == null) {
				final HttpResponseStatus status = new HttpResponseStatus(FORBIDDEN.getCode(),
						"Unknow Socket.IO Transport '" + transportName + "'");
				final HttpResponse res = new DefaultHttpResponse(HTTP_1_1, status);
				sendHttpResponse(ctx, req, res);
				return;
			}

			/* Validate session bebore handshake */
			final SocketIOInbound inbound = clazz.newInstance();
			if (!inbound.validate(req.getUri())) {
				final HttpResponseStatus status = new HttpResponseStatus(FORBIDDEN.getCode(),
						"SocketIO Validation failed '" + transportName + "'");
				final HttpResponse res = new DefaultHttpResponse(HTTP_1_1, status);
				sendHttpResponse(ctx, req, res);
				return;
			}

			/* do handshake */
			final HttpResponse res = new DefaultHttpResponse(HTTP_1_1, OK);

			/* Create a Socket.IO session */
			SocketIOSession session = new SocketIOSession(sessionsID, inbound);

			transport.handleRequest(ctx, req, res, session, https);
			return;
		}

		// Send an error page otherwise.
		sendHttpResponse(ctx, req, new DefaultHttpResponse(HTTP_1_1, FORBIDDEN));
	}

	/**
	 * Send a response to the client
	 * 
	 * @param ctx
	 * @param req
	 * @param res
	 */
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

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
		LOGGER.trace("exceptionCaught");
		e.getCause().printStackTrace();
		e.getChannel().close();
	}
}
