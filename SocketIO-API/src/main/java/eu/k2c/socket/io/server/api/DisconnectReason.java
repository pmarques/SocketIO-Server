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
public enum DisconnectReason {
	/**
	 * Unknown reason
	 */
	UNKNOWN(
			-1),
	/**
	 * Connection attempt failed.
	 */
	CONNECT_FAILED(
			1),
	/**
	 * Disconnect was called explicitly.
	 */
	DISCONNECT(
			2),
	/**
	 * A timeout occurred.
	 */
	TIMEOUT(
			3),
	/**
	 * The connection dropped before an orderly close could complete.
	 */
	CLOSE_FAILED(
			4),
	/**
	 * A GET or POST returned an error, or an internal error occurred.
	 */
	ERROR(
			5),
	/**
	 * Remote end point initiated a close.
	 */
	CLOSED_REMOTELY(
			6),
	/**
	 * Locally initiated close succeeded.
	 */
	CLOSED(
			6);

	private int value;

	private DisconnectReason(final int value) {
		this.value = value;
	}

	/**
	 * 
	 * @return DisconnectReason value
	 */
	public int value() {
		return value;
	}

	/**
	 * Create a DisconnectReason from a int value
	 * 
	 * @param value
	 * 
	 * @return {@link DisconnectReason}
	 */
	public static DisconnectReason fromInt(final int value) {
		for (DisconnectReason type : values()) {
			if (type.value == value)
				return type;
		}

		return UNKNOWN;
	}
}