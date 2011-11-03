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
package eu.k2c.socket.io.server.frames;

/**
 * Socket.IO frames
 * 
 * @see <a href="https://github.com/LearnBoost/socket.io-spec">Socket.IO
 *      Spec</a>
 */
public enum FrameType {
    /**
     * Unknown frame
     */
    UNKNOWN(-1),
    /**
     * Disconnect frame
     */
    DISCONNECT(0),
    /**
     * Connect frame
     */
    CONNECT(1),
    /**
     * HeartBeat frame
     */
    HEARTBEAT(2),
    /**
     * Text Message frame
     */
    MESSAGE(3),
    /**
     * JSON Message frame
     */
    JSON(4),
    /**
     * Event frame
     */
    EVENT(5),
    /**
     * Acknowledge frame
     */
    ACK(6),
    /**
     * Error frame
     */
    ERROR(7),
    /**
     * No operation frame. Used for example to close a poll after the polling
     * duration times out.
     */
    NOOP(8);

    private int value;

    private FrameType(final int value) {
	this.value = value;
    }

    public int value() {
	return value;
    }

    /**
     * Create a FrameType from a int value
     * 
     * @param value
     * 
     * @return {@link FrameType}
     */
    public static FrameType fromInt(final int value) {
	for (FrameType type : values()) {
	    if (type.value == value)
		return type;
	}

	return UNKNOWN;
    }
}