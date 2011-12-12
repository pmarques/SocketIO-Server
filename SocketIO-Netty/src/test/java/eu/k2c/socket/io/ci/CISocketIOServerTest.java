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

import static org.jboss.netty.channel.Channels.pipeline;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.handler.codec.http.HttpChunkAggregator;
import org.jboss.netty.handler.codec.http.HttpRequestDecoder;
import org.jboss.netty.handler.codec.http.HttpResponseEncoder;

import eu.k2c.socket.io.ci.usecases.UC01Handler;
import eu.k2c.socket.io.ci.usecases.UC02Handler;
import eu.k2c.socket.io.ci.usecases.UC03Handler;
import eu.k2c.socket.io.ci.usecases.UC04Handler;
import eu.k2c.socket.io.ci.usecases.UC05Handler;
import eu.k2c.socket.io.ci.usecases.UC06Handler;
import eu.k2c.socket.io.ci.usecases.UC07Handler;
import eu.k2c.socket.io.ci.usecases.UC08Handler;
import eu.k2c.socket.io.ci.usecases.UC09Handler;
import eu.k2c.socket.io.ci.usecases.UC10Handler;
import eu.k2c.socket.io.ci.usecases.UC11Handler;
import eu.k2c.socket.io.ci.usecases.UC12Handler;
import eu.k2c.socket.io.ci.usecases.UC13Handler;
import eu.k2c.socket.io.ci.usecases.UC14Handler;
import eu.k2c.socket.io.ci.usecases.UC15Handler;
import eu.k2c.socket.io.ci.usecases.UC16Handler;
import eu.k2c.socket.io.ci.usecases.UC17Handler;
import eu.k2c.socket.io.ci.usecases.UC18Handler;
import eu.k2c.socket.io.ci.usecases.UC19Handler;
import eu.k2c.socket.io.example.echo.EchoServerNettyTest;
import eu.k2c.socket.io.server.SocketIOServer;
import eu.k2c.socket.io.server.api.SocketIOInbound;

public class CISocketIOServerTest {
	private static final Logger LOGGER = Logger.getLogger(EchoServerNettyTest.class);

	private int port = 8080;

	private Class<? extends SocketIOInbound> classHdlr;

	public static boolean SSL_ENABLE = false;

	public static void main(String... args) throws Exception {
		int port = 8080;
		new CISocketIOServerTest(port, UC01Handler.class).start();
		new CISocketIOServerTest(++port, UC02Handler.class).start();
		new CISocketIOServerTest(++port, UC03Handler.class).start();
		new CISocketIOServerTest(++port, UC04Handler.class).start();
		new CISocketIOServerTest(++port, UC05Handler.class).start();
		new CISocketIOServerTest(++port, UC06Handler.class).start();
		new CISocketIOServerTest(++port, UC07Handler.class).start();
		new CISocketIOServerTest(++port, UC08Handler.class).start();
		new CISocketIOServerTest(++port, UC09Handler.class).start();
		new CISocketIOServerTest(++port, UC10Handler.class).start();
		new CISocketIOServerTest(++port, UC11Handler.class).start();
		new CISocketIOServerTest(++port, UC12Handler.class).start();
		new CISocketIOServerTest(++port, UC13Handler.class).start();
		new CISocketIOServerTest(++port, UC14Handler.class).start();
		new CISocketIOServerTest(++port, UC15Handler.class).start();
		new CISocketIOServerTest(++port, UC16Handler.class).start();
		new CISocketIOServerTest(++port, UC17Handler.class).start();
		new CISocketIOServerTest(++port, UC18Handler.class).start();
		new CISocketIOServerTest(++port, UC19Handler.class).start();
	}

	CISocketIOServerTest(final int port, Class<? extends SocketIOInbound> classHdlr) {
		this.port = port;
		this.classHdlr = classHdlr;
	}

	public void start() {
		// Configure the server.
		ServerBootstrap bootstrap = new ServerBootstrap(new NioServerSocketChannelFactory(
				Executors.newCachedThreadPool(), Executors.newCachedThreadPool()));

		// Set up the event pipeline factory.
		bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
			public ChannelPipeline getPipeline() throws Exception {
				ChannelPipeline pipeline = pipeline();

				pipeline.addLast("decoder", new HttpRequestDecoder());
				pipeline.addLast("aggregator", new HttpChunkAggregator(65536));
				pipeline.addLast("encoder", new HttpResponseEncoder());
				pipeline.addLast("handler", new SocketIOServer(classHdlr, SSL_ENABLE));
				return pipeline;
			}
		});

		// Bind and start to accept incoming connections.
		bootstrap.bind(new InetSocketAddress(port));

		LOGGER.info("Server is started on port " + port + " with handler " + classHdlr.getSimpleName());
	}
}
