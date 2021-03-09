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
package org.osgi.impl.service.upnp.cd;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.impl.service.upnp.cd.control.ControlImpl;
import org.osgi.impl.service.upnp.cd.event.EventRegistry;
import org.osgi.impl.service.upnp.cd.event.GenaServer;
import org.osgi.impl.service.upnp.cd.event.SubscriptionAlive;
import org.osgi.impl.service.upnp.cd.ssdp.SSDPComponent;

public class UPnPController implements BundleActivator {
	private String				devexp;
	private SSDPComponent		ssdpcomp;
	private ControlImpl			control;
	private GenaServer			server;
	private SubscriptionAlive	sa;
	@SuppressWarnings("unused")
	private BundleContext		bc;

	// This method starts the CD bundle
	@Override
	public void start(@SuppressWarnings("hiding") BundleContext bc)
			throws Exception {
		//System.out.println("UPnP : starting CD exporter");
		this.bc = bc;
		devexp = "2100";
		String IP = bc.getProperty("org.osgi.service.http.hostname");
		try {
			if (IP == null) {
				IP = InetAddress.getLocalHost().getHostAddress();
			}
			else {
				IP = InetAddress.getByName(IP).getHostAddress();
			}
		}
		catch (UnknownHostException e) {
			IP = "127.0.0.1";
		}
		//Control starting
		control = new ControlImpl();
		//Eventing starting
		EventRegistry eventregistry = new EventRegistry(IP);
		sa = new SubscriptionAlive();
		sa.start();
		try {
			server = new GenaServer(8180, control, bc, eventregistry);
			server.start();
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
		}
		//SSDDP starting
		try {
			ssdpcomp = new SSDPComponent(devexp, bc, server.getServerIP(),
					eventregistry);
			ssdpcomp.startSSDPFunctionality();
			//System.out.println("Discovery started");
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	// This method stops the bundle
	@Override
	public void stop(@SuppressWarnings("hiding") BundleContext bc)
			throws Exception {
		try {
			if (ssdpcomp != null) {
				ssdpcomp.killSSDP();
			}
			if (server != null) {
				server.shutdown();
			}
			if (sa != null) {
				sa.surrender(false);
			}
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
			throw e;
		}
	}
}
