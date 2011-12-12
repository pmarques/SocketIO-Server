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
package eu.k2c.socket.io.server.api;

import eu.k2c.socket.io.server.exceptions.SocketIOException;

/**
 * @author "Patrick F. Marques <patrick.marques@k2c.eu>"
 */
public interface SocketIOOutbound {
	/**
	 * Terminate the connection. This method may return before the connection
	 * disconnect completes. The onDisconnect() method of the associated
	 * SocketInbound will be called when the disconnect is completed. The
	 * onDisconnect() method may be called during the invocation of this method.
	 */
	void disconnect();

	/**
	 * Initiate an orderly close of the connection. The state will be changed to
	 * CLOSING so no new messages can be sent, but messages may still arrive
	 * until the distant end has acknowledged the close.
	 */
	void close();

	ConnectionState getConnectionState();

	/**
	 * Send a text message to the client. This method will block if the message
	 * will not fit in the outbound buffer. If the socket is closed, becomes
	 * closed, or times out, while trying to send the message, the
	 * SocketClosedException will be thrown.
	 * 
	 * @param message
	 *            The message to send
	 * @param endPoint
	 *            The message endPoint
	 * 
	 * @throws SocketIOException
	 */
	void sendMessage(final String message, final String endPoint) throws SocketIOException;

	/**
	 * Send a JSON message to the client. This method will block if the message
	 * will not fit in the outbound buffer. If the socket is closed, becomes
	 * closed, or times out, while trying to send the message, the
	 * SocketClosedException will be thrown.
	 * 
	 * @param message
	 *            The JSON message to send
	 * @param endPoint
	 *            The message endPoint
	 * 
	 * @throws IllegalStateException
	 *             if the socket is not CONNECTED.
	 * @throws SocketIOException
	 */
	void sendJSONMessage(final String message, final String endPoint) throws SocketIOException;

	/**
	 * Send a event message to a client.
	 * 
	 * @param eventName
	 *            Event name
	 * @param message
	 *            The JSON message to send
	 * @param endPoint
	 *            The message endPoint
	 * @throws SocketIOException
	 */
	void sendEventMessage(final String eventName, final String message, final String endPoint) throws SocketIOException;

	/**
	 * Force to send an acknowledge for a received message.
	 * This is necessary when a package is sent with '+' on message id.
	 * NOTE: This methode could disappear due to the implications in the
	 * correctness in terms of SocketIO flow
	 * 
	 * @param message
	 *            The JSON message to send
	 * @param endPoint
	 *            The message endPoint
	 * @param handler
	 *            A method to be executed by the acknowledge of this packet
	 * 
	 * @throws SocketIOException
	 */
	void sendJSONMessage(String string, final String endPoint, AckHandler ackHandler) throws SocketIOException;

	/**
	 * Force to send an acknowledge for a received message.
	 * This is necessary when a package is sent with '+' on message id.
	 * NOTE: This methode could disappear due to the implications in the
	 * correctness in terms of SocketIO flow
	 * 
	 * @param message
	 *            The message to send
	 * @param endPoint
	 *            The message endPoint
	 * @param handler
	 *            A method to be executed by the acknowledge of this packet
	 * 
	 * @throws SocketIOException
	 */
	void sendMessage(String string, final String endPoint, AckHandler ackHandler) throws SocketIOException;

	/**
	 * Send a event message to a client.
	 * 
	 * @param eventName
	 *            Event name
	 * @param message
	 *            The JSON message to send
	 * @param endPoint
	 *            The message endPoint
	 * @param handler
	 *            A method to be executed by the acknowledge of this packet
	 * 
	 * @throws SocketIOException
	 */
	void sendEventMessage(final String eventName, final String message, final String endPoint, final AckHandler handler)
			throws SocketIOException;

	/**
	 * Force to send an acknowledge for a received message.
	 * This is necessary when a package is sent with '+' on message id.
	 * NOTE: This method could disappear due to the implications in the
	 * correctness in terms of SocketIO flow
	 * 
	 * @param messageID
	 *            Event name
	 * @param message
	 *            The message to send
	 * 
	 * @throws SocketIOException
	 */
	void sendAck(final long messageID, final String message) throws SocketIOException;
}
