/**
 * The MIT License
 * Copyright (c) 2010 Tad Glines
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package eu.k2c.socket.io.server.frames;

import eu.k2c.socket.io.server.exceptions.SocketIOMalformedMessageException;

public class SocketIOFrame {
    // private static final Logger LOGGER =
    // Logger.getLogger(SocketIOFrame.class);

    /**
     * Socket.IO V7.x separator character
     */
    public static final char SEPARATOR_CHAR = ':';
    public static final char ENDPOINT_CHAR = '/';
    public static final char QUERY_SEPARATOR_CHAR = '?';
    public static final char ACK_SEPARATOR_CHAR = '+';
    public static final char ERROR_SEPARATOR_CHAR = '+';

    /**
     * Socket.IO parameters of JSON Event format
     */
    public final static String FIELD_NAME = "name";
    public static final String FIELD_ARGS = "args";

    public static final int EMPTY_FIELD = Integer.MAX_VALUE;

    private final FrameType frameType;
    private final long messageID;
    private final boolean socketIOHandleAck;
    private final String endPoint;
    private final String data1;
    private final String data2;

    /**
     * Create a simpler Socket.IO frame.
     * 
     * @param frameType
     * @param messageID
     * @param data1
     */
    public SocketIOFrame(final FrameType frameType, final long messageID, final String data) {
	this.frameType = frameType;
	this.messageID = messageID;
	this.socketIOHandleAck = false;
	this.endPoint = null;
	this.data1 = null;
	this.data2 = data;
    }

    /**
     * Create a Socket.IO frame
     * 
     * @param frameType
     * @param messageID
     * @param endPoint
     * @param data1
     */
    public SocketIOFrame(final FrameType frameType, final long messageID, final String endPoint, final String data) {
	this.frameType = frameType;
	this.messageID = messageID;
	this.socketIOHandleAck = false;
	this.endPoint = endPoint;
	this.data1 = null;
	this.data2 = data;
    }

    /**
     * Create a Socket.IO frame
     * 
     * @param frameType
     * @param messageID
     * @param endPoint
     * @param eventName
     * @param data1
     */
    public SocketIOFrame(final FrameType frameType, final long messageID, final String endPoint,
	    final String eventName, final String data) {
	this.frameType = frameType;
	this.messageID = messageID;
	this.socketIOHandleAck = false;
	this.endPoint = endPoint;
	this.data1 = eventName;
	this.data2 = data;
    }

    /**
     * Create a Socket.IO frame and define all parameters
     * 
     * @param frameType
     * @param messageID
     * @param socketIOHandleAck
     * @param endPoint
     * @param eventName
     * @param data1
     */
    public SocketIOFrame(final FrameType frameType, final long messageID, final boolean socketIOHandleAck,
	    final String endPoint, final String eventName, final String data) {
	this.frameType = frameType;
	this.messageID = messageID;
	this.socketIOHandleAck = socketIOHandleAck;
	this.endPoint = endPoint;
	this.data1 = eventName;
	this.data2 = data;
    }

    public SocketIOFrame(final FrameType frameType) throws SocketIOMalformedMessageException {
	if (frameType != FrameType.CONNECT && frameType != FrameType.DISCONNECT && frameType != FrameType.HEARTBEAT)
	    throw new SocketIOMalformedMessageException();

	this.frameType = frameType;
	this.messageID = EMPTY_FIELD;
	this.socketIOHandleAck = false;
	this.endPoint = null;
	this.data1 = null;
	this.data2 = null;
    }

    /**
     * @return {@link FrameType}
     */
    public FrameType getFrameType() {
	return frameType;
    }

    /**
     * @return the message unique identifier
     */
    public long getMessageID() {
	return messageID;
    }

    /**
     * @return if the Acknowledge should be treated as an event message packet.
     *         This occurs if a '+' sign follows the message id
     */
    public boolean isSocketIOHandleAck() {
	return socketIOHandleAck;
    }

    /**
     * @return socketIO endPoint of associated to the current frame
     */
    public String getEndPoint() {
	return endPoint;
    }

    /**
     * @return the message data1, null if no reason available Data can be text
     *         message ot a JSON message
     */
    public String getData() {
	return data2;
    }

    /**
     * @return the reason of an error message, null if no reason available
     */
    public String getReason() {
	return data1;
    }

    /**
     * @return the advice of an error message, null if no advice available
     */
    public String getAdvice() {
	return data2;
    }

    /**
     * @return the eventName, null if no name or event available
     */
    public String getEventName() {
	return data1;
    }

    /**
     * @return the ackID, null if no ack ID available
     */
    public String getAckID() {
	return data1;
    }

    /**
     * @return the error code, null if no error available
     */
    public String getError() {
	return data1;
    }

    /**
     * @return the acknowledge should be treated as event or not
     */
    public boolean getTreasAsEvent() {
	return false;
    }

    /**
     * Encode SocketIOFrame into a string using {@link SocketIOFrameEncoder}
     */
    public String encode() {
	return SocketIOFrameEncoder.encode(frameType, messageID, socketIOHandleAck, endPoint, data1, data2);
    }

    public static SocketIOFrame decode(final String message) {
	throw new UnsupportedOperationException();
    }

    /**
     * toString() is override to return the encoded message (this calls encode
     * method)
     */
    @Override
    public String toString() {
	return encode();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((data1 == null) ? 0 : data1.hashCode());
	result = prime * result + ((data2 == null) ? 0 : data2.hashCode());
	result = prime * result + ((endPoint == null) ? 0 : endPoint.hashCode());
	result = prime * result + ((frameType == null) ? 0 : frameType.hashCode());
	result = prime * result + (int) (messageID ^ (messageID >>> 32));
	result = prime * result + (socketIOHandleAck ? 1231 : 1237);
	return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	SocketIOFrame other = (SocketIOFrame) obj;
	if (data1 == null) {
	    if (other.data1 != null)
		return false;
	} else if (!data1.equals(other.data1))
	    return false;
	if (data2 == null) {
	    if (other.data2 != null)
		return false;
	} else if (!data2.equals(other.data2))
	    return false;
	if (endPoint == null) {
	    if (other.endPoint != null)
		return false;
	} else if (!endPoint.equals(other.endPoint))
	    return false;
	if (frameType != other.frameType)
	    return false;
	if ((messageID != other.messageID)
		&& (((messageID < 0) && (other.messageID != EMPTY_FIELD)) || ((other.messageID < 0) && (messageID != EMPTY_FIELD))))
	    return false;
	if (socketIOHandleAck != other.socketIOHandleAck)
	    return false;
	return true;
    }
}
