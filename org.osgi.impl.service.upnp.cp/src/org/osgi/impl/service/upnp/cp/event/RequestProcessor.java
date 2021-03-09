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
package org.osgi.impl.service.upnp.cp.event;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.Hashtable;
import java.util.StringTokenizer;

public class RequestProcessor {
	private BufferedInputStream	in;
	@SuppressWarnings("unused")
	private String				reqUriPath;
	protected String			reqMethod;
	public Hashtable<String,String>	headers;
	public String				contentBody	= null;
	public String				errorMessage;

	public RequestProcessor(BufferedInputStream in) {
		this.in = in;
		headers = new Hashtable<>();
	}

	// function used for separating the given string into multiple tokens and
	// returns
	// a string array containing all the tokens.
	private String[] splitStr(String str) {
		StringTokenizer st = new StringTokenizer(str);
		int n = st.countTokens();
		String[] strs = new String[n];
		for (int i = 0; i < n; ++i) {
			strs[i] = st.nextToken();
		}
		return strs;
	}

	// This method parses the given input stream and retrieves all the values
	// from the input
	// stream and stores in appropriate variables. Stores all the header values
	// in headers
	// table, stores all the cookies in the reqCookies table.
	public int parseRequest() {
		String line;
		try {
			line = getLine();
			if (line == null || line.length() == 0) {
				setErrorMessage("Bad request");
				return 400;
			}
			String[] tokens = splitStr(line);
			if (tokens[0].equals("HTTP/1.1")) {
				if (!tokens[1].equals("200")) {
					setErrorMessage(tokens[2]);
					return Integer.parseInt(tokens[1].trim());
				}
			}
			while (true) {
				line = getLine();
				if (line == null || line.length() == 0) {
					break;
				}
				int colonBlank = line.indexOf(": ");
				if (colonBlank != -1) {
					String name = line.substring(0, colonBlank);
					String value = line.substring(colonBlank + 2);
					headers.put(name.toLowerCase(), value);
				}
			}
			reqMethod = tokens[0];
			reqUriPath = tokens[1];
			String host = headers.get("host");
			if (reqMethod.equals("NOTIFY")) {
				if (host == null) {
					setErrorMessage("PreCondition Failed");
					return 412;
				}
				String content_type = headers.get("content-type");
				if (content_type == null) {
					setErrorMessage("PreCondition Failed");
					return 412;
				}
				if (!content_type.equals("text/xml")) {
					setErrorMessage("Bad Request");
					return 400;
				}
				if (headers.get("content-length") == null) {
					setErrorMessage("PreCondition Failed");
					return 412;
				}
				int contentLength = Integer.parseInt(headers
						.get("content-length"));
				if (contentLength <= 0) {
					setErrorMessage("Length Required");
					return 411;
				}
				byte abyte0[] = new byte[contentLength];
				int i2 = 0;
				for (int l1 = 0; l1 < contentLength; l1 += i2) {
					try {
						i2 = in.read(abyte0, l1, contentLength - l1);
					}
					catch (Exception e) {
						System.out.println(e.getMessage());
					}
					if (i2 == -1) {
						setErrorMessage("Length Required");
						return 411;
					}
				}
				contentBody = new String(abyte0);
			}
			return 200;
		}
		catch (IOException e) {
			setErrorMessage("Bad Request");
			return 400;
		}
	}

	// Returns a line from the input stream as a string.
	private String getLine() throws IOException {
		int val = in.read();
		if (val == -1) {
			return null;
		}
		StringBuffer stringbuffer = new StringBuffer();
		for (; val != -1 && val != 13 && val != 10; val = in.read()) {
			stringbuffer.append((char) val);
		}
		if (val == 13) {
			in.read();
		}
		return stringbuffer.toString();
	}

	// This method sets the error message.
	void setErrorMessage(String message) {
		errorMessage = message;
	}
}
