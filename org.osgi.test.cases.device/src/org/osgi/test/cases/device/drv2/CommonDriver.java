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
package org.osgi.test.cases.device.drv2;

import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.device.Driver;
import org.osgi.test.cases.device.tbc.TestBundleControl;

/**
 * A common driver implementation - a driver that will be matched with other
 * drivers but will lose the bidding (through selector or through the default
 * matching algorythm). Its SERVICE_RANKING properti is 42
 * 
 * @author ProSyst
 * @version 1.0
 */
public class CommonDriver implements Driver, BundleActivator {
	private ServiceRegistration<Driver>			driverRegistration	= null;
	private BundleContext		bc					= null;
	private TestBundleControl	master				= null;
	private ServiceReference<TestBundleControl>	masterRef			= null;

	/**
	 * The start method of the activator of the CommonDriver bundle. As required
	 * registers the Driver service synchronously with the bundle start.
	 */
	public void start(BundleContext bc) {
		this.bc = bc;
		/* get the master of this test case */
		masterRef = bc.getServiceReference(TestBundleControl.class);
		master = bc.getService(masterRef);
		log("starting driver bundle");
		Hashtable<String,Object> h = new Hashtable<>();
		h.put("DRIVER_ID", "common_driver");
		h.put(org.osgi.framework.Constants.SERVICE_RANKING, Integer.valueOf(42));
		log("registering service");
		driverRegistration = bc.registerService(
				Driver.class, this, h);
	}

	/**
	 * unregisters the Driver service
	 */
	public void stop(BundleContext bc) {
		driverRegistration.unregister();
	}

	/**
	 * Checks if this dirver matches to the Device service reference passed.
	 * 
	 * @param reference service reference to the registred device.
	 * @returns 255
	 */
	public int match(ServiceReference< ? > reference) {
		return 255;
	}

	/**
	 * Basic implementation of a driver. Attaches successfully every time but
	 * sets the message in the master test case to
	 * TestBundleControl.MESSAGE_ERROR because this driver should not be
	 * attached - there is always other driver that should win.
	 * 
	 * @param reference service reference to the registred device. This dirver
	 *        attachtes always successfully
	 * @returns null
	 */
	public String attach(ServiceReference< ? > reference) throws Exception {
		log("attaching to " + (String) reference.getProperty("deviceID"));
		master.setMessage(TestBundleControl.MESSAGE_ERROR);
		bc.ungetService(masterRef);
		return null;
	}

	/**
	 * Logs the results i the master's log as remarks.
	 */
	public void log(String toLog) {
		//  	master.logRemark("common driver", toLog);
	}
}
