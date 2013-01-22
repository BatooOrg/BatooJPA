/*
 * Copyright (c) 1996, 2011, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package org.batoo.jpa.core.impl.nativequery;

import java.io.IOException;
import java.io.Reader;

/**
 * An un-synchronised version of BufferedReader.
 * 
 * @author hceylan
 * @since 2.0.0
 */
public class SqlReader {

	private static final int INVALIDATED = -2;
	private static final int UNMARKED = -1;

	private static int defaultExpectedLineLength = 80;

	private final Reader in;

	private char cb[];
	private int nChars, nextChar;

	private int markedChar = SqlReader.UNMARKED;
	private int readAheadLimit = 0;

	private boolean skipLF = false;

	/**
	 * Creates a buffering character-input stream that uses a default-sized input buffer.
	 * 
	 * @param in
	 *            A Reader
	 */
	public SqlReader(Reader in) {
		super();

		this.in = in;
		this.cb = new char[8192];
		this.nextChar = this.nChars = 0;
	}

	/**
	 * Fills the input buffer, taking the mark into account if it is valid.
	 * 
	 * @throws IOException
	 * 
	 * @since 2.0.0
	 */
	private void fill() throws IOException {
		int dst;
		if (this.markedChar <= SqlReader.UNMARKED) {
			// No mark
			dst = 0;
		}
		else {
			// Marked
			final int delta = this.nextChar - this.markedChar;
			if (delta >= this.readAheadLimit) {
				// Gone past read-ahead limit: Invalidate mark
				this.markedChar = SqlReader.INVALIDATED;
				this.readAheadLimit = 0;

				dst = 0;
			}
			else {
				if (this.readAheadLimit <= this.cb.length) {
					// Shuffle in the current buffer
					System.arraycopy(this.cb, this.markedChar, this.cb, 0, delta);
					this.markedChar = 0;
					dst = delta;
				}
				else {
					// Reallocate buffer to accommodate read-ahead limit
					final char ncb[] = new char[this.readAheadLimit];
					System.arraycopy(this.cb, this.markedChar, ncb, 0, delta);
					this.cb = ncb;
					this.markedChar = 0;
					dst = delta;
				}
				this.nextChar = this.nChars = delta;
			}
		}

		int n;
		do {
			n = this.in.read(this.cb, dst, this.cb.length - dst);
		}
		while (n == 0);
		if (n > 0) {
			this.nChars = dst + n;
			this.nextChar = dst;
		}
	}

	/**
	 * Reads a line of text. A line is considered to be terminated by any one of a line feed ('\n'), a carriage return ('\r'), or a carriage
	 * return followed immediately by a linefeed.
	 * 
	 * @return A String containing the contents of the line, not including any line-termination characters, or null if the end of the stream
	 *         has been reached
	 * 
	 * @exception IOException
	 *                If an I/O error occurs
	 * 
	 * @see java.nio.file.Files#readAllLines
	 * 
	 * @since 2.0.0
	 */
	public String readLine() throws IOException {
		StringBuilder s = null;
		int startChar;

		boolean omitLF = this.skipLF;

		for (;;) {

			if (this.nextChar >= this.nChars) {
				this.fill();
			}
			if (this.nextChar >= this.nChars) { /* EOF */
				if ((s != null) && (s.length() > 0)) {
					return s.toString();
				}
				else {
					return null;
				}
			}

			boolean eol = false;
			char c = 0;
			int i;

			/* Skip a leftover '\n', if necessary */
			if (omitLF && (this.cb[this.nextChar] == '\n')) {
				this.nextChar++;
			}
			this.skipLF = false;
			omitLF = false;

			charLoop:
			for (i = this.nextChar; i < this.nChars; i++) {
				c = this.cb[i];
				if ((c == '\n') || (c == '\r')) {
					eol = true;
					break charLoop;
				}
			}

			startChar = this.nextChar;
			this.nextChar = i;

			if (eol) {
				String str;
				if (s == null) {
					str = new String(this.cb, startChar, i - startChar);
				}
				else {
					s.append(this.cb, startChar, i - startChar);
					str = s.toString();
				}
				this.nextChar++;
				if (c == '\r') {
					this.skipLF = true;
				}
				return str;
			}

			if (s == null) {
				s = new StringBuilder(SqlReader.defaultExpectedLineLength);
			}

			s.append(this.cb, startChar, i - startChar);
		}
	}
}
