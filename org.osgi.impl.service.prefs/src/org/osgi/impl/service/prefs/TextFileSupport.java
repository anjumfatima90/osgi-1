/*******************************************************************************
 * Copyright (c) Contributors to the Eclipse Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0 
 *******************************************************************************/
package org.osgi.impl.service.prefs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;

import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

/**
 * @author $Id$
 */
class TextFileSupport {
	static void read(File preferencesFile, Preferences prefs) {
		try {
			if (!preferencesFile.exists()) {
				return;
			}
			FileInputStream fis = new FileInputStream(preferencesFile);
			InputStreamReader isr = new InputStreamReader(fis, "UTF8");
			BufferedReader br = new BufferedReader(isr);
			importPreferences(br, prefs);
			br.close();
		}
		catch (Exception e) {
			System.err.println("Couldn't read preferences from: "
					+ preferencesFile);
			e.printStackTrace();
		}
	}

	static void write(File preferencesFile, Preferences prefs)
			throws BackingStoreException {
		try {
			preferencesFile.delete();
			OutputStreamWriter osw = new OutputStreamWriter(
					new FileOutputStream(preferencesFile), "UTF8");
			exportPreferences(osw, prefs);
			osw.close();
		}
		catch (IOException e) {
			throw new BackingStoreException("Couldn't write preferences to: "
					+ preferencesFile, e);
		}
	}

	/**
	 * Export the specified preferences node and all subnodes to the specified
	 * output stream.
	 * 
	 * @throws IOException if writing to the specified output stream results in
	 *         an <tt>IOException</tt>.
	 * @throws BackingStoreException if preference data cannot be read from
	 *         backing store.
	 */
	private static void exportPreferences(OutputStreamWriter osw,
			Preferences prefs) throws IOException, BackingStoreException {
		try {
			output(osw, prefs.absolutePath());
			osw.write('\n');
			String[] keys = prefs.keys();
			for (int i = 0; i < keys.length; i++) {
				output(osw, keys[i]);
				osw.write('=');
				output(osw, prefs.get(keys[i], null)); // default value
													   // shouldn't get used
				osw.write('\n');
			}
			// Recurse
			String[] kids = prefs.childrenNames();
			for (int i = 0; i < kids.length; i++) {
				exportPreferences(osw, prefs.node(kids[i]));
			}
		}
		catch (IllegalStateException ignored) {
			// prefs node must have been removed - ignore it
		}
	}

	/**
	 * Import preferences from the specified input stream.
	 * 
	 * @throws IOException if reading from the specified output stream results
	 *         in an <tt>IOException</tt>.
	 * @throws InvalidPreferencesFormatException if the input Properties file
	 *         has an invalid key.
	 */
	private static void importPreferences(BufferedReader br, Preferences root)
			throws IOException, InvalidPreferencesFormatException {
		int nextChar = br.read();
		do {
			if (nextChar != '/') {
				throw new InvalidPreferencesFormatException("'/' expected");
			}
			Preferences prefs = root.node(readLine(br));
			nextChar = importPrefs(br, prefs);
		} while (nextChar != -1);
	}

	private static int importPrefs(BufferedReader br, Preferences prefs)
			throws IOException {
		int nextChar;
		for (;;) {
			nextChar = br.read();
			if (nextChar == -1 || nextChar == '/') {
				break;
			}
			String key = readKey(nextChar, br);
			String value = readLine(br);
			prefs.put(key, value);
		}
		return nextChar;
	}

	/**
	 * Reads a line, undoing escapes for '/', '=', '\r' and '\n'
	 */
	private static String readLine(BufferedReader br) throws IOException {
		String line = br.readLine();
		StringBuffer buf = new StringBuffer(line);
		return unescape(buf);
	}

	/**
	 * Read a key, terminated by an unescaped '=' character. Unescape the
	 * result.
	 */
	private static String readKey(int nextChar, Reader br) throws IOException {
		StringBuffer buf = new StringBuffer();
		while (nextChar != '=' && nextChar != -1) {
			buf.append((char) nextChar);
			nextChar = br.read();
		}
		return unescape(buf);
	}

	/**
	 * Output string, escaping newline, carriage-return, backslash and equals
	 * characters.
	 */
	private static void output(OutputStreamWriter osw, String string)
			throws IOException {
		for (int i = 0; i < string.length(); i++) {
			char ch = string.charAt(i);
			if (ch == '\n') {
				osw.write('\n');
			}
			else
				if (ch == '\r') {
					osw.write('\r');
				}
				else {
					if (ch == '\\' || ch == '=') {
						osw.write('\\');
					}
					osw.write(ch);
				}
		}
	}

	/**
	 * Return string equal to buffer but with escapes undone.
	 */
	private static String unescape(StringBuffer buf) {
		int i = 0;
		int j = 0;
		int n = buf.length();
		while (j < n) {
			int ch = buf.charAt(j++);
			if (ch == '\\' && j < n) {
				ch = buf.charAt(j++);
				if (ch == 'r') {
					ch = '\r';
				}
				else
					if (ch == 'n') {
						ch = '\n';
					}
			}
			buf.setCharAt(i++, (char) ch);
		}
		buf.setLength(i);
		return buf.toString();
	}
}
