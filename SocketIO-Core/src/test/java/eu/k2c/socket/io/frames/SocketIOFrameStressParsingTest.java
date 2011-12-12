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

import org.junit.Test;

import eu.k2c.socket.io.frames.SocketIOFrameParser;

public class SocketIOFrameStressParsingTest {

	@Test(timeout = 1000)
	public void testParseStressed() throws Exception {
		// long t0 = System.currentTimeMillis();
		stressJSONParser(250 * 1000);
		// System.out.println(System.currentTimeMillis() - t0 + "ms");
	}

	private void stressJSONParser(int runs) throws Exception {
		String event = "5:::{\"name\":\"user.login\",\"args\":[{\"user\":\"asd\",\"password\":\"qwe\",\"uuid\":\"9838D8D2-1027-42BA-BD2D-9DCE4EFDC267\"}]}";

		for (int i = 0; i < runs; ++i)
			SocketIOFrameParser.decode(event);
	}
}
