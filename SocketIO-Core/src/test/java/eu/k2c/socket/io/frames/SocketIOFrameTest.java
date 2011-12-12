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

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import eu.k2c.socket.io.frames.FrameType;
import eu.k2c.socket.io.frames.SocketIOFrame;
import eu.k2c.socket.io.frames.SocketIOFrameParser;
import eu.k2c.socket.io.server.exceptions.SocketIOMalformedMessageException;
import eu.k2c.socket.io.server.exceptions.SocketIOUnknownMessageException;

/**
 * @author "Patrick F. Marques <patrick.marques@k2c.eu>"
 * 
 * @see <a href="https://github.com/LearnBoost/socket.io-spec">Socket.IO
 *      Spec</a>
 */
public class SocketIOFrameTest {

	private final static String UUID = "4040EAF2-4B09-401F-BE51-C659E2EF18C3";
	private final static String CODE = "200";
	private final static String PHRASE = "this is the code prhase";
	private final static long MSGID = 100;
	private final static String ACK_ID = "9834";
	private final static String EVENT_NAME = "topic";
	private final static String ENDPOINT = "/endPoint";
	private final static String QUERY = "query=true";
	private final static String JSON_DATA = "{\"some\":\"object\"}";
	private final static String MsgData = "Some message data";
	private final static String REASON = "some reason for the error";
	private final static String ADVICE = "some advice to give around to the errorr";

	/*
	 * Test simplest messages..
	 */
	@Test
	public void testEncode1() throws SocketIOMalformedMessageException, SocketIOUnknownMessageException {
		SocketIOFrame frame = new SocketIOFrame(FrameType.DISCONNECT);
		String encoded = frame.encode();
		SocketIOFrame result = SocketIOFrameParser.decode(encoded);
		assertTrue("Similar frames are expected!", frame.equals(result));

		frame = new SocketIOFrame(FrameType.CONNECT);
		encoded = frame.encode();
		result = SocketIOFrameParser.decode(encoded);
		assertTrue("Similar frames are expected!", frame.equals(result));

		frame = new SocketIOFrame(FrameType.HEARTBEAT);
		encoded = frame.encode();
		result = SocketIOFrameParser.decode(encoded);
		assertTrue("Similar frames are expected!", frame.equals(result));
	}

	/*
	 * Test if message attributes are correct when encode and decode messages
	 */
	@Test
	public void testEncode2Datas() throws SocketIOMalformedMessageException, SocketIOUnknownMessageException {
		SocketIOFrame frame = new SocketIOFrame(FrameType.ERROR, -1, null, CODE, ADVICE);
		String encoded = frame.encode();
		SocketIOFrame result = SocketIOFrameParser.decode(encoded);
		assertTrue("Similar frames are expected!", frame.equals(result));
		assertTrue("Similar frames are expected!", CODE.equals(result.getError()));
		assertTrue("Similar frames are expected!", ADVICE.equals(result.getAdvice()));

		frame = new SocketIOFrame(FrameType.EVENT, -1, null, EVENT_NAME, JSON_DATA);
		encoded = frame.encode();
		result = SocketIOFrameParser.decode(encoded);
		assertTrue("Similar frames are expected!", frame.equals(result));
		assertTrue("Similar frames are expected!", EVENT_NAME.equals(result.getEventName()));
		assertTrue("Similar frames are expected!", JSON_DATA.equals(result.getData()));

		frame = new SocketIOFrame(FrameType.ACK, -1, null, ACK_ID, ADVICE);
		encoded = frame.encode();
		result = SocketIOFrameParser.decode(encoded);
		assertTrue("Similar frames are expected!", frame.equals(result));
		assertTrue("Similar frames are expected!", ACK_ID.equals(result.getError()));
		assertTrue("Similar frames are expected!", ADVICE.equals(result.getAdvice()));
	}
}
