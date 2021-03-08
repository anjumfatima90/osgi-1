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
package org.osgi.impl.service.upnp.cp.basedriver;

import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.impl.service.upnp.cp.description.RootDevice;
import org.osgi.impl.service.upnp.cp.util.Control;
import org.osgi.impl.service.upnp.cp.util.EventService;
import org.osgi.impl.service.upnp.cp.util.UPnPController;
import org.osgi.impl.service.upnp.cp.util.UPnPDeviceListener;
import org.osgi.service.upnp.UPnPDevice;

public class UPnPBaseDriver implements UPnPDeviceListener {
	private UPnPController	controller;
	public RootDevice		deviceinfo;
	public Control			control;
	public EventService		eventservice;
	private Hashtable<String,UPnPDeviceImpl>			devices;
	private BundleContext	bc;
	private Hashtable<String,ServiceRegistration<UPnPDevice>>	servicerefs;
	private String			parentUDN;

	// This constructor creates the UPnPBaseDriver object based on the
	// controller and the BundleContext object.
	public UPnPBaseDriver(UPnPController controller, BundleContext bc) {
		this.controller = controller;
		this.bc = bc;
		devices = new Hashtable<>(10);
		servicerefs = new Hashtable<>(10);
	}

	// This method starts the base driver. And registers with controller for
	// getting notifications.
	public void start() {
		controller.registerDeviceListener(this);
		control = controller.getControl();
		eventservice = controller.getEventService();
	}

	// This method stops the base driver. And unregisters with controller for
	// getting notifications.
	public void stop() {
		controller.unRegisterDeviceListener(this);
		for (Enumeration<UPnPDeviceImpl> enumeration = devices
				.elements(); enumeration.hasMoreElements();) {
			UPnPDeviceImpl dev = enumeration.nextElement();
			dev.unsubscribe();
		}
		for (Enumeration<ServiceRegistration<UPnPDevice>> enumeration = servicerefs
				.elements(); enumeration.hasMoreElements();) {
			ServiceRegistration<UPnPDevice> sreg = enumeration.nextElement();
			if (sreg != null) {
				try {
					sreg.unregister();
				}
				catch (Exception e) {
					System.out.println(e.getMessage());
				}
			}
		}
		control = null;
		eventservice = null;
	}

	// This method is called whenever a new CD is came to the network.
	// addDevice method gets the properties of the new CD and then registers
	// the service in the osgi framework.
	@Override
	synchronized public void addDevice(String uuid,
			@SuppressWarnings("hiding") RootDevice deviceinfo) {
		if (devices.get(uuid) != null) {
			return;
		}
		this.deviceinfo = deviceinfo;
		String[] childrenUDN = null;
        Dictionary<String, Object> props = new Hashtable<String, Object>();
		RootDevice devinfo = deviceinfo.getDevice();
		RootDevice[] embdevices = devinfo.getEmbededDevices();
		parentUDN = devinfo.getUDN();
		if (parentUDN == null) {
			return;
		}
		if (devices.get(parentUDN) != null) {
			return;
		}
		if (embdevices != null) {
			if (embdevices.length > 0) {
				childrenUDN = new String[embdevices.length];
				for (int i = 0; i < embdevices.length; i++) {
					childrenUDN[i] = embdevices[i].getUDN();
				}
			}
		}
		if (devinfo != null) {
			UPnPDeviceImpl upnpdevice = null;
			String udn = devinfo.getUDN();
			props = getDeviceProps(props, devinfo);
			if (childrenUDN != null) {
				props.put(UPnPDevice.CHILDREN_UDN, childrenUDN);
			}
			try {
				upnpdevice = new UPnPDeviceImpl(this, devinfo, props, bc);
				System.out.println("REGISTERING UPnP DEVICE");
				ServiceRegistration<UPnPDevice> sr = bc.registerService(
						UPnPDevice.class,
						upnpdevice,
						props);
				servicerefs.put(udn, sr);
			}
			catch (Exception e) {
				System.out.println(e.getMessage());
			}
			if (upnpdevice != null) {
				devices.put(udn, upnpdevice);
			}
		}
		if (embdevices != null) {
			if (embdevices.length > 0) {
				regEmbedded(embdevices);
			}
		}
	}

	// This method is called whenever a embeded device is found from the CD.
	// regEmbedded method gets the properties of the new embeded device and then
	// registers
	// the service in the osgi framework.
	private void regEmbedded(RootDevice[] sembdevices) {
        Dictionary<String, Object> props = new Hashtable<String, Object>();
		for (int i = 0; i < sembdevices.length; i++) {
			UPnPDeviceImpl upnpdevice = null;
			String euuid = sembdevices[i].getUDN();
			String[] childUDN = null;
			RootDevice[] embdevices111 = sembdevices[i].getEmbededDevices();
			if (devices.get(euuid) == null) {
				if (embdevices111 != null) {
					if (embdevices111.length > 0) {
						childUDN = new String[embdevices111.length];
						for (int em = 0; em < embdevices111.length; em++) {
							childUDN[em] = embdevices111[em].getUDN();
						}
						props.put(UPnPDevice.CHILDREN_UDN, childUDN);
					}
				}
				props = getDeviceProps(props, sembdevices[i]);
				props.put(UPnPDevice.PARENT_UDN, parentUDN);
				try {
					upnpdevice = new UPnPDeviceImpl(this, sembdevices[i],
							props, bc);
					System.out.println("REGISTERING Embedded UPnP DEVICE");
					ServiceRegistration<UPnPDevice> sr = bc.registerService(
							UPnPDevice.class, upnpdevice,
							props);
					servicerefs.put(euuid, sr);
				}
				catch (Exception e) {
					System.out.println(e.getMessage());
				}
				devices.put(euuid, upnpdevice);
			}
			if (embdevices111 != null) {
				if (embdevices111.length > 0) {
					regEmbedded(embdevices111);
				}
			}
		}
	}

	// This method is called to get all the device properties.
    Dictionary<String, Object> getDeviceProps(Dictionary<String, Object> props, RootDevice devinfo) {
		props.put("DEVICE_CATEGORY", "UPnP");
		if (devinfo.getUDN() != null) {
			props.put(UPnPDevice.UDN, devinfo.getUDN());
			props.put(UPnPDevice.ID, devinfo.getUDN());
		}
		if (devinfo.getDeviceType() != null) {
			props.put(UPnPDevice.TYPE, devinfo.getDeviceType());
		}
		if (devinfo.getManufacturer() != null) {
			props.put(UPnPDevice.MANUFACTURER, devinfo.getManufacturer());
		}
		if (devinfo.getModelName() != null) {
			props.put(UPnPDevice.MODEL_NAME, devinfo.getModelName());
		}
		if (devinfo.getFriendlyName() != null) {
			props.put(UPnPDevice.FRIENDLY_NAME, devinfo.getFriendlyName());
		}
		if (devinfo.getManufacturerURL() != null) {
			props
					.put(UPnPDevice.MANUFACTURER_URL, devinfo
							.getManufacturerURL());
		}
		if (devinfo.getModelDescription() != null) {
			props.put(UPnPDevice.MODEL_DESCRIPTION, devinfo
					.getModelDescription());
		}
		if (devinfo.getModelNumber() != null) {
			props.put(UPnPDevice.MODEL_NUMBER, devinfo.getModelNumber());
		}
		if (devinfo.getModelURL() != null) {
			props.put(UPnPDevice.MODEL_URL, devinfo.getModelURL());
		}
		if (devinfo.getSerialNumber() != null) {
			props.put(UPnPDevice.SERIAL_NUMBER, devinfo.getSerialNumber());
		}
		if (devinfo.getUPC() != null) {
			props.put(UPnPDevice.UPC, devinfo.getUPC());
		}
		if (devinfo.getPresentationURL() != null) {
			props
					.put(UPnPDevice.PRESENTATION_URL, devinfo
							.getPresentationURL());
		}
		return props;
	}

	// This method is called whenever a device is removed from the network to
	// remove the device from the osgi framework.
	@Override
	synchronized public void removeDevice(String uuid) {
		try {
			if (servicerefs.get(uuid) != null) {
				ServiceRegistration<UPnPDevice> sreg = servicerefs
						.get(uuid);
				if (sreg != null) {
					UPnPDevice rootdev = devices.get(uuid);
					if (rootdev != null) {
						Dictionary<String,Object> props = rootdev
								.getDescriptions("en");
						String[] childUDN = (String[]) props
								.get(UPnPDevice.CHILDREN_UDN);
						if (childUDN != null) {
							for (int i = 0; i < childUDN.length; i++) {
								if (!childUDN[i].equals(uuid)) {
									removeDevice(childUDN[i]);
								}
							}
						}
						UPnPDeviceImpl dev = devices.get(uuid);
						dev.unsubscribe();
						System.out.println("UNREGISTERING UPnP DEVICE");
						sreg.unregister();
						dev.release();
						servicerefs.remove(uuid);
						devices.remove(uuid);
					}
				}
			}
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
}
