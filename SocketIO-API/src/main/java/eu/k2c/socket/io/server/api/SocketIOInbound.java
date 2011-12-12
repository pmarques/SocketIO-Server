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

/**
 * @author "Patrick F. Marques <patrick.marques@k2c.eu>"
 */
public interface SocketIOInbound {

	/**
	 * Called before the connection is established. This will provide the
	 * ability
	 * to validate some parameters from connection string.
	 * 
	 * @param URI
	 *            The SocketOutbound associated with the connection
	 * 
	 * @return
	 *         - true, proceed to handshake
	 *         - false, fail the handshake
	 */
	public boolean validate(final String URI);

	/**
	 * Called when the connection is established. This will only ever be called
	 * once.
	 * 
	 * @param outbound
	 *            The SocketOutbound associated with the connection
	 * @param endPoint
	 *            Topic where the message belongs
	 */
	public void onConnect(final SocketIOOutbound outbound, final String endPoint);

	/**
	 * Called when the socket connection is closed. This will only ever be
	 * called once. This method may be called instead of onConnect() if the
	 * connection handshake isn't completed successfully.
	 * 
	 * @param reason
	 *            The reason for the disconnect.
	 * @param errorMessage
	 *            Possibly non null error message associated with the reason for
	 *            disconnect.
	 */
	public void onDisconnect(final DisconnectReason reason, final String errorMessage);

	/**
	 * Called when the socket connection is closed. This will only ever be
	 * called once. This method may be called instead of onConnect() if the
	 * connection handshake isn't completed successfully.
	 * 
	 * @param reason
	 *            The reason for the disconnect.
	 * @param errorMessage
	 *            Possibly non null error message associated with the reason for
	 *            disconnect.
	 * @param endPoint
	 *            Topic where the message belongs
	 */
	public void onDisconnect(final DisconnectReason reason, final String errorMessage, final String endPoint);

	/**
	 * Called one per arriving a text message.
	 * 
	 * @param messageID
	 *            Unique message identifier
	 * @param endPoint
	 *            Topic where the message belongs
	 * @param message
	 *            Message data
	 */
	public void onMessage(final long messageID, final String endPoint, final String message);

	/**
	 * Called one per arriving a JSON message.
	 * 
	 * @param messageID
	 *            Unique message identifier
	 * @param endPoint
	 *            Topic where the message belongs
	 * @param message
	 *            Message data in JSON format
	 */
	public void onJSONMessage(final long messageID, final String endPoint, final String message);

	/**
	 * Called to handle Socket.IO events. Only deals events which are not
	 * registered in the event listener with
	 * {@link SocketIOSessionEventRegister}
	 * 
	 * @param messageID
	 *            Unique message identifier
	 * @param endPoint
	 *            Topic where the message belongs
	 * @param eventName
	 *            Name of received event
	 * @param data
	 *            Message data in JSON format
	 */
	public void onEvent(final long messageID, final String endPoint, final String eventName, final String data);

	/**
	 * Called to handle a acknowledgment which isn't handled by the socket.io
	 * 
	 * @param messageID
	 * @param data
	 */
	public void onAck(final long messageID, final String data);

	/**
	 * Called when a error occurs
	 * 
	 * @param endPoint
	 * @param reason
	 * @param advice
	 */
	public void onError(final String endPoint, final String reason, final String advice);
}
