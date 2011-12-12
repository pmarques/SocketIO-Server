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
public interface SocketIOSessionNSRegister {
	/**
	 * Register a event listener for client session. When a event with a
	 * {@link evenName} is received, this is send to the {@link handler}. If
	 * there is no event listener for the coming event this is will be sent
	 * to the {@link SocketIOSession#onEvent}
	 * 
	 * @param eventName
	 *            Event name to be added.
	 * @param handler
	 *            Event listener.
	 */
	void registerNSListeners(final String eventName, final SocketIONSHandler handler);

	/**
	 * Remove event listener from client session
	 * 
	 * @param eventName
	 *            Event Name to be removed.
	 */
	void unRegisterNSListeners(final String eventName);

	/**
	 * Remove all event listeners from the client session.
	 */
	void removeNS();

	public SocketIONSHandler getHandler(final String eventName);
}