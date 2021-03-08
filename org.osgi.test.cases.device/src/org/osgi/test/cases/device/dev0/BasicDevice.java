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
package org.osgi.test.cases.device.dev0;

import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.device.Device;
import org.osgi.test.cases.device.tbc.TestBundleControl;

/**
 * This class is used as an activator fo the device bundle - the common device
 * that is used by all subtests.
 * 
 * @author ProSyst
 * @version 1.0
 */
public class BasicDevice implements BundleActivator, Device {
	private ServiceRegistration< ? >			deviceSR	= null;
	private String[]			category	= {"test"};
	private TestBundleControl	master		= null;
	private ServiceReference<TestBundleControl>	masterRef	= null;

	/**
	 * The start method of the activator of the device bundle. Registers
	 * different devices depending on the device.test.mode system property.
	 * Following values of this property are recognized: 0 - registers a device
	 * that implements org.osgi.service.device.Device and there is matching
	 * driver for it 1 - registers a device that doesn't implement
	 * org.osgi.service.device.Device and there is matching driver 2 - registers
	 * a device that implements org.osgi.service.device.Device and there is NO
	 * matching driver for it 3 - registers a device that dooesn't implement
	 * org.osgi.service.device.Device and there is NO matching driver for it 4 -
	 * registers a device that implements org.osgi.service.device.Driver but
	 * does not set DEVICE_CATEGOR property 100 - registers a general device
	 * 
	 * @param bc the bundle context of this bundle
	 */
	public void start(BundleContext bc) {
		// get the master of this test case - it is used for logging
		masterRef = bc.getServiceReference(TestBundleControl.class);
		master = bc.getService(masterRef);
		// org.osgi.service.device.Device with matching driver
		Hashtable<String,Object> h = new Hashtable<>();
		h.put("deviceID", "basicDevice");
		h.put("DEVICE_CATEGORY", category);
		h.put("device.test", Boolean.TRUE);
		log("Registering device that implements Device and there is a driver for it");
		deviceSR = bc.registerService("org.osgi.service.device.Device", this,
				h);
	}

	/**
	 * Unregisters the device service
	 * 
	 * @param bc the bundle context of this bundle
	 */
	public void stop(BundleContext bc) {
		deviceSR.unregister();
		bc.ungetService(masterRef);
	}

	/**
	 * Should be called when no dirver is found for this device - only for
	 * devices that implement org.osgi.service.device.Device.
	 */
	public void noDriverFound() {
		master.setNoDriverFoundCalled(true);
	}

	/* Calls the log method of the master test case */
	private void log(String toLog) {
		master.log("basic device", toLog);
	}
}
