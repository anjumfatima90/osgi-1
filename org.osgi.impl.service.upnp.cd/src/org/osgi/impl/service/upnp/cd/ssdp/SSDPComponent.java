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
package org.osgi.impl.service.upnp.cd.ssdp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Enumeration;
import java.util.Hashtable;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.impl.service.upnp.cd.control.SOAPConstants;
import org.osgi.service.http.HttpService;

// This class initiated ssdp and exporter  functionalities.It maintains the ssdp related 
// databse. It have NOTIFY BYE functionality.
public class SSDPComponent implements SSDPConstants {
	private SSDPMulticastListener	multicastListener;
	private DeviceRenewalThread		deviceRenewalThread;
	private UPnPExporter			exporter;
	public String					cacheControl;
	public long						cacheValue;
	public String					baseURL;
	public String					genaURL;
	public String					server;
	private String					msearchResponse;
	private MulticastSocket			multicastsock;
	private InetAddress				ssdpinet;
	public Hashtable<String,DeviceDetails>	allDeviceDetails;
	private byte					ttl	= 4;
	private String					notifyByeMessage;
	public EventAccessForExporter	eventregistry;

	// This constructor construct the SDDPcomponent. It initializez
	// all required information for discovery layer.
	public SSDPComponent(String cache, BundleContext bc, String genaIP,
			EventAccessForExporter evRegistry) throws Exception {
		cacheControl = cache;
		Long chVal = Long.valueOf(cache);
		cacheValue = chVal.longValue();
		genaURL = genaIP;
		eventregistry = evRegistry;
		server = new String(SOAPConstants.osNameVersion
				+ " UPNP/1.0 SAMSUNG-UPnP-STACK/1.0");
		allDeviceDetails = new Hashtable<>(10);
		HttpService httpService = null;
		try {
			ServiceReference<HttpService> sr = bc
					.getServiceReference(HttpService.class);
			if (bc.getProperty("org.osgi.service.http.port") != null)
				baseURL = genaIP.substring(0, genaIP.indexOf(":") + 1)
						+ bc.getProperty("org.osgi.service.http.port");
			else
				baseURL = genaIP.substring(0, genaIP.indexOf(":") + 1) + "8080";
			httpService = bc.getService(sr);
			//Creating SSDP multicast socket
			multicastsock = new MulticastSocket(HOST_PORT);
			ssdpinet = InetAddress.getByName(HOST_IP);
			multicastsock.joinGroup(ssdpinet);
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
		}
		//Creating UPnPExporter for exporting all exported devices
		exporter = new UPnPExporter(this, bc, httpService);
	}

	// This method starts the Discovery functionality
	public void startSSDPFunctionality() throws Exception {
		try {
			startMulticastListener();
			deviceRenewalThread = new DeviceRenewalThread(this);
			deviceRenewalThread.start();
			exporter.startExporter();
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
			throw e;
		}
	}

	// This method starts the Mulicast listener. This listener getts all ssdp
	// multicast messages
	void startMulticastListener() throws Exception {
		try {
			multicastListener = new SSDPMulticastListener(this);
			multicastListener.start();
		}
		catch (Exception e) {
			System.out.println("Unable to create multicast listener");
		}
	}

	// This method reads device details of CD Bundle device and all exported
	// devices. It calls
	// sendNotifyBye() method by passing the required details.
	void readDevicesForNotifyBye() {
		for (Enumeration<DeviceDetails> enumeration = allDeviceDetails
				.elements(); enumeration
				.hasMoreElements();) {
			DeviceDetails devDet = enumeration.nextElement();
			sendDeviceForNotifyBye(devDet.getUUID());
		}
	}

	// This method calls sendNotifyBye() method by passing the required details.
	public void sendDeviceForNotifyBye(String uuid) {
		DeviceDetails devDet = allDeviceDetails.get(uuid);
		if (devDet != null) {
			if (devDet.isRoot()) {
				sendNotifyBye("root", uuid, devDet.getDevType(), null);
			}
			else {
				sendNotifyBye("embdev", uuid, devDet.getDevType(), null);
			}
			Hashtable<String,String> services = devDet.getServices();
			for (Enumeration<String> e = services.elements(); e
					.hasMoreElements();) {
				String serType = e.nextElement();
				sendNotifyBye("service", uuid, null, serType);
			}
			allDeviceDetails.remove(uuid);
		}
	}

	// This method sends notify BYE messages according input parameters
	void sendNotifyBye(String type, String uuid, String deviceType,
			String serviceType) {
		String usn = null;
		String nt = null;
		if (type.equals("root")) {
			usn = new String(uuid + "::" + ROOTDEVICE);
			sendMessage(ROOTDEVICE, usn);
			nt = uuid;
			usn = uuid;
			sendMessage(nt, usn);
			nt = new String(deviceType);
			usn = new String(uuid + "::" + deviceType);
			sendMessage(nt, usn);
		}
		else
			if (type.equals("embdev")) {
				nt = uuid;
				usn = uuid;
				sendMessage(nt, usn);
				nt = new String(deviceType);
				usn = new String(uuid + "::" + deviceType);
				sendMessage(nt, usn);
			}
			else
				if (type.equals("service")) {
					nt = new String(serviceType);
					usn = new String(uuid + "::" + serviceType);
					sendMessage(nt, usn);
				}
	}

	// This method used by sendNotifyBye method for sending messages
	void sendMessage(String nt, String usn) {
		String notify = null;
		byte data[] = null;
		DatagramPacket packet = null;
		notify = makeNotifyByeMessage(nt, usn);
		if (notify != null) {
			data = notify.getBytes();
			packet = new DatagramPacket(data, data.length, ssdpinet, HOST_PORT);
			for (int i = 0; i <= 1; i++) {
				try {
					multicastsock.setTimeToLive(ttl);
					multicastsock.send(packet);
				}
				catch (IOException e) {
					System.out.println(e.getMessage());
				}
			}
		}
	}

	// This method used for making NOTIFY BYE messages
	String makeNotifyByeMessage(String nt, String usn) {
		notifyByeMessage = new String(NOTIFY + "\r\n" + HOST + "\r\n" + NT
				+ " " + nt + "\r\n" + NTS + " " + BYEBYE + "\r\n" + USN + " "
				+ usn + "\r\n\r\n");
		return notifyByeMessage;
	}

	// This method used for making M-SEARCH responses
	String makeMsearchResponse(String dateValue, String st, String usn,
			String location) {
		msearchResponse = new String(MSEARCH_RESP + "\r\n" + CACHE
				+ " max-age = " + cacheControl + "\r\n" + DATE1 + " "
				+ dateValue + "\r\n" + EXT + "\r\n" + LOC + " " + location
				+ "\r\n" + SERVER + " " + server + "\r\n" + ST + " " + st
				+ "\r\n" + USN + " " + usn + "\r\n\r\n");
		return msearchResponse;
	}

	// This method add a new DeviceDetails object to the allDeviceDetails list.
	// This list used
	// for sending notiyfy messages and msearch responses for all devices.
	synchronized void addDeviceDetails(String uuid, DeviceDetails info) {
		if ((uuid != null) && (info != null)) {
			allDeviceDetails.put(uuid, info);
		}
	}

	// This method removes a DeviceDetails object fom the allDeviceDetails list.
	// This list used
	// for sending notiyfy messages and msearch responses for all devices.
	synchronized void removeDeviceDetails(String uuid) {
		if (uuid != null) {
			allDeviceDetails.remove(uuid);
		}
	}

	// This method kills all SSDP functionality
	public void killSSDP() {
		readDevicesForNotifyBye();
		if (exporter != null) {
			exporter.stopExporter();
		}
		if (multicastListener != null) {
			multicastListener.killMulticastListener();
		}
		if (deviceRenewalThread != null) {
			deviceRenewalThread.killDeviceRenewalThread();
		}
		multicastListener = null;
		deviceRenewalThread = null;
		multicastsock = null;
		ssdpinet = null;
	}
}
