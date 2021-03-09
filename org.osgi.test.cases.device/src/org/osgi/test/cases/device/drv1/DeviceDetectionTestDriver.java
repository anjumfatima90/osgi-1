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
package org.osgi.test.cases.device.drv1;

import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.device.Device;
import org.osgi.service.device.Driver;
import org.osgi.test.cases.device.tbc.TestBundleControl;

/**
 * A common driver implementation. It is used in the device detection test and
 * will match only to some of the devices - those that are intended to have
 * drivers.
 * 
 * @author ProSyst
 * @version 1.0
 */
public class DeviceDetectionTestDriver implements Driver, BundleActivator {
	private BundleContext		bc					= null;
	private ServiceRegistration<Driver>			driverRegistration	= null;
	private TestBundleControl	master				= null;
	private ServiceReference<TestBundleControl>	masterRef			= null;
	/*
	 * !!!FIX!!! The device service this driver is attaching to The driver must
	 * hold this until it is uninstalled (or the device is uninstalled) else the
	 * device will be considered idle and will be included in any searches.
	 */
	@SuppressWarnings("unused")
	private Device				device				= null;
	private ServiceReference< ? >				deviceRef			= null;

	/**
	 * The start method of the activator of this Driver bundle. As required
	 * registers the Driver service synchronously with the bundle start.
	 */
	public void start(BundleContext bc) {
		this.bc = bc;
		/* get the master of this test case */
		masterRef = bc.getServiceReference(TestBundleControl.class);
		master = bc.getService(masterRef);
		log("starting bundle");
		Hashtable<String,Object> h = new Hashtable<>();
		h.put("DRIVER_ID", "org.osgi.test.device.basicDriver");
		log("registering service");
		driverRegistration = bc.registerService(
				Driver.class, this, h);
	}

	/**
	 * Unregisters the Driver service and ungets the master service
	 */
	public void stop(BundleContext bc) {
		bc.ungetService(deviceRef);
		bc.ungetService(masterRef);
		driverRegistration.unregister();
	}

	/**
	 * Checks if this dirver matches to the Device service reference passed.
	 * 
	 * @param reference service reference to the registred device.
	 * @return 255 if the Driver is intended to have a driver - thouse are
	 *         drivers with IDs "basicDevice", "basicDevice_noDevice",
	 *         "basicDevice_noCategory"
	 */
	public int match(ServiceReference< ? > reference) {
		String driverID = (String) reference.getProperty("deviceID");
		if ("basicDevice".equals(driverID)
				|| "basicDevice_noDevice".equals(driverID)
				|| "basicDevice_noCategory".equals(driverID)) {
			return 255;
		}
		else
			if ("basicDevice_noDriver".equals(driverID)
					|| "basicDevice_noDevice_noDriver".equals(driverID)) {
				master.setMessage(TestBundleControl.MESSAGE_OK);
				return Device.MATCH_NONE;
			}
			else {
				return Device.MATCH_NONE;
			}
	}

	/**
	 * Basic implementation of a driver. Attaches successfully every time but
	 * attaching to a wrong device then sets the message in the masser to
	 * TestBundleControl.MESSAGE_ERROR to indicate that there is a wrong
	 * attachment.
	 * 
	 * @param reference service reference to the registred device.
	 * @returns null
	 */
	public String attach(ServiceReference< ? > reference) throws Exception {
		String deviceID = (String) reference.getProperty("deviceID");
		if ("basicDevice".equals(deviceID)
				|| "basicDevice_noDevice".equals(deviceID)
				|| "basicDevice_noCategory".equals(deviceID)) {
			// log("<drv1 basicdriver> attaching basic device"); //***
			master.setMessage(TestBundleControl.MESSAGE_OK);
			device = (Device) bc.getService(reference);
			deviceRef = reference;
		}
		else {
			log(deviceID + " attached! Error");
			master.setMessage(TestBundleControl.MESSAGE_ERROR);
		}
		bc.ungetService(masterRef);
		return null;
	}

	/**
	 * Logs the results in the master's log.
	 */
	private void log(String toLog) {
		master.log("device detection driver", toLog);
	}
}
