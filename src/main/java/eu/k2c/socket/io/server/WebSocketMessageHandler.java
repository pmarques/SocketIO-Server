package eu.k2c.socket.io.server;

import org.jboss.netty.buffer.ChannelBuffer;

public interface WebSocketMessageHandler {
    void onTextMessage(String text);

    void onBinaryMessage(ChannelBuffer binaryData);
}
