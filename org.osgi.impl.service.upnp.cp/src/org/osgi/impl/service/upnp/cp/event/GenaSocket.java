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
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;

public class GenaSocket {
	private BufferedInputStream	in;
	private OutputStream		dos;
	private RequestProcessor	requestparser;
	public String				receivedTimeout;
	public String				sid;
	public long					timeDuration;
	private Socket				clientSocket;

	/**
	 * This method creates a socket and returns it. Based on the given path,
	 * extracts the host and the port for creating the socket.If any error
	 * occurs while creating the socket, throws GenaException with the error
	 * message.
	 */
	void createSocket(String path) throws Exception {
		try {
			URL url = new URL(path);
			String host = url.getHost();
			int port = url.getPort();
			if (port == -1) {
				port = 80;
			}
			clientSocket = new Socket(host, port);
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	// This method writes the given message to the client.
	void sendSocket(String message) {
		try {
			in = new BufferedInputStream(clientSocket.getInputStream());
			dos = clientSocket.getOutputStream();
			dos.write(message.getBytes());
			dos.flush();
			clientSocket.setSoTimeout(30 * 1000);
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	// This method checks for the subscription id. And if the subscription id is
	// invalid.
	void checkSubscriptionId() throws Exception {
		sid = requestparser.headers.get("sid");
		if (sid == null || !sid.startsWith("uuid:")) {
			throw new Exception("Invalid sid received, please try again");
		}
	}

	// This method checks for the timeout duration. And if the timeout duration
	// is invalid
	void checkTimeoutDuration() throws Exception {
		receivedTimeout = requestparser.headers.get("timeout");
		if (receivedTimeout == null) {
			throw new Exception("Invalid timeout received,please try again");
		}
		if (!receivedTimeout.equals("infinite")) {
			int result = receivedTimeout.indexOf("-");
			timeDuration = Integer.parseInt(receivedTimeout
					.substring(result + 1));
			timeDuration = (timeDuration * 1000) + System.currentTimeMillis();
		}
		else {
			timeDuration = 0;
		}
	}

	// This method parses the request and if the request is unable to get parsed
	boolean parseRequest() {
		requestparser = new RequestProcessor(in);
		int result = requestparser.parseRequest();
		if (result != 200) {
			return false;
		}
		try {
			clientSocket.close();
			dos.close();
			in.close();
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return true;
	}
}
