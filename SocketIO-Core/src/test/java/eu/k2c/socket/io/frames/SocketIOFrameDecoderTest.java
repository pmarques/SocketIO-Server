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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Ignore;
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
public class SocketIOFrameDecoderTest {

	/* Decoding Heartbeat packets */
	@Test
	public void testDisconnectionPacket() throws Exception {
		final SocketIOFrame frame = SocketIOFrameParser.decode("0::/woot");
		assertEquals(frame.getFrameType(), FrameType.DISCONNECT);
		assert("/woot".equals(frame.getEndPoint()));
	}

	/* Decoding Connection packets */
	@Test
	public void testConnectionPacket() throws Exception {
		final SocketIOFrame frame = SocketIOFrameParser.decode("1::/tobi");
		assertEquals(frame.getFrameType(), FrameType.CONNECT);
		assert("/tobi".equals(frame.getEndPoint()));
	}

	@Test
	public void testConnectionPacketWithQueryString() throws Exception {
		final SocketIOFrame frame = SocketIOFrameParser.decode("1::/test:?test=1");
		assertEquals(frame.getFrameType(), FrameType.CONNECT);
		assertEquals(frame.getEndPoint(), "/test");
		assert("?test=1".equals(frame.getQuery()));
	}

	/* Decoding Heartbeat packets */
	@Test
	public void testParseHB() throws Exception {
		final SocketIOFrame frame = SocketIOFrameParser.decode("2::");
		assertEquals(frame.getFrameType(), FrameType.HEARTBEAT);
		assertEquals(frame.getEndPoint(), null);
	}

	/* Decoding Message packets */
	@Test
	public void testMessagePacket() throws Exception {
		final SocketIOFrame frame = SocketIOFrameParser.decode("3:::woot");
		assertEquals(frame.getFrameType(), FrameType.MESSAGE);
		assertEquals(frame.getEndPoint(), null);
		assertEquals(frame.getMessageID(), -1);
		assertEquals(frame.getEventData(), "woot");
	}

	@Test
	public void testMessagePacketWithIDAndEndPoint() throws Exception {
		final SocketIOFrame frame = SocketIOFrameParser.decode("3:5:/tobi");
		assertEquals(frame.getFrameType(), FrameType.MESSAGE);
		assert("/tobi".equals(frame.getEndPoint()));
		assertEquals(frame.getMessageID(), 5);
		assertEquals(frame.getData(), null);
	}

	@Test
	public void testMessagePacketWithACKIDAndEndPoint() throws Exception {
		final SocketIOFrame frame = SocketIOFrameParser.decode("3:5+:/tobi");
		assertEquals(frame.getFrameType(), FrameType.MESSAGE);
		assert("/tobi".equals(frame.getEndPoint()));
		assertEquals(frame.getMessageID(), 5);
		assertEquals(frame.getData(), null);
	}

	/* Decoding Event packets */
	@Test
	public void testEventPacket() throws Exception {
		final SocketIOFrame frame = SocketIOFrameParser.decode("5:::{\"name\":\"woot\"}");
		assertEquals(frame.getFrameType(), FrameType.EVENT);
		assertEquals(frame.getEndPoint(), null);
		assertEquals(frame.getEventName(), "woot");
		assertEquals(frame.getEventData(), null);
	}

	@Test
	public void testEventPacketWithMsgIDAndAck() throws Exception {
		final SocketIOFrame frame = SocketIOFrameParser.decode("5:1+::{\"name\":\"tobi\"}");
		assertEquals(frame.getFrameType(), FrameType.EVENT);
		assertEquals(frame.getMessageID(), 1);
		assertEquals(frame.getAckID(), "data");
		assertEquals(frame.getEndPoint(), null);
		assertEquals(frame.getEventName(), "tobi");
		assertEquals(frame.getEventData(), null);
		// parser.decodePacket('5:1+::{"name":"tobi"}').should.eql({
		// type: 'event'
		// , id: 1
		// , ack: 'data'
		// , endpoint: ''
		// , name: 'tobi'
		// , args: []
		// });
	}

	@Test
	public void testEventPacketWithData() throws Exception {
		final SocketIOFrame frame = SocketIOFrameParser
				.decode("5:::{\"name\":\"edwald\",\"args\":[{\"a\": \"b\"},2,\"3\"]}");
		assertEquals(frame.getFrameType(), FrameType.EVENT);
		assertEquals(frame.getMessageID(), -1);
		assertEquals(frame.getEndPoint(), null);
		assertEquals(frame.getEventName(), "edwald");
		assertEquals(frame.getEventData(), "{\"a\": \"b\"},2,\"3\"");
	}

	@Test
	public void testParseMalformedMessage() {
		try {
			SocketIOFrameParser.decode("5");
			fail("Malformed message expected!");
		} catch (SocketIOMalformedMessageException e) {
			assertTrue(true);
		} catch (SocketIOUnknownMessageException e) {
			e.printStackTrace();
			fail("SocketIO malformed message exception, Malformed message expected!");
		}
	}

	/* Decoding Acknowledge packets */
	@Test
	public void testAckPacket() throws Exception {
		SocketIOFrame frame = SocketIOFrameParser.decode("6:::140");
		assertEquals(frame.getFrameType(), FrameType.ACK);
		assertEquals(frame.getAckID(), 140);
		assertEquals(frame.getEndPoint(), null);
		assertEquals(frame.getData(), null);
	}

	@Test
	public void testAckPacketArgs() throws Exception {
		SocketIOFrame frame = SocketIOFrameParser.decode("6:::12+[\"woot\",\"wa\"]");
		assertEquals(frame.getFrameType(), FrameType.ACK);
		assertEquals(frame.getAckID(), 12);
		assertEquals(frame.getEndPoint(), null);
		assertEquals(frame.getData(), "[\"woot\",\"wa\"]");
	}

	@Ignore("We do not parse JSON args part")
	@Test
	public void testAckPacketWithBadJSON() throws Exception {
		SocketIOFrame frame = SocketIOFrameParser.decode("6:::1+{\"++]");
		assertEquals(frame.getFrameType(), FrameType.ACK);
		assertEquals(frame.getAckID(), "1");
		assertEquals(frame.getEndPoint(), null);
		assertEquals(frame.getData(), null);
		// parser.decodePacket('6:::1+{"++]').should.eql({
		// type: 'ack'
		// , ackId: '1'
		// , endpoint: ''
		// , args: []
		// });
	}

	/* Decoding Error packets */
	@Test
	public void testErroPacketDecoder() throws Exception {
		SocketIOFrame frame = SocketIOFrameParser.decode("7:::");
		assertEquals(frame.getFrameType(), FrameType.ERROR);
		assertEquals(frame.getReason(), null);
		assertEquals(frame.getAdvice(), null);
		assertEquals(frame.getEndPoint(), null);
	}

	@Test
	public void testErroPacketDecoderWithReason() throws Exception {
		SocketIOFrame frame = SocketIOFrameParser.decode("7:::0");
		assertEquals(frame.getFrameType(), FrameType.ERROR);
		assertEquals(frame.getReason(), "transport not supported");
		assertEquals(frame.getAdvice(), null);
		assertEquals(frame.getEndPoint(), null);
	}

	@Test
	public void testErroPacketDecoderWithReasonAndAdvice() throws Exception {
		SocketIOFrame frame = SocketIOFrameParser.decode("7:::2+0");
		assertEquals(frame.getFrameType(), FrameType.ERROR);
		assertEquals(frame.getReason(), "unauthorized");
		assertEquals(frame.getAdvice(), "reconnect");
		assertEquals(frame.getEndPoint(), "");
	}

	@Test
	public void testErroPacketDecoderWithEndpoint() throws Exception {
		SocketIOFrame frame = SocketIOFrameParser.decode("7::/woot");
		assertEquals(frame.getFrameType(), FrameType.ERROR);
		assertEquals(frame.getReason(), null);
		assertEquals(frame.getAdvice(), null);
		assert("/woot".equals(frame.getEndPoint()));
	}

	/* Decoding Unknown packets */
	@Test
	public void testParseUnknowMessage() {
		try {
			SocketIOFrameParser.decode("10:::");
			fail("Unknown message expected!");

		} catch (SocketIOMalformedMessageException e) {
			e.printStackTrace();
			fail("SocketIO malformed message exception, Unknown message expected!");
		} catch (SocketIOUnknownMessageException e) {
			assertTrue(true);
		}
	}
}
