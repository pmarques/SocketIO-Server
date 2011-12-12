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
package eu.k2c.socket.io.frames;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;

/**
 * This collection of (static) methods has the main intent simplify the code and
 * make it more easily to maintain or not....
 * 
 * @author "Patrick F. Marques <patrick.marques@k2c.eu>"
 */
public class SocketIOFrameGenerator {
	private static JsonFactory JSONF = new JsonFactory();

	/***
	 * Encode SocketIO data parts
	 * 
	 * @param str
	 * @param part1
	 * @param part2
	 * @param separator
	 */
	private static void encodeParts(final StringBuilder str, final String part1, final String part2,
			final char separator) {
		if (part1 != null) {
			str.append(part1);
		}
		if (part2 != null) {
			str.append(separator);
			str.append(part2);
		}
	}

	/**
	 * Encode a SocketIO part of Event frame. Data argument is correspondent to
	 * the args in the Socket.IO event specification. Hence, here is constructed
	 * the correspondent socket.io event with event name and <code>data</code>
	 * on args
	 * 
	 * @param str
	 * @param eventName
	 *            Name of the generated event
	 * @param data
	 *            JSON encoded data (args content of JSON formated event)
	 */
	private static void encodeEvent(final StringBuilder str, final String eventName, final String data) {
		final Writer result = new StringWriter();

		/* Create the Socket.IO Event in JSON format */
		try {
			final JsonGenerator g = JSONF.createJsonGenerator(result);
			g.writeStartObject();
			g.writeStringField(SocketIOFrame.FIELD_NAME, eventName);
			g.writeArrayFieldStart(SocketIOFrame.FIELD_ARGS);
			g.writeRaw(data);
			g.writeEndArray();
			g.writeEndObject();
			g.flush();
		} catch (IOException e) {
			e.printStackTrace();
			throw new UnsupportedOperationException();
		} catch (Exception e) {
			e.printStackTrace();
			throw new UnsupportedOperationException();
		}

		final String fullData = result.toString();
		str.append(fullData);
	}

	/**
	 * Encode Socket.IO generic part of frame
	 * 
	 * @param str
	 * @param type
	 * @param msgid
	 * @param handledByUser
	 * @param endPoint
	 */
	private static void encode(final StringBuilder str, final FrameType type, final String msgid,
			final boolean handledByUser, final String endPoint) {

		// Message type
		str.append(type.value());
		str.append(SocketIOFrame.SEPARATOR_CHAR);

		// Message ID
		if (msgid != null)
			str.append(msgid);

		// If the ACK is not handled by socket.io, but by the user instead.
		if (handledByUser)
			str.append(SocketIOFrame.ACK_SEPARATOR_CHAR);

		str.append(SocketIOFrame.SEPARATOR_CHAR);

		// End point
		if (endPoint != null) {
			str.append(endPoint);
		}
	}

	/**
	 * Encode Socket.IO messages
	 * 
	 * @param type
	 *            Socket.IO Message type
	 * @param msgID
	 *            Message ID or null if doesn't exist
	 * @param endPoint
	 *            Connection endPoint name or null if doesn't exist
	 * @param data1
	 *            Message data, if type is ACK, ERROR or EVENT this corresponds
	 *            respectively to reason, eventName and ackID
	 * @param data2
	 *            Message data in the cases above this corresponds respectively
	 *            to Advice, event arguments (JSON formated) and Ack arguments
	 * @return
	 */
	public static String encode(final FrameType type, final long msgID, final boolean handledByUser,
			final String endPoint, final String data1, final String data2) {
		final String messageID = ((msgID == SocketIOFrame.EMPTY_FIELD) || (msgID < 0)) ? "" : String.valueOf(msgID);

		/* Calculate initial size */
		int size = 3; // to simplify allocate e always 3 separators chars
		size += messageID != null ? messageID.length() : 0;
		size += handledByUser ? 1 : 0;
		size += endPoint != null ? endPoint.length() : 0;

		// If a reason exists add more one separator char
		size += data1 != null ? data1.length() + 1 : 0;

		final StringBuilder str = new StringBuilder(size);

		// Encode part present on every message
		encode(str, type, messageID, handledByUser, endPoint);

		if ((data1 != null) /* || (data2 != null) */)
			str.append(SocketIOFrame.SEPARATOR_CHAR);

		switch (type) {
			case EVENT:
				encodeEvent(str, data1, data2);
				break;
			case ACK:
				// FIXME: The ack message is a bit differente...
				return FrameType.ACK.value() + ":::" + msgID + (data2 != null ? '+' : "")
						+ (data2 != null ? data2 : "");
			case ERROR:
				encodeParts(str, data1, data2, SocketIOFrame.ERROR_SEPARATOR_CHAR);
				break;
			case CONNECT:
				encodeParts(str, data1, data2, SocketIOFrame.QUERY_SEPARATOR_CHAR);
				break;
			default:
				str.append(':');
				if (data2 != null)
					str.append(data2);
				break;
		}

		return str.toString();
	}
}
