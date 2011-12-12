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

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import eu.k2c.socket.io.frames.helpers.SocketIOFrameHelper;

/**
 * 
 * @author "Patrick F. Marques <patrick.marques@k2c.eu>"
 * 
 * @see <a href="https://github.com/LearnBoost/socket.io-spec">Socket.IO
 *      Spec</a>
 */
public class SocketIOFrameHelperTest {

	private final static String UUID = "4040EAF2-4B09-401F-BE51-C659E2EF18C3";
	private final static String CODE = "200";
	private final static String PHRASE = "this is the code prhase";
	private final static long MSGID = 100;
	private final static String eventName = "topic";
	private final static String ENDPOINT = "/endPoint";
	private final static String QUERY = "query=true";
	private final static String JSONData = "{\"uuid\":\"" + UUID + "\",\"code\":\"" + CODE + "\",\"phrase\":\""
			+ PHRASE + "\"}";
	private final static String MsgData = "Some message data";
	private final static String REASON = "some reason for the error";
	private final static String ADVICE = "some advice to give around to the errorr";

	@Test
	public void testEncodeDisconnect() {
		String expected = "0";
		String result = SocketIOFrameHelper.encodeDisconnect();
		assertTrue(result.equals(expected));

		result = SocketIOFrameHelper.encodeDisconnect(null);
		assertTrue(result.equals(expected));
	}

	@Test
	public void testEncodeDisconnect2() {
		String expected = "0::" + ENDPOINT;
		String result = SocketIOFrameHelper.encodeDisconnect(ENDPOINT);
		assertTrue(result.equals(expected));
	}

	@Test
	public void testEncodeConnect() {
		String expected = "1::";
		String result = SocketIOFrameHelper.encodeConnect();
		assertTrue(result.equals(expected));
	}

	@Test
	public void testEncodeConnect2() {
		String expected = "1::" + ENDPOINT;
		String result = SocketIOFrameHelper.encodeConnect(ENDPOINT, null);
		assertTrue(result.equals(expected));
	}

	@Test
	public void testEncodeConnect3() {
		String expected = "1::" + ENDPOINT + "?" + QUERY;
		String result = SocketIOFrameHelper.encodeConnect(ENDPOINT, QUERY);
		assertTrue(result.equals(expected));
	}

	@Test
	public void testEncodeHeartBit() {
		String expected = "2::";
		String result = SocketIOFrameHelper.encodeHeartBit();
		assertTrue(result.equals(expected));
	}

	@Test
	public void testEncodeAck() {
		String expected = "6:::" + MSGID + "+" + MsgData;
		String result = SocketIOFrameHelper.encodeAck(-1, MSGID, MsgData);
		assertTrue(result.equals(expected));
	}

	@Test
	public void testEncodeError() {
		String expected = "7::" + ENDPOINT + ":" + REASON + "+" + ADVICE;
		String result = SocketIOFrameHelper.encodeError(ENDPOINT, REASON, ADVICE);
		assertTrue(result.equals(expected));
	}

	@Test
	public void testEncodeMessage() {
		String expected = "3:" + MSGID + ":" + ENDPOINT + ":" + MsgData;
		String result = SocketIOFrameHelper.encodeMessage(MSGID, ENDPOINT, MsgData);
		assertTrue(result.equals(expected));
	}

	@Test
	public void testEncodeJSONMessage() {
		String expected = "4:" + MSGID + ":" + ENDPOINT + ":" + JSONData;
		String result = SocketIOFrameHelper.encodeJSONMessage(MSGID, ENDPOINT, JSONData);
		assertTrue(result.equals(expected));
	}

	@Test
	public void testEncodeEVENT() {
		String expected = "5:" + MSGID + ":" + ENDPOINT + ":{\"name\":\"" + eventName + "\",\"args\":[" + JSONData
				+ "]}";
		String result = SocketIOFrameHelper.encodeEVENT(MSGID, ENDPOINT, eventName, JSONData);
		assertTrue(result.equals(expected));
	}
}
