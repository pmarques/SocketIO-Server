/**
 * The MIT License
 * Copyright (c) 2010 Tad Glines
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package eu.k2c.socket.io.server.api;

/**
 * @author "Patrick F. Marques <patrick.marques@k2c.eu>"
 */
public enum ConnectionState {
    /**
     * Socket is an unknown state;
     */
    UNKNOWN(-1),
    /**
     * Socket is connecting;
     */
    CONNECTING(0),
    /**
     * Socket is connected;
     */
    CONNECTED(1),
    /**
     * Socket is closing;
     */
    CLOSING(2),
    /**
     * Socket is close;
     */
    CLOSED(3);

    private int value;

    private ConnectionState(final int value) {
	this.value = value;
    }

    /**
     * 
     * @return Connection State value
     */
    public int value() {
	return value;
    }

    /**
     * Create a ConnectionState from a int value
     * 
     * @param value
     * @return {@link ConnectionState}
     */
    public static ConnectionState fromInt(final int value) {
	for (ConnectionState type : values()) {
	    if (type.value == value)
		return type;
	}

	return UNKNOWN;
    }
}
