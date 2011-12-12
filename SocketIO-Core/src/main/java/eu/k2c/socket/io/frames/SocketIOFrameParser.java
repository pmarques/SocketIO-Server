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
import org.apache.log4j.Logger;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonToken;

import eu.k2c.socket.io.server.exceptions.SocketIOMalformedMessageException;
import eu.k2c.socket.io.server.exceptions.SocketIOUnknownMessageException;

/**
 * @author "Patrick F. Marques <patrick.marques@k2c.eu>"
 */
public class SocketIOFrameParser {
	private static final Logger LOGGER = Logger.getLogger(SocketIOFrameParser.class);
	private static final JsonFactory JSONF = new JsonFactory();

	public static SocketIOFrame decode(final String message) throws SocketIOMalformedMessageException,
			SocketIOUnknownMessageException {

		/* Parse Frame Type */
		int start = 0;
		int end = message.indexOf(SocketIOFrame.SEPARATOR_CHAR, start);

		if (-1 == end || start == end) {
			LOGGER.error("MalformedMessage");
			throw new SocketIOMalformedMessageException();
		}

		int ftype = Integer.parseInt(message.substring(start, end), 16);

		FrameType frameType = FrameType.fromInt(ftype);
		if (frameType == FrameType.UNKNOWN) {
			LOGGER.error("Unknown Message Type");
			throw new SocketIOUnknownMessageException();
		}

		/* Parse Message ID */
		start = end + 1;
		end = message.indexOf(SocketIOFrame.SEPARATOR_CHAR, start);
		/*
		 * if no more separator characters is available this is a malformed
		 * message
		 */
		if (-1 == end) {
			LOGGER.error("Malformed Message");
			return new SocketIOFrame(frameType);
		}
		int mid = SocketIOFrame.EMPTY_FIELD;
		boolean ack = false;
		/* Message ID isn't required! */
		if (start != end) {
			int endl = end;
			if ('+' == message.charAt(end - 1)) {
				endl = end - 1;
				ack = true;
			}
			final String MsgID = message.substring(start, endl);
			mid = Integer.parseInt(MsgID, 16);

		}

		/* Parse message end point */
		start = end + 1;
		end = message.indexOf(SocketIOFrame.SEPARATOR_CHAR, start);
		String endPoint = null;
		if (end == -1)
			end = message.length();
		if (end > start)
			endPoint = message.substring(start, end);

		String mdata = null;
		/* Get message data if exists */
		start = end + 1;
		end = message.length();
		if (start < end) {
			mdata = message.substring(start, end);
		}

		String data1 = null;
		String data2 = null;
		if (mdata != null) {

			switch (frameType) {
				case EVENT: {
					final String[] p = parseEvent(mdata);
					data1 = p[0];
					data2 = p[1];
					break;
				}
				case ERROR: {
					final String[] p = parseParts(mdata, SocketIOFrame.ERROR_SEPARATOR_CHAR);
					data1 = p[0];
					data2 = p[1];
					break;
				}
				case ACK: {
					final String[] p = parseParts(mdata, SocketIOFrame.ACK_SEPARATOR_CHAR);
					mid = Integer.valueOf(p[0]);
					data2 = p[1];
					break;
				}
				default:
					// prevent errors...
					data1 = null;
					data2 = mdata;
					break;
			}
		}

		return new SocketIOFrame(frameType, mid, ack, endPoint, data1, data2);
	}

	private static String[] parseParts(final String mdata, char separator) {
		final int start = 0;
		final int end = mdata.indexOf(separator, start);
		if (end == -1)
			return new String[] { mdata, null };

		final String data1 = mdata.substring(start, end);
		final String data2 = mdata.substring(end + 1, mdata.length());

		return new String[] { data1, data2 };
	}

	private static String[] parseEvent(final String mdata) {
		try {

			// use jackson library to parse JSON messages
			JsonParser jp = JSONF.createJsonParser(mdata);

			// if the message do not start properly give an exception!
			if (jp.nextToken() != JsonToken.START_OBJECT)
				throw new NullPointerException();

			String eventName = null;
			String eventData = null;

			JsonToken token = jp.nextToken();
			while (token != JsonToken.END_OBJECT) {
				// trying optimization, only process field strings tokens
				if ((token != JsonToken.VALUE_STRING) && (token != JsonToken.START_ARRAY)) {
					token = jp.nextToken();
					continue;
				}

				// retrieve field data
				String fieldname = jp.getCurrentName();
				if (SocketIOFrame.FIELD_NAME.equals(fieldname)) {
					eventName = jp.getText();
				} else if (SocketIOFrame.FIELD_ARGS.equals(fieldname)) {
					int startA = jp.getCurrentLocation().getColumnNr() - 1;
					jp.skipChildren();
					int endA = jp.getTokenLocation().getColumnNr() - 1;
					eventData = mdata.substring(startA, endA);
				}

				token = jp.nextToken();
			}
			jp.close();
			return new String[] { eventName, eventData };
		} catch (JsonParseException e) {
			LOGGER.debug("Parser exception :: " + mdata);
			e.printStackTrace();
			throw new NullPointerException();
		} catch (IOException e) {
			LOGGER.debug("IO exception :: " + mdata);
			e.printStackTrace();
			throw new NullPointerException();
		}
	}
}
