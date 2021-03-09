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
package org.osgi.test.cases.device.drv7;

import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.device.Device;
import org.osgi.service.device.Driver;
import org.osgi.test.cases.device.tbc.TestBundleControl;

/**
 * Will match and should attach the standalone test device only
 * 
 * @author Vasil Panushev
 * @version 1.0
 */
public class StandAloneDriver implements BundleActivator, Driver {
	private BundleContext		bc					= null;
	private ServiceReference< ? >				deviceRef			= null;
	private ServiceRegistration<Driver>			driverRegistration	= null;
	private ServiceReference<TestBundleControl>	masterRef			= null;
	private TestBundleControl	master				= null;
	@SuppressWarnings("unused")
	private Object				device				= null;

	/**
	 * Will register the standalone driver
	 * 
	 * @param bc bundle context received from the fw
	 * @exception Exception
	 */
	public void start(BundleContext bc) throws Exception {
		this.bc = bc;
		/* get the master of this test case */
		masterRef = bc.getServiceReference(TestBundleControl.class);
		master = bc.getService(masterRef);
		/* register the driver service */
		Hashtable<String,Object> h = new Hashtable<>();
		h.put("DRIVER_ID", "sadriver");
		h.put(org.osgi.framework.Constants.SERVICE_RANKING, Integer.valueOf(4242));
		driverRegistration = bc.registerService(
				Driver.class, this, h);
	}

	/**
	 * unregisters the driver
	 * 
	 * @param bc
	 * @exception Exception
	 */
	public void stop(BundleContext bc) throws Exception {
		if (deviceRef != null) {
			bc.ungetService(deviceRef);
			deviceRef = null;
		}
		driverRegistration.unregister();
	}

	/**
	 * Will attach only to a device with ID
	 * 
	 * @param ref Reference to the device this driver will try to attach to
	 * @return null
	 * @exception Exception no way
	 */
	
	static int n = 0;
	
	public String attach(ServiceReference< ? > ref) throws Exception {
		if ("standalone driver test device".equals(ref.getProperty("deviceID"))) {
			deviceRef = ref;
			device = bc.getService(deviceRef); // to catch the device
			log("attaching to standalone device" );
			master.setMessage(TestBundleControl.MESSAGE_OK);
		}
		else {
			log("attaching to: " + ref.getProperty("deviceID"));
		}
		return null;
	}

	/**
	 * 
	 * 
	 * @param ref
	 * @return
	 * @exception Exception
	 */
	public int match(ServiceReference< ? > ref) throws Exception {
		System.out.println("match called !!!!!!!!!!!!!!!!!!!! "
				+ ref.getProperty("deviceID"));
		if ("standalone driver test device".equals(ref.getProperty("deviceID"))) {
			return 255;
		}
		else {
			return Device.MATCH_NONE;
		}
	}

	private void log(String toLog) {
		master.log("sadriver", toLog);
	}
}
