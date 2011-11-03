package eu.k2c.socket.io.example.echo;

import static org.jboss.netty.channel.Channels.pipeline;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import javax.net.ssl.SSLEngine;

import org.apache.log4j.Logger;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.example.securechat.SecureChatSslContextFactory;
import org.jboss.netty.handler.codec.http.HttpChunkAggregator;
import org.jboss.netty.handler.codec.http.HttpRequestDecoder;
import org.jboss.netty.handler.codec.http.HttpResponseEncoder;
import org.jboss.netty.handler.ssl.SslHandler;

import eu.k2c.socket.io.server.SocketIOServer;

public class EchoServerNettyTest {
    private static final Logger LOGGER = Logger.getLogger(EchoServerNettyTest.class);

    private static final int SERVER_PORT = 8080;

    public static boolean ssl = true;

    public static void main(String... args) throws Exception {
	new EchoServerNettyTest().start();

    }

    public void start() {
	// Configure the server.
	ServerBootstrap bootstrap = new ServerBootstrap(new NioServerSocketChannelFactory(
		Executors.newCachedThreadPool(), Executors.newCachedThreadPool()));

	// Set up the event pipeline factory.
	bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
	    public ChannelPipeline getPipeline() throws Exception {
		LOGGER.trace("getPipeline");

		ChannelPipeline pipeline = pipeline();

		if (ssl) {
		    SSLEngine engine = SecureChatSslContextFactory.getServerContext().createSSLEngine();
		    engine.setUseClientMode(false);

		    pipeline.addLast("ssl", new SslHandler(engine));
		}
		pipeline.addLast("decoder", new HttpRequestDecoder());
		pipeline.addLast("aggregator", new HttpChunkAggregator(65536));
		pipeline.addLast("encoder", new HttpResponseEncoder());
		pipeline.addLast("handler", new SocketIOServer(EchoHandler.class));
		return pipeline;
	    }
	});

	// Bind and start to accept incoming connections.
	bootstrap.bind(new InetSocketAddress(SERVER_PORT));

	LOGGER.info("Server is started on port " + SERVER_PORT);
    }
}
