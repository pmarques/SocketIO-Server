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

import junit.framework.TestCase;

import org.junit.Test;

import eu.k2c.socket.io.frames.FrameType;
import eu.k2c.socket.io.frames.SocketIOFrame;
import eu.k2c.socket.io.frames.SocketIOFrameParser;

/**
 * 
 * @author "Patrick F. Marques <patrick.marques@k2c.eu>"
 * 
 * @see <a href="https://github.com/LearnBoost/socket.io-spec">Socket.IO
 *      Spec</a>
 */
public class SocketIOFrameEncoderTest extends TestCase {
	/* Encoding Error packets */
//	@Test
//	public void testErrorPacket() throws Exception {
//		final SocketIOFrame frame = SocketIOFrameParser.decode("0::/woot");
//		assertEquals(frame.getFrameType(), FrameType.DISCONNECT);
//		assertEquals(frame.getEndPoint(), "/woot");
//	}
//	  'encoding error packet': function () {
//    parser.encodePacket({
//        type: 'error'
//      , reason: ''
//      , advice: ''
//      , endpoint: ''
//    }).should.eql('7::');
//  },
//
//  'encoding error packet with reason': function () {
//    parser.encodePacket({
//        type: 'error'
//      , reason: 'transport not supported'
//      , advice: ''
//      , endpoint: ''
//    }).should.eql('7:::0');
//  },
//
//  'encoding error packet with reason and advice': function () {
//    parser.encodePacket({
//        type: 'error'
//      , reason: 'unauthorized'
//      , advice: 'reconnect'
//      , endpoint: ''
//    }).should.eql('7:::2+0');
//  },
//
//  'encoding error packet with endpoint': function () {
//    parser.encodePacket({
//        type: 'error'
//      , reason: ''
//      , advice: ''
//      , endpoint: '/woot'
//    }).should.eql('7::/woot');
//  },

	@Test
	public void testAckPacket() throws Exception {
		final SocketIOFrame frame = new SocketIOFrame(FrameType.ACK, 140);
		final String msg = frame.encode();
		assertEquals("6:::140", msg);
	}

	@Test
	public void testAckPacketWithArgs() throws Exception {
		final SocketIOFrame frame = new SocketIOFrame(FrameType.ACK, 12, "[\"woot\",\"wa\"]");
		final String msg = frame.encode();
		assertEquals("6:::12+[\"woot\",\"wa\"]", msg);
	}

//  'encoding json packet': function () {
//    parser.encodePacket({
//        type: 'json'
//      , endpoint: ''
//      , data: '2'
//    }).should.eql('4:::"2"');
//  },
//
//  'encoding json packet with message id and ack data': function () {
//    parser.encodePacket({
//        type: 'json'
//      , id: 1
//      , ack: 'data'
//      , endpoint: ''
//      , data: { a: 'b' }
//    }).should.eql('4:1+::{"a":"b"}');
//  },
//
//  'encoding an event packet': function () {
//    parser.encodePacket({
//        type: 'event'
//      , name: 'woot'
//      , endpoint: ''
//      , args: []
//    }).should.eql('5:::{"name":"woot"}');
//  },
//
//  'encoding an event packet with message id and ack': function () {
//    parser.encodePacket({
//        type: 'event'
//      , id: 1
//      , ack: 'data'
//      , endpoint: ''
//      , name: 'tobi'
//      , args: []
//    }).should.eql('5:1+::{"name":"tobi"}');
//  },
//
//  'encoding an event packet with data': function () {
//    parser.encodePacket({
//        type: 'event'
//      , name: 'edwald'
//      , endpoint: ''
//      , args: [{a: 'b'}, 2, '3']
//    }).should.eql('5:::{"name":"edwald","args":[{"a":"b"},2,"3"]}');
//  },
//
//  'encoding a message packet': function () {
//    parser.encodePacket({
//        type: 'message'
//      , endpoint: ''
//      , data: 'woot'
//    }).should.eql('3:::woot');
//  },
//
//  'encoding a message packet with id and endpoint': function () {
//    parser.encodePacket({
//        type: 'message'
//      , id: 5
//      , ack: true
//      , endpoint: '/tobi'
//      , data: ''
//    }).should.eql('3:5:/tobi');
//  },
//
//  'encoding a heartbeat packet': function () {
//    parser.encodePacket({
//        type: 'heartbeat'
//      , endpoint: ''
//    }).should.eql('2::');
//  },
//
//  'encoding a connection packet': function () {
//    parser.encodePacket({
//        type: 'connect'
//      , endpoint: '/tobi'
//      , qs: ''
//    }).should.eql('1::/tobi');
//  },
//
//  'encoding a connection packet with query string': function () {
//    parser.encodePacket({
//        type: 'connect'
//      , endpoint: '/test'
//      , qs: '?test=1'
//    }).should.eql('1::/test:?test=1');
//  },
//
//  'encoding a disconnection packet': function () {
//    parser.encodePacket({
//        type: 'disconnect'
//      , endpoint: '/woot'
//    }).should.eql('0::/woot');
//  },
//
//  'test decoding a payload': function () {
//    parser.decodePayload('\ufffd5\ufffd3:::5\ufffd7\ufffd3:::53d'
//      + '\ufffd3\ufffd0::').should.eql([
//        { type: 'message', data: '5', endpoint: '' }
//      , { type: 'message', data: '53d', endpoint: '' }
//      , { type: 'disconnect', endpoint: '' }
//    ]);
//  },
//
//  'test encoding a payload': function () {
//    parser.encodePayload([
//        parser.encodePacket({ type: 'message', data: '5', endpoint: '' })
//      , parser.encodePacket({ type: 'message', data: '53d', endpoint: '' })
//    ]).should.eql('\ufffd5\ufffd3:::5\ufffd7\ufffd3:::53d')
//  },
}
