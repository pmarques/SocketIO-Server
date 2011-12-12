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
package eu.k2c.socket.io.frames.helpers;

import eu.k2c.socket.io.frames.FrameType;
import eu.k2c.socket.io.frames.SocketIOFrame;
import eu.k2c.socket.io.frames.SocketIOFrameGenerator;

/**
 * This collection of (static) methods has the main intent simplify the code and
 * make it more easily to maintain or not....
 * 
 * @author "Patrick F. Marques <patrick.marques@k2c.eu>"
 */
public class SocketIOFrameHelper {
	/*
	 * Simplifications
	 */
	/**
	 * Disconnect the whole socket, simple disconnection frame
	 * 
	 * @return frame string
	 */
	public static String encodeDisconnect() {
		return "" + FrameType.DISCONNECT.value();
	}

	/**
	 * Disconnect a from a specific endPoint.
	 * 
	 * @param endPoint
	 * 
	 * @return frame string
	 */
	public static String encodeDisconnect(final String endPoint) {
		// Simplified version
		if (endPoint == null)
			return "" + FrameType.DISCONNECT.value();

		return SocketIOFrameGenerator.encode(FrameType.DISCONNECT, SocketIOFrame.EMPTY_FIELD, false, endPoint, null,
				null);

	}

	/**
	 * Encode simple Connection frame
	 * 
	 * @return frame string
	 */
	public static String encodeConnect() {
		return SocketIOFrameGenerator.encode(FrameType.CONNECT, SocketIOFrame.EMPTY_FIELD, false, null, null, null);
	}

	/**
	 * Encode Connection frame with parameters
	 * 
	 * @param endPoint
	 *            Connection endPoint name or null if doesn't exist
	 * @param query
	 *            Connection parameter or null if doesn't exist
	 * 
	 * @return frame string
	 */
	public static String encodeConnect(final String endPoint, final String query) {
		return SocketIOFrameGenerator
				.encode(FrameType.CONNECT, SocketIOFrame.EMPTY_FIELD, false, endPoint, null, query);
	}

	/**
	 * Create a HeartBeat frame
	 * 
	 * @return frame string
	 */
	public static String encodeHeartBit() {
		return SocketIOFrameGenerator.encode(FrameType.HEARTBEAT, SocketIOFrame.EMPTY_FIELD, false, null, null, null);
	}

	/**
	 * Create a HeartBeat frame
	 * 
	 * @param msgid
	 *            heart beat id
	 * 
	 * @return frame string
	 */
	public static String encodeHeartBit(final long msgid) {
		return SocketIOFrameGenerator.encode(FrameType.HEARTBEAT, msgid, false, null, null, null);
	}

	/**
	 * Create a SocketIO Acknowledge frame
	 * 
	 * @param msgID
	 *            message id to which corresponds the acknowledge
	 * @param endPoint
	 *            Connection endPoint name or null if doesn't exist
	 * @param data
	 *            Acknowledge message or null if doesn't exist
	 * 
	 * @return frame string
	 */
	public static String encodeAck(final long msgID, final long ackID, final String data) {
		final String _ackID = ((ackID == SocketIOFrame.EMPTY_FIELD) || (ackID < 0)) ? null : String.valueOf(ackID);
		return SocketIOFrameGenerator.encode(FrameType.ACK, msgID, false, null, _ackID, data);
	}

	/**
	 * Create a SocketIO Error frame
	 * 
	 * @param reason
	 *            Error reason
	 * @param advice
	 *            Advice to pass or resolve the error
	 * 
	 * @return frame string
	 */
	public static String encodeError(final String reason, final String advice) {
		return SocketIOFrameGenerator.encode(FrameType.ERROR, SocketIOFrame.EMPTY_FIELD, false, null, reason, advice);
	}

	/**
	 * Create a SocketIO Error frame
	 * 
	 * @param endPoint
	 *            message id to which corresponds the acknowledge
	 * @param reason
	 *            Error reason
	 * @param advice
	 *            Advice to pass or resolve the error
	 * 
	 * @return frame string
	 */
	public static String encodeError(final String endPoint, final String reason, final String advice) {
		return SocketIOFrameGenerator.encode(FrameType.ERROR, SocketIOFrame.EMPTY_FIELD, false, endPoint, reason,
				advice);
	}

	/**
	 * Create a SocketIO Message frame
	 * 
	 * @param messageID
	 *            Message ID or null if doesn't exist
	 * @param endPoint
	 *            Connection endPoint name or null if doesn't exist
	 * @param data
	 *            Message data
	 * 
	 * @return frame string
	 */
	public static String encodeMessage(final long messageID, final String endPoint, final String data) {
		return SocketIOFrameGenerator.encode(FrameType.MESSAGE, messageID, false, endPoint, data, null);
	}

	/**
	 * Create a SocketIO Message frame with JSON data
	 * 
	 * @param messageID
	 *            Message ID or null if doesn't exist
	 * @param endPoint
	 *            Connection endPoint name or null if doesn't exist
	 * @param data
	 *            JSON encoded Message
	 * 
	 * @return frame string
	 */
	public static String encodeJSONMessage(final long messageID, final String endPoint, final String data) {
		return SocketIOFrameGenerator.encode(FrameType.JSON, messageID, false, endPoint, data, null);
	}

	/**
	 * Create a SocketIO Event frame. Data argument is correspondent to the args
	 * in the Socket.IO event specification. Hence, here is constructed the
	 * correspondent socket.io event with event name and <code>data</code> on
	 * args
	 * 
	 * @param messageID
	 *            Message ID or null if doesn't exist
	 * @param endPoint
	 *            Connection endPoint name or null if doesn't exist
	 * @param eventName
	 *            Name of the generated event
	 * @param data
	 *            JSON encoded data (args content of JSON formated event)
	 * 
	 * @return frame string
	 */
	public static String encodeEVENT(final long messageID, final String endPoint, final String eventName,
			final String data) {
		return SocketIOFrameGenerator.encode(FrameType.EVENT, messageID, false, endPoint, eventName, data);
	}
}
